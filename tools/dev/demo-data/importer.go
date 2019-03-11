package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"time"

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
	client = http_client.NewClient("http://janus:8182")
	ctx, cancel := context.WithTimeout(context.Background(), 60*time.Second)
	defer cancel()
	waitForStartup(ctx)
	addProperties()
}

func addProperties() {
	fmt.Println("Add label 'person'")
	addVertexLabel()

	fmt.Println("Add property 'name'")
	addProperty("name", schema.DATATYPE_STRING)

	fmt.Println("Add index for property 'name'")
	addIndex("name")

	fmt.Println("Add property 'age' and extend index")
	addIndexedProperty("age", schema.DATATYPE_LONG)

	fmt.Println("Add property 'networth' and extend index")
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
		q = q.
			AddV("person").
			StringProperty("name", p.name).
			Int64Property("age", p.age).
			Float64Property("networth", p.networth)
	}

	fmt.Println("Import demo set.")
	_, err := client.Execute(q)
	check(err)
}

func waitForStartup(ctx context.Context) {
	fmt.Printf("Waiting for Janus to be ready ")
	for range time.Tick(time.Second) {
		if err := ctx.Err(); err != nil {
			fmt.Fprintf(os.Stderr, "\nerror: %v\n", err)
			os.Exit(1)
		}

		if isReady() {
			fmt.Printf("\n")
			return
		}

		fmt.Printf(".")
	}
}

func isReady() bool {
	if _, err := client.Execute(gremlin.New().Raw("1 + 1")); err != nil {
		return false
	}

	return true
}
