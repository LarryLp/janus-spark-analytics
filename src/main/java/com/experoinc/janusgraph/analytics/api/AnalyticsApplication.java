package com.experoinc.janusgraph.analytics.api;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AnalyticsApplication extends Application<AnalyticsConfiguration> {
    public static void main(String[] args) throws Exception {
        new AnalyticsApplication().run(args);
    }

    @Override
    public String getName() {
        return "janusgraph-analytics";
    }

    @Override
    public void initialize(Bootstrap<AnalyticsConfiguration> bootstrap) {
    }

    @Override
    public void run(AnalyticsConfiguration configuration,
                    Environment environment) {
        final AnalyticsResource resource = new AnalyticsResource(configuration.getGraphConfigPath());
        environment.jersey().register(resource);
    }

}