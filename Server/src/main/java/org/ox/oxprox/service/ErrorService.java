package org.ox.oxprox.service;

import org.ox.oxprox.model.server.ModelUtils;
import org.ox.oxprox.model.ws.ResponseError;
import org.ox.oxprox.model.ws.ResponseErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 24/12/2013
 */

public class ErrorService {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorService.class);

    public String entity(ResponseErrorType type) {
        try {
            return ModelUtils.asJsonSilently(new ResponseError(type));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return "";
        }
    }

    public Response response(int status, ResponseErrorType type) {
        return Response.status(status).entity(entity(type)).build();
    }

    public Response response(Response.Status status, ResponseErrorType type) {
        return Response.status(status).entity(entity(type)).build();
    }
}
