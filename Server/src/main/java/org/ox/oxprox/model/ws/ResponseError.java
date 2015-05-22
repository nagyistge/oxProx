package org.ox.oxprox.model.ws;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 25/03/2014
 */

public class ResponseError implements Serializable {

    @JsonProperty(value = "error")
    private String error;
    @JsonProperty(value = "error_description")
    private String errorDescription;

    public ResponseError() {
    }

    public ResponseError(ResponseErrorType p_errorType) {
        this(p_errorType.getError(), p_errorType.getDescription());
    }

    public ResponseError(String p_error, String p_errorDescription) {
        error = p_error;
        errorDescription = p_errorDescription;
    }

    public String getError() {
        return error;
    }

    public void setError(String p_error) {
        error = p_error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String p_errorDescription) {
        errorDescription = p_errorDescription;
    }

    /**
     * Returns string representation of object
     *
     * @return string representation of object
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ResponseError");
        sb.append("{error=").append(error);
        sb.append(", errorDescription='").append(errorDescription).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
