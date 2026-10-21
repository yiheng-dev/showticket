package com.showticket.selection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ticket.selection")
public class SelectionProperties {
    private Integer maxSelections;
}
