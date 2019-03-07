package com.seminetwork.janusgraph.analytics.model;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

public class Result {
    private String id;
    private Status status;
    private Object result;

    public Result(String id, Status status, Object result) {
        this.id = id;
        this.status = status;
        this.result = result;
    }

    public String toJson() throws IOException {
        StringWriter writer = new StringWriter();
        ObjectMapper om = new ObjectMapper() ;
        om.writeValue(writer, this);
        return writer.getBuffer().toString();
    }

    public String getId() {
        return this.id;
    }

    public Status getStatus() {
        return this.status;
    }

    public Object getResult() {
        return this.result;
    }
}

