package com.seminetwork.janusgraph.analytics.computer;

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;

import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.List;

public class SparkComputer implements Computer {
    private final GraphTraversalSource g;

    public SparkComputer(String graphConfigPath) {
        g = GraphFactory.open(graphConfigPath)
                .traversal().withComputer(SparkGraphComputer.class);
    }

    @Override
    public List run(String query) throws ScriptException {
        GremlinGroovyScriptEngine gremlinGroovyScriptEngine = new GremlinGroovyScriptEngine();
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("g", g);
        final GraphTraversal traversal = (GraphTraversal) gremlinGroovyScriptEngine.eval(query, bindings);
        return traversal.toList();
    }
}
