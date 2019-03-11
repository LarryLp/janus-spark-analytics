package com.seminetwork.janusgraph.analytics.api;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seminetwork.janusgraph.analytics.computer.Computer;
import com.seminetwork.janusgraph.analytics.model.Result;
import com.seminetwork.janusgraph.analytics.model.Status;
import com.seminetwork.janusgraph.analytics.storage.Storage;
import com.seminetwork.janusgraph.analytics.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/analytics")
@Produces(MediaType.APPLICATION_JSON)
public class AnalyticsResource {
    private final Storage storage;
    private final Computer computer;
    private ExecutorService myExecutor = Executors.newCachedThreadPool();
    private final static Logger log = LoggerFactory.getLogger(AnalyticsResource.class);

    public AnalyticsResource(final Computer computer, final Storage storage) {
        this.computer = computer;
        this.storage = storage;
    }

    void storeRunning(Query query) throws StorageException {
        Result res = new Result(query.id, Status.INPROGRESS, null, query);
        log.debug("received analytics job", res);
        this.storage.store(res);
    }

    void storeSuccess(Query query, List results) throws StorageException {
        Result res = new Result(query.id, Status.SUCCEEDED, results, query);
        log.debug("successfully finished analytics job", res);
        this.storage.store(res);
    }

    void storeFailure(Query query, Exception e) throws StorageException {
        String err = e.getMessage() + Arrays.toString(e.getStackTrace());
        Result res = new Result(query.id, Status.FAILED, err, query);
        log.debug("analytics job failed", res);
        this.storage.store(res);
    }

    void runQuery(final Query query) throws ScriptException, StorageException {
        List results = this.computer.run(query.query);
        storeSuccess(query, results);
    }

    void runQueryAsync(final Query query) {
        myExecutor.execute(() -> {
            try {
                this.runQuery(query);
            } catch (Exception e) {
                try {
                    storeFailure(query, e);
                } catch (StorageException se) {
                    log.error("analytics job failed, then updating the cache failed as well", e);
                    se.printStackTrace();
                }
            }
        });
    }

    static class Query {
        @JsonProperty String id;
        @JsonProperty String query;

        public String getId() {
            return id;
        }

        public String getQuery() {
            return query;
        }
    }

    static class ErrorResponse {
        @JsonProperty String error;

        public String getError() {
            return error;
        }
    }

    @POST
    @Timed
    public Response submitQuery(Query query) {
        try {
            storeRunning(query);
        } catch (StorageException e) {
            ErrorResponse err = new ErrorResponse();
            err.error = "could not initiate new job in storage: " + e.getMessage() + " " + e.getCause();
            return Response.status(Response.Status.BAD_REQUEST).entity(err).build();
        }
        runQueryAsync(query);
        return Response.status(Response.Status.ACCEPTED).build();
    }
}
