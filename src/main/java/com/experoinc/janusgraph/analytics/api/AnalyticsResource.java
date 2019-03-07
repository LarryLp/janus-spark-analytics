package com.experoinc.janusgraph.analytics.api;

import com.codahale.metrics.annotation.Timed;
import com.experoinc.janusgraph.analytics.etcd.EtcdStorage;
import com.experoinc.janusgraph.analytics.etcd.StorageException;
import com.experoinc.janusgraph.analytics.model.Result;
import com.experoinc.janusgraph.analytics.model.Status;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

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
    private final EtcdStorage storage;

    public AnalyticsResource(final String graphConfigPath, final String etcdOrigin) {
      g = GraphFactory.open(graphConfigPath)
        .traversal().withComputer(SparkGraphComputer.class);
        this.storage = new EtcdStorage(etcdOrigin);
    }

    void storeRunning() throws StorageException {
        Result res = new Result("foo", Status.INPROGRESS, null);
        this.storage.store(res);
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
      myExecutor.execute(() -> {
        GremlinGroovyScriptEngine gremlinGroovyScriptEngine = new GremlinGroovyScriptEngine();
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("g", g);

          try {
              storeRunning();
          } catch (StorageException e) {
              e.printStackTrace();
          }

          try {
          final GraphTraversal traversal = (GraphTraversal) gremlinGroovyScriptEngine.eval(query, bindings);
          Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
          Object results = traversal.toList();
          String jsonified = gson.toJson(results);
          System.out.println("\n\n\n Done:");
          System.out.println("JSON:");
          System.out.println(jsonified);
          System.out.println("Results:");
          System.out.println(results);
          storeSuccess();
        } catch(Exception e) {
          System.out.println("\n\n\n Failure: " + e.getMessage());
          e.printStackTrace();
          storeFailure(e);
        }
      });
    }

    @GET
    @Timed
    public Response submitQuery(@QueryParam("query") String query) {
      runQueryAsync(query);
      return Response.status(Response.Status.ACCEPTED).build();
    }
}
