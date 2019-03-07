package com.seminetwork.janusgraph.analytics.api;

import com.seminetwork.janusgraph.analytics.computer.Computer;
import com.seminetwork.janusgraph.analytics.computer.SparkComputer;
import com.seminetwork.janusgraph.analytics.storage.EtcdStorage;
import com.seminetwork.janusgraph.analytics.storage.Storage;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AnalyticsApplication extends Application<AnalyticsConfiguration> {
    private Storage storage;
    private Computer computer;

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
        this.storage =  new EtcdStorage(configuration.getEtcdOrigin());
        this.computer =  new SparkComputer(configuration.getGraphConfigPath());
        final AnalyticsResource resource = new AnalyticsResource(this.computer, this.storage);
        environment.jersey().register(resource);
    }

}