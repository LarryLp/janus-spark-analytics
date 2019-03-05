package main

import (
	"fmt"
	"log"

	"github.com/creativesoftwarefdn/weaviate/gremlin"
	schema "github.com/creativesoftwarefdn/weaviate/gremlin/gremlin_schema_query"
	"github.com/creativesoftwarefdn/weaviate/gremlin/http_client"
)

var client *http_client.Client

type person struct {
	name     string
	age      int64
	networth float64
}

var demoData = []person{
	{"John", 48, 500000},
	{"Jane", 38, 600000},
	{"Frank", 23, 10000},
	{"Diane", 59, 1000000},
}

func check(err error) {
	if err != nil {
		log.Fatal(err)
	}
}

func main() {
	client = http_client.NewClient("http://localhost:6182")
	addProperties()
}

func addProperties() {
	addVertexLabel()
	addProperty("name", schema.DATATYPE_STRING)
	addIndex("name")
	addIndexedProperty("age", schema.DATATYPE_LONG)
	addIndexedProperty("networth", schema.DATATYPE_LONG)
	importDemoSet()
}

func addVertexLabel() {
	q := schema.New()
	q.MakeVertexLabel("person")
	q.Commit()
	_, err := client.Execute(q)
	check(err)
}

func addProperty(label string, dataType schema.DataType) {
	q := schema.New()
	q.MakePropertyKey(label, dataType, schema.CARDINALITY_SINGLE)
	q.Commit()
	_, err := client.Execute(q)
	check(err)
}

func addIndexedProperty(label string, dataType schema.DataType) {
	q := schema.New()
	q.MakeIndexedPropertyKey(label, dataType, schema.CARDINALITY_SINGLE, "search")
	q.Commit()
	_, err := client.Execute(q)
	check(err)
}

func addIndex(labels ...string) {
	q := schema.New()
	q.AddGraphMixedIndexString("search", labels, "search")
	q.Commit()
	_, err := client.Execute(q)
	check(err)
}

func importDemoSet() {
	q := gremlin.New().Raw("g")

	for _, p := range demoData {
		fmt.Printf("importing %v\n", p)
		q = q.
			AddV("person").
			StringProperty("name", p.name).
			Int64Property("age", p.age).
			Float64Property("networth", p.networth)
	}

	_, err := client.Execute(q)
	check(err)
}
