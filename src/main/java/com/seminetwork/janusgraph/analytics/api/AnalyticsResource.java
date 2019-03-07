package com.seminetwork.janusgraph.analytics.api;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seminetwork.janusgraph.analytics.computer.Computer;
import com.seminetwork.janusgraph.analytics.model.Result;
import com.seminetwork.janusgraph.analytics.model.Status;
import com.seminetwork.janusgraph.analytics.storage.Storage;
import com.seminetwork.janusgraph.analytics.storage.StorageException;

import javax.script.ScriptException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/analytics")
@Produces(MediaType.APPLICATION_JSON)
public class AnalyticsResource {
    private final Storage storage;
    private final Computer computer;
    private ExecutorService myExecutor = Executors.newCachedThreadPool();

    public AnalyticsResource(final Computer computer, final Storage storage) {
        this.computer = computer;
        this.storage = storage;
    }

    void storeRunning() throws StorageException {
        Result res = new Result("foo", Status.INPROGRESS, null);
        this.storage.store(res);
    }

    void storeSuccess() {
        // update storage
        // set status to succeeded
        // put results
    }

    void storeFailure(Exception e) {
        // update storage
        // set status to failed
        // put error message
    }

    void runQuery(final String query) throws ScriptException {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        Object results = this.computer.run(query);
        String jsonified = gson.toJson(results);
        System.out.println("\n\n\n Done:");
        System.out.println("JSON:");
        System.out.println(jsonified);
        System.out.println("Results:");
        System.out.println(results);
        storeSuccess();
    }

    void runQueryAsync(final String query) {
        myExecutor.execute(() -> {
            try {
                this.runQuery(query);
            } catch (Exception e) {
                System.out.println("\n Failure: " + e.getMessage());
                e.printStackTrace();
                storeFailure(e);
            }
        });
    }

    @GET
    @Timed
    public Response submitQuery(@QueryParam("query") String query) {
        try {
            storeRunning();
        } catch (StorageException e) {
            e.printStackTrace();
        }
        runQueryAsync(query);
        return Response.status(Response.Status.ACCEPTED).build();
    }
}
