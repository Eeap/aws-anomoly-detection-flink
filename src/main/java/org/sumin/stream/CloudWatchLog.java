package org.sumin.stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class CloudWatchLog implements Serializable {

    private String messageType;
    private String owner;
    private String logGroup;
    private String logStream;
    private List<String> subscriptionFilters;
    private List<LogEvent> logEvents;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LogEvent {
        public String id;
        public long timestamp;
        public String message;

    }
}
