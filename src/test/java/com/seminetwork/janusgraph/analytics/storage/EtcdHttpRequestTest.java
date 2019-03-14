package com.seminetwork.janusgraph.analytics.storage;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class EtcdHttpRequestTest {
    @Test
    public void testBase64EncodingInJson() throws IOException {
        String res = new EtcdHttpRequest("some-key", "some-value").toJson();
        assertEquals("{\"key\":\"c29tZS1rZXk=\",\"value\":\"c29tZS12YWx1ZQ==\"}", res);
    }

}
