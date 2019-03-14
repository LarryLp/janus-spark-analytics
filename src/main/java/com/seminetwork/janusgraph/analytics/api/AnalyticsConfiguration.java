package com.seminetwork.janusgraph.analytics.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seminetwork.janusgraph.analytics.storage.EtcdStorageConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration jerseyClient;

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @Valid
    @NotNull
    private EtcdStorageConfiguration etcdConfiguration = new EtcdStorageConfiguration();

    @JsonProperty("etcdConfiguration")
    public EtcdStorageConfiguration getEtcdStorageConfiguration() {
        return etcdConfiguration;
    }

    @JsonProperty("etcdConfiguration")
    public void setEtcdStorageConfiguration(EtcdStorageConfiguration config) {
        this.etcdConfiguration = config;
    }
}
