package eu.cessda.cvs.web.rest.domain;

import java.io.Serializable;

public class SimpleResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String status;
    private String message;

    public SimpleResponse() {
    }

    public SimpleResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
