package com.experoinc.janusgraph.analytics.api;

import com.codahale.metrics.annotation.Timed;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;

import javax.script.SimpleBindings;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/analytics")
@Produces(MediaType.APPLICATION_JSON)
public class AnalyticsResource {

    private final GraphTraversalSource g;

    public AnalyticsResource(final String graphConfigPath) {
        g = GraphFactory.open(graphConfigPath)
                .traversal().withComputer(SparkGraphComputer.class);
    }

    @GET
    @Timed
    public Object submitQuery(@QueryParam("query") String query) throws Exception {
        GremlinGroovyScriptEngine gremlinGroovyScriptEngine = new GremlinGroovyScriptEngine();
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("g", g);

        final GraphTraversal traversal = (GraphTraversal) gremlinGroovyScriptEngine.eval(query, bindings);

        return traversal.next();
    }
}