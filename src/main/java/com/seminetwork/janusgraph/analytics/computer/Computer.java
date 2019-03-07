package com.seminetwork.janusgraph.analytics.computer;

import javax.script.ScriptException;
import java.util.List;

public interface Computer {
    List run(String query) throws ScriptException;
}
