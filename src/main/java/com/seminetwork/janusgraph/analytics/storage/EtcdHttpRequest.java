package com.seminetwork.janusgraph.analytics.storage;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

public class EtcdHttpRequest {
    private String key;
    private String value;

    public EtcdHttpRequest(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return new String(Base64.encodeBase64(key.getBytes()));
    }

    public String getValue() {
        return new String(Base64.encodeBase64(value.getBytes()));
    }

    public String toJson() throws IOException {
        StringWriter writer = new StringWriter();
        ObjectMapper om = new ObjectMapper() ;
        om.writeValue(writer, this);
        return writer.getBuffer().toString();
    }
}
