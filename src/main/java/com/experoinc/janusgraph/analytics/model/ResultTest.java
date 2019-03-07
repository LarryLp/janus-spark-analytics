package com.experoinc.janusgraph.analytics.model;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ResultTest {
    @Test
    public void testToJson() throws IOException {
        String res = new Result("foo", Status.INPROGRESS, null).toJson();
        assertEquals("{\"id\":\"foo\",\"status\":\"INPROGRESS\",\"result\":null}", res);
    }
}
