package com.experoinc.janusgraph.analytics.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class AnalyticsConfiguration extends Configuration {
    @NotEmpty
    private String graphConfigPath;

    @JsonProperty
    public String getGraphConfigPath() {
        return graphConfigPath;
    }

    @JsonProperty
    public void setTemplate(String graphConfigPath) {
        this.graphConfigPath = graphConfigPath;
    }
}