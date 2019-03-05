// add name prop
mgmt = graph.openManagement()
newProp = mgmt.makePropertyKey("name").dataType(String.class).cardinality(org.janusgraph.core.Cardinality.SINGLE).make()
existingIndex = mgmt.getGraphIndex("search")
mgmt.addIndexKey(existingIndex, newProp)
mgmt.commit()


// add age prop
mgmt = graph.openManagement()
newProp = mgmt.makePropertyKey("age").dataType(Integer.class).cardinality(org.janusgraph.core.Cardinality.SINGLE).make()
existingIndex = mgmt.getGraphIndex("search")
mgmt.addIndexKey(existingIndex, newProp)
mgmt.commit()
