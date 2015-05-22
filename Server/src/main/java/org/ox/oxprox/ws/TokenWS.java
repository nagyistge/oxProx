package org.ox.oxprox.ws;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.ldap.AccessTokenMap;
import org.ox.oxprox.ldap.oxProxClient;
import org.ox.oxprox.model.gwt.ClientMapping;
import org.ox.oxprox.model.ws.ResponseErrorType;
import org.ox.oxprox.service.AccessTokenMappingService;
import org.ox.oxprox.service.ClientService;
import org.ox.oxprox.service.ErrorService;
import org.ox.oxprox.service.HttpService;
import org.ox.oxprox.service.OicDiscoveryService;
import org.ox.oxprox.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.client.OpenIdConfigurationResponse;
import org.xdi.oxauth.client.TokenClient;
import org.xdi.oxauth.client.TokenRequest;
import org.xdi.oxauth.client.TokenResponse;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.common.TokenType;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2014
 */

@Path("/rest")
public class TokenWS {

    private static final Logger LOG = LoggerFactory.getLogger(TokenWS.class);

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
    @Inject
    AccessTokenMappingService accessTokenService;

    @POST
    @Path("/token")
    @Produces({MediaType.APPLICATION_JSON})
    public Response requestAccessToken(
            @FormParam("grant_type") String grantType,
            @FormParam("code") String code,
            @FormParam("redirect_uri") String rpRedirectUri,
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("scope") String scope,
            @FormParam("assertion") String assertion,
            @FormParam("refresh_token") String refreshToken,
            @FormParam("oxauth_exchange_token") String oxAuthExchangeToken,
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret,
            @Context HttpServletRequest request,
            @Context SecurityContext sec) {
        final SessionService sessionService = new SessionService(request.getSession());
        final String authorizationHeader = request.getHeader("Authorization");
        return handleRequest(sessionService, grantType, code, rpRedirectUri, username, password, scope, assertion,
                refreshToken, oxAuthExchangeToken, clientId, clientSecret, authorizationHeader);
    }

    public Response handleRequest(SessionService sessionService, String grantType, String code, String rpRedirectUri, String username, String password,
                                  String scope, String assertion, String refreshToken, String oxAuthExchangeToken, String clientId, String clientSecret,
                                  String authorizationHeader) {
        try {
            final OpenIdConfigurationResponse discoveryResponse = discoveryService.getDiscoveryResponseByAmHost(sessionService.getOpDomain());

            GrantType gt = GrantType.fromString(grantType);
            if (gt != null) {
                if (gt == GrantType.AUTHORIZATION_CODE) {
                    final String oxProxClientId = httpService.parseBasicAuthorizationHeader(authorizationHeader).getFirst();
                    final oxProxClient oxProxClient = clientService.getClientWithException(oxProxClientId);
                    final ClientMapping.Client opClient = clientService.getOpClient(sessionService.getOpDomain(), oxProxClient);
                    TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE);
                    tokenRequest.setCode(code);
                    tokenRequest.setRedirectUri(conf.getRedirectEndpoint());
                    tokenRequest.setAuthUsername(opClient.getId());
                    tokenRequest.setAuthPassword(opClient.getSecret());
                    tokenRequest.setAuthenticationMethod(AuthenticationMethod.CLIENT_SECRET_BASIC);

                    TokenClient tokenClient = new TokenClient(discoveryResponse.getTokenEndpoint());
                    tokenClient.setRequest(tokenRequest);
                    TokenResponse tokenResponse = tokenClient.exec();
                    persistMapping(tokenResponse, oxProxClientId);
                    String entity = getJSonResponse(tokenResponse.getAccessToken(), tokenResponse.getTokenType(),
                            tokenResponse.getExpiresIn(), tokenResponse.getRefreshToken(), tokenResponse.getScope(), tokenResponse.getIdToken());
                    return Response.ok().entity(entity).build();
                } else {
                    return errorService.response(501, ResponseErrorType.NOT_SUPPORTED_GRANT);
                }
            } else {
                return errorService.response(501, ResponseErrorType.INVALID_GRANT);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.trace("Internal error occurred.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private void persistMapping(TokenResponse tokenResponse, String oxProxClientId) {
        final String accessToken = tokenResponse.getAccessToken();
        if (StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(oxProxClientId)) {

            Calendar calendar = Calendar.getInstance();

            final Date creationDate = calendar.getTime();
            calendar.add(Calendar.SECOND, tokenResponse.getExpiresIn() > 0 ? tokenResponse.getExpiresIn() : 1);
            final Date expirationDate = calendar.getTime();

            final AccessTokenMap accessTokenMap = new AccessTokenMap(accessToken, oxProxClientId);
            accessTokenMap.setCreationDate(creationDate);
            accessTokenMap.setExpirationDate(expirationDate);

            accessTokenService.persist(accessTokenMap);
        }
    }

    public static String getJSonResponse(String accessToken, TokenType tokenType,
                                         Integer expiresIn, String refreshToken, String scope,
                                         String idToken) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("access_token", accessToken); // Required
        jsonObj.put("token_type", tokenType.toString()); // Required
        if (expiresIn != null) { // Optional
            jsonObj.put("expires_in", expiresIn);
        }
        if (refreshToken != null) { // Optional
            jsonObj.put("refresh_token", refreshToken);
        }
        if (scope != null) { // Optional
            jsonObj.put("scope", scope);
        }
        if (idToken != null) {
            jsonObj.put("id_token", idToken);
        }
        return jsonObj.toString();
    }
}
