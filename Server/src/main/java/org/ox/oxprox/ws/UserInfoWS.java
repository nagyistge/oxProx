package org.ox.oxprox.ws;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.ox.oxprox.ldap.oxProxClient;
import org.ox.oxprox.service.AccessTokenMappingService;
import org.ox.oxprox.service.ClientService;
import org.ox.oxprox.service.OicDiscoveryService;
import org.ox.oxprox.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.client.OpenIdConfigurationResponse;
import org.xdi.oxauth.client.UserInfoClient;
import org.xdi.oxauth.client.UserInfoRequest;
import org.xdi.oxauth.client.UserInfoResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 16/04/2014
 */

@Path("/rest")
public class UserInfoWS {

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoWS.class);

    @Inject
    OicDiscoveryService discoveryService;
    @Inject
    AccessTokenMappingService accessTokenService;
    @Inject
    ClientService clientService;

    @GET
    @Path("/userinfo")
    Response requestUserInfoGet(
            @QueryParam("access_token") String accessToken,
            @HeaderParam("Authorization") String authorization,
            @Context HttpServletRequest httpRequest,
            @Context SecurityContext securityContext) {
        final SessionService sessionService = new SessionService(httpRequest.getSession());
        return requestUserInfo(accessToken, sessionService);
    }

    @POST
    @Path("/userinfo")
    Response requestUserInfoPost(
            @FormParam("access_token") String accessToken,
            @HeaderParam("Authorization") String authorization,
            @Context HttpServletRequest httpRequest,
            @Context SecurityContext securityContext) {
        final SessionService sessionService = new SessionService(httpRequest.getSession());
        return requestUserInfo(accessToken, sessionService);
    }

    public Response requestUserInfo(String accessToken, SessionService sessionService) {
        try {
            final OpenIdConfigurationResponse discoveryResponse = discoveryService.getDiscoveryResponseByAmHost(sessionService.getOpDomain());

            UserInfoRequest request = new UserInfoRequest(accessToken);
            UserInfoClient client = new UserInfoClient(discoveryResponse.getUserInfoEndpoint());
            client.setRequest(request);
            UserInfoResponse response = client.exec();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return Response.status(Response.Status.OK).entity(createEntity(response, accessToken)).build();
            } else {
                // return error "as is"
                return Response.status(response.getStatus()).entity(response.getEntity()).build();
            }
        } catch (JSONException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        LOG.trace("Internal error occurred.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private String createEntity(UserInfoResponse response, String accessToken) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        final Map<String, List<String>> claims = response.getClaims();
        if (claims != null && !claims.isEmpty()) {

            final String clientId = accessTokenService.getClientId(accessToken);
            final oxProxClient client = clientService.getClient(clientId);

            for (Map.Entry<String, List<String>> entry : claims.entrySet()) {
                final String claimName = remapClaim(entry.getKey(), client);
                JSONArray value = new JSONArray();
                value.put(entry.getValue());
                jsonObj.put(claimName, value);
            }
        }
        return jsonObj.toString(4).replace("\\/", "/");
    }

    private String remapClaim(String opClaimName, oxProxClient client) {
        if (client != null) {
            final Map<String, String> map = client.getClaimMappingMap();
            if (map != null) {
                final String rpClaimName = map.get(opClaimName);
                if (StringUtils.isNotBlank(rpClaimName)) {
                    return rpClaimName;
                }
            }
        }
        return opClaimName;
    }
}
