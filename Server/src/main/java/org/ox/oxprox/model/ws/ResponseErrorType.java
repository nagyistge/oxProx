package org.ox.oxprox.model.ws;

import org.xdi.oxauth.model.token.TokenErrorResponseType;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 25/03/2014
 */

public enum ResponseErrorType {
    NO_DISCOVERY("no_discovery", "No discovery response from OP."),
    INVALID_CLIENT("invalid_client", "Unknown client."),
    NO_MAPPING_TO_OP_CLIENT("no_op_client_mapping", "Client does not contain mapping to OP client."),
    NO_AUTHORIZATION_HEADER("invalid_request", "Request does not contain 'Authorization' header"),

    // Token endpoint
    INVALID_GRANT(TokenErrorResponseType.INVALID_GRANT.getParameter(), "Grant type is unsupported, or is otherwise malformed."),
    NOT_SUPPORTED_GRANT("not_supported_grant", "Grant type is not supported or otherwise malformed."),;

    private final String error;
    private final String description;

    private ResponseErrorType(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getError() {
        return error;
    }
}
