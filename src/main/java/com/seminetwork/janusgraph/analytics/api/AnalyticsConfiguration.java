package com.seminetwork.janusgraph.analytics.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class AnalyticsConfiguration extends Configuration {
    @NotEmpty
    private String graphConfigPath;

    @NotEmpty
    private String etcdOrigin;

    @JsonProperty
    public String getGraphConfigPath() {
        return graphConfigPath;
    }

    @JsonProperty
    public String getEtcdOrigin() {
        return etcdOrigin;
    }

    @JsonProperty
    public void setTemplate(String graphConfigPath) {
        this.graphConfigPath = graphConfigPath;
        this.etcdOrigin = etcdOrigin;
    }
}