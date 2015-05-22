package org.ox.oxprox.ws;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.ox.oxprox.Utils;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.ldap.oxProxClient;
import org.ox.oxprox.model.ws.ResponseErrorType;
import org.ox.oxprox.service.ClientService;
import org.ox.oxprox.service.ErrorService;
import org.ox.oxprox.service.HttpService;
import org.ox.oxprox.service.OicDiscoveryService;
import org.ox.oxprox.service.SessionService;
import org.python.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.client.AuthorizationResponse;
import org.xdi.oxauth.client.OpenIdConfigurationResponse;
import org.xdi.oxauth.model.common.Prompt;
import org.xdi.oxauth.model.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 27/02/2014
 */

@Path("/rest")
public class AuthorizationWS {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationWS.class);

    @Inject
    Configuration conf;
    @Inject
    OicDiscoveryService discoveryService;
    @Inject
    ErrorService errorService;
    @Inject
    ClientService clientService;
    @Inject
    HttpService httpService;

    @GET
    @Path("/authorize")
    @Produces({MediaType.TEXT_PLAIN})
    public Response requestAuthorizationGet(
            @Context HttpServletRequest httpRequest,
            @Context SecurityContext securityContext) {
        final SessionService sessionService = new SessionService(httpRequest.getSession());
        final String authorizationHeader = httpRequest.getHeader("Authorization");
        return requestAuthorization(sessionService, authorizationHeader);
    }

    @POST
    @Path("/authorize")
    @Produces({MediaType.TEXT_PLAIN})
    public Response requestAuthorizationPost(
            @Context HttpServletRequest httpRequest,
            @Context SecurityContext securityContext) {
        final SessionService sessionService = new SessionService(httpRequest.getSession());
        final String authorizationHeader = httpRequest.getHeader("Authorization");
        return requestAuthorization(sessionService, authorizationHeader);
    }

    public Response requestAuthorization(SessionService sessionService, String authorizationHeader) {
        try {
            final String opDomain = sessionService.getOpDomain();
            final Map<String, String[]> parameterMap = sessionService.getParameterMap();

            LOG.debug("Attempting to request authorization opDomain = {}, parameterMap = {}", opDomain, Utils.mapAsString(parameterMap));

            final OpenIdConfigurationResponse discoveryResponse = discoveryService.getDiscoveryResponseByAmHost(opDomain);
            final oxProxClient oxProxClient = getClient(parameterMap);
            final String opClientId = clientService.getOpClient(sessionService.getOpDomain(), oxProxClient).getId();

            if (opClientId == null) {
                LOG.warn("Failed to resolve OP client.");
                throw new WebApplicationException(errorService.response(Response.Status.BAD_REQUEST, ResponseErrorType.NO_MAPPING_TO_OP_CLIENT));
            }
            LOG.trace("Resolved op client from proxy client: {}, (op: {}) to op client: {}", new Object[]{oxProxClient.getClientId(), sessionService.getOpDomain(), opClientId});
            sessionService.setOpClientId(opClientId);

            final String[] prompts = sessionService.getParameterMap().get("prompt");

            // direct WS call
            if (httpService.contains(prompts, Prompt.NONE.getParamName())) {
                return directWsCall(parameterMap, discoveryResponse, oxProxClient, opClientId, authorizationHeader);
            } else {
                // redirect user for authentication & authorization
                return redirectEndUserForAuth(parameterMap, discoveryResponse, oxProxClient, opClientId);
            }
        } catch (Exception e) {
            if (e instanceof WebApplicationException) {
                throw (WebApplicationException) e;
            }
            LOG.error(e.getMessage(), e);
        }

        LOG.trace("Internal error occurred.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private Response directWsCall(Map<String, String[]> parameterMap, OpenIdConfigurationResponse discoveryResponse, oxProxClient oxProxClient, String opClientId, String authorizationHeader) throws Exception {
        final ClientRequest clientRequest = new ClientRequest(discoveryResponse.getAuthorizationEndpoint());
        clientRequest.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
        clientRequest.setHttpMethod(HttpMethod.POST);
        clientRequest.header("Authorization", authorizationHeader);

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            final String parameterName = entry.getKey();
            if (parameterName.equals("redirect_uri")) {
                clientRequest.formParameter(parameterName, conf.getRedirectEndpoint());
            } else if (parameterName.equals("client_id")) {
                clientRequest.formParameter(parameterName, opClientId);
            } else if (parameterName.equals("scope")) {
                final List<String> scopesToSent = Lists.newArrayList();
                final List<String> rpScopes = prepareRpScopes(entry.getValue());
                for (String rpScope : rpScopes) {
                    final String opScope = oxProxClient.getScopeMappingMap().get(rpScope);
                    final String currentScope;
                    if (!Strings.isNullOrEmpty(opScope)) {
                        LOG.trace("Remapped scope, clientId: {}, rpScope: {}, opScope: {}", new Object[]{oxProxClient.getClientId(), rpScope, opScope});
                        currentScope = opScope;
                    } else {
                        currentScope = rpScope;
                    }
                    scopesToSent.add(currentScope);
                }
                clientRequest.formParameter(parameterName, httpService.formParameterValue(scopesToSent));

            } else { // DEFAULT
                clientRequest.formParameter(parameterName, httpService.formParameterValue(entry.getValue()));
            }
        }
        final ClientResponse<String> postResponse = clientRequest.post(String.class);
        LOG.trace("Post response on direct WS call: {}", postResponse);

        try {
            // re-map scopes if response has location
            AuthorizationResponse parsedResponse = new AuthorizationResponse(postResponse);
            if (Util.allNotBlank(parsedResponse.getCode(), parsedResponse.getScope(), postResponse.getLocation().getHref())) {
                final String rpRedirectUri = httpService.getRedirectUriParameter(parameterMap);
                final StringBuilder locationUri = new StringBuilder(rpRedirectUri);
                locationUri.append("?code=").append(encode(parsedResponse.getCode())).
                        append("&session_id=").append(encode(parsedResponse.getSessionId())).
                        append("&scope=").append(encode(rpScopes(oxProxClient, parsedResponse.getScope()))).
                        append("&state=").append(encode(parsedResponse.getState())).
                        append("&access_token=").append(encode(parsedResponse.getAccessToken())).
                        append("&id_token=").append(encode(parsedResponse.getIdToken())).
                        append("&token_type=").append(parsedResponse.getTokenType() != null ? parsedResponse.getTokenType().toString() : "");
                final String locationAsString = locationUri.toString();
                return Response.status(postResponse.getStatus()).entity(postResponse.getEntity()).
                        location(new URI(locationAsString)).build();
            }
        } catch (Exception e) {
            //ignore : here we may have any response, we are interested in response with location ONLY
            LOG.trace(e.getMessage(), e);
        }

        return postResponse;
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(Strings.nullToEmpty(value), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
            return "";
        }
    }

    public static String rpScopes(oxProxClient oxProxClient, String scopeString) {
        final Map<String, String> rpToOp = oxProxClient.getScopeMappingMap();
        final List<String> opScopes = Util.splittedStringAsList(scopeString, " ");
        final List<String> resultScopes = Lists.newArrayList();
        for (Map.Entry<String, String> entry : rpToOp.entrySet()) {
            for (String opScope : opScopes) {
                if (!resultScopes.contains(opScope)) {
                    if (entry.getValue().equalsIgnoreCase(opScope)) {
                        resultScopes.add(entry.getKey());
                    } else {
                        resultScopes.add(opScope);
                    }
                }
            }
        }
        return Util.listAsString(resultScopes);
    }

    private List<String> prepareRpScopes(String[] value) {
        if (value != null) {
            if (value.length == 1 && value[0].contains(" ")) {
                return Util.splittedStringAsList(value[0], " ");
            } else {
                return Arrays.asList(value);
            }
        }

        return Collections.emptyList();
    }

    private Response redirectEndUserForAuth(Map<String, String[]> parameterMap, OpenIdConfigurationResponse discoveryResponse, oxProxClient oxProxClient, String opClientId) throws URISyntaxException {
        final StringBuilder parametersString = new StringBuilder();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            appendEntry(parametersString, entry, opClientId, oxProxClient);
        }

        final String authorizationUrl = discoveryResponse.getAuthorizationEndpoint() + parametersString.toString();
        LOG.trace("Authorize to : {}", authorizationUrl);
        return Response.seeOther(new URI(authorizationUrl)).build();
    }

    private oxProxClient getClient(Map parameterMap) {
        final Object clientId = parameterMap.get("client_id");
        if (clientId instanceof String && StringUtils.isNotBlank((String) clientId)) {
            return clientService.getClient((String) clientId);
        } else if (clientId instanceof String[]) {
            final String[] clientIds = (String[]) clientId;
            if (clientIds.length > 0 && StringUtils.isNotBlank(clientIds[0])) {
                final oxProxClient client = clientService.getClient(clientIds[0]);
                if (client != null) {
                    return client;
                }
            }
        }
        LOG.warn("Failed to resolve oxProx client.");
        throw new WebApplicationException(errorService.response(Response.Status.BAD_REQUEST, ResponseErrorType.INVALID_CLIENT));
    }

    private void appendEntry(StringBuilder parameters, Map.Entry<String, ?> entry, String opClient, oxProxClient client) {
        try {
            final String parameterName = entry.getKey();
            final Object parameterValue = entry.getValue();

            if (!Strings.isNullOrEmpty(parameterName) && parameterValue != null) {
                final boolean hasParameters = parameters.indexOf("?") != -1;
                parameters.append(hasParameters ? "&" : "?");
                parameters.append(parameterName).append("=");
                appendValue(parameters, parameterName, parameterValue, opClient, client);
            } else {
                LOG.error("parameter name ({}) or value ({}) is blank.", parameterName, parameterValue);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void appendValue(StringBuilder parameters, String parameterName, Object parameterValue, String opClientId, oxProxClient client) throws UnsupportedEncodingException {
        if (parameterName.equals("redirect_uri")) {
            parameters.append(URLEncoder.encode(conf.getRedirectEndpoint(), "UTF-8"));
            return;
        }

        if (parameterName.equals("client_id")) {
            parameters.append(URLEncoder.encode(opClientId, "UTF-8"));
            return;
        }

        if (parameterName.equals("scope") && parameterValue instanceof String[] && ((String[]) parameterValue).length > 0) {
            String[] rpScopes = StringUtils.split(((String[]) parameterValue)[0], " ");
            for (String rpScope : rpScopes) {
                final String opScope = client.getScopeMappingMap().get(rpScope);
                final String currentScope;
                if (!Strings.isNullOrEmpty(opScope)) {
                    LOG.trace("Remapped scope, clientId: {}, rpScope: {}, opScope: {}", new Object[]{client.getClientId(), rpScope, opScope});
                    currentScope = opScope;
                } else {
                    currentScope = rpScope;
                }

                parameters.append(URLEncoder.encode(currentScope, "UTF-8")).append(URLEncoder.encode(" ", "UTF-8"));
            }
            return;
        }

        if (parameterValue instanceof String[]) {
            String[] values = (String[]) parameterValue;
            for (String value : values) {
                parameters.append(URLEncoder.encode(value, "UTF-8")).append(URLEncoder.encode(" ", "UTF-8"));
            }
        } else if (parameterValue instanceof String) {
            parameters.append(parameterValue);
        }
    }
}
