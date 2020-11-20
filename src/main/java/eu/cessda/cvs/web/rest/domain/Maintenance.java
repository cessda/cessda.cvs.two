package eu.cessda.cvs.web.rest.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Maintenance implements Serializable {
    private static final long serialVersionUID = 1L;
    private String output;
    private LocalDateTime timestamp;
    private String type;

    public Maintenance() {
    }

    public Maintenance(String output, String type) {
        this.output = output;
        this.timestamp = LocalDateTime.now();
        this.type = type;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
