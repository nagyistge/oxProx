package org.ox.oxprox.ws;

import com.google.inject.Inject;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.conf.DiscoverEntityBuilder;
import org.ox.oxprox.model.ws.DiscoveryEntity;
import org.ox.oxprox.service.ErrorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/03/2014
 */
@Path("/rest/.well-known")
public class DiscoveryWS {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryWS.class);

    @Inject
    Configuration conf;
    @Inject
    ErrorService errorService;

    @GET
    @Path("/openid-configuration")
    @Produces({MediaType.TEXT_PLAIN})
    public Response requestDiscoveryGet(
            @Context HttpServletRequest httpRequest,
            @Context SecurityContext securityContext) {
        return discovery(httpRequest, securityContext);
    }

    @POST
    @Path("/openid-configuration")
    @Produces({MediaType.TEXT_PLAIN})
    public Response requestDiscoveryPost(
            @Context HttpServletRequest httpRequest,
            @Context SecurityContext securityContext) {
        return discovery(httpRequest, securityContext);
    }

    private Response discovery(HttpServletRequest httpRequest, SecurityContext securityContext) {
        try {
            final String entity = Util.asPrettyJson(getDiscoveryEntity());
            return Response.ok(entity, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal Server Error").build());
        }
    }

    public DiscoveryEntity getDiscoveryEntity() {
        return new DiscoverEntityBuilder(conf).build();
    }
}
