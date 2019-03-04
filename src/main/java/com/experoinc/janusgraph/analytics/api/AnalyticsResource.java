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
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/analytics")
@Produces(MediaType.APPLICATION_JSON)
public class AnalyticsResource {
    private ExecutorService myExecutor = Executors.newCachedThreadPool(); 
    private final GraphTraversalSource g;

    public AnalyticsResource(final String graphConfigPath) {
      g = GraphFactory.open(graphConfigPath)
        .traversal().withComputer(SparkGraphComputer.class);
    }

    void storeRunning() {
      // update etcd
      // set status to running
    }

    void storeSuccess() {
      // update etcd
      // set status to succeeded
      // put results
    }

    void storeFailure(Exception e) {
      // update etcd
      // set status to failed
      // put error message
    }

    void runQueryAsync(final String query) {
      myExecutor.execute(new Runnable() {
        public void run() {
          GremlinGroovyScriptEngine gremlinGroovyScriptEngine = new GremlinGroovyScriptEngine();
          SimpleBindings bindings = new SimpleBindings();
          bindings.put("g", g);

          storeRunning();

          try {
            final GraphTraversal traversal = (GraphTraversal) gremlinGroovyScriptEngine.eval(query, bindings);
            traversal.next();
            storeSuccess();
          } catch(Exception e) {
            storeFailure(e);
          }
        }
      });
    }

    @GET
    @Timed
    public Response submitQuery(@QueryParam("query") String query) throws Exception {
      runQueryAsync(query);
      return Response.status(Response.Status.ACCEPTED).build();
    }
}
