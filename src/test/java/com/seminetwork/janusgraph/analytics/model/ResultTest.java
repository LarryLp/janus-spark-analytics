package com.seminetwork.janusgraph.analytics.model;

import com.seminetwork.janusgraph.analytics.model.Result;
import com.seminetwork.janusgraph.analytics.model.Status;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ResultTest {
    @Test
    public void testToJson() throws IOException {
        String res = new Result("foo", Status.INPROGRESS, null, "some-query").toJson();
        assertEquals("{\"id\":\"foo\",\"status\":\"INPROGRESS\",\"result\":null,\"originalQuery\":\"some-query\"}", res);
    }
}
