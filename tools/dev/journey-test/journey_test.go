package main

import (
	"context"
	"net/url"
	"testing"
	"time"

	client "github.com/SeMI-network/janus-spark-analytics/clients/go"
	"github.com/coreos/etcd/clientv3"
	uuid "github.com/satori/go.uuid"
	"github.com/stretchr/testify/assert"
)

const testQuery = `
	g.V().hasLabel("person").union(
		values("age").union(
			min().project("ageMin"), 
			max().project("ageMax"), 
			count().project("ageCount"),
			mean().project("ageMean"),
			sum().project("ageSum")
		), 
		values("networth").union(
			min().project("networthMin"),
			max().project("networthMax"),
			count().project("networthCount"),
			mean().project("networthMean"),
			sum().project("networthSum")
		), 
	)`

var sparkJobMustCompleteWithin = 240 * time.Second

var expectedResult = []interface{}{
	map[string]interface{}{"ageMax": float64(59)},
	map[string]interface{}{"networthMin": float64(10000)},
	map[string]interface{}{"networthSum": float64(2.11e+06)},
	map[string]interface{}{"ageMin": float64(23)},
	map[string]interface{}{"networthCount": float64(4)},
	map[string]interface{}{"ageCount": float64(4)},
	map[string]interface{}{"ageMean": float64(42)},
	map[string]interface{}{"networthMax": float64(1e+06)},
	map[string]interface{}{"ageSum": float64(168)},
	map[string]interface{}{"networthMean": float64(527500)},
}

func TestAnalyticsAPI(t *testing.T) {
	u, _ := url.Parse("http://localhost:6080")
	id, err := uuid.NewV4()
	if err != nil {
		t.Fatalf("could not generate id: %v", err)
	}

	c := client.New(u)
	params := client.QueryParams{
		ID:    id.String(),
		Query: testQuery,
	}

	err = c.Schedule(context.Background(), params)
	if err != nil {
		t.Fatalf("scheduling failed: %v", err)
	}

	etcd, err := clientv3.NewFromURL("http://localhost:6379")
	if err != nil {
		t.Fatalf("could not create etcd client: %v", err)
	}

	ctx, cancel := context.WithTimeout(context.Background(), sparkJobMustCompleteWithin)
	defer cancel()
	wc := etcd.Watch(ctx, id.String())
	for res := range wc {
		for _, ev := range res.Events {
			res, err := client.ParseResult(ev.Kv.Value)
			if err != nil {
				t.Fatalf(err.Error())
			}

			if res.Status == client.StatusSucceeded {
				assert.ElementsMatch(t, expectedResult, res.Result, "should match the expected results on success")
				return
			}
		}
	}

	t.Fatalf("waited for %s for analytics job to succeed, but never happened", sparkJobMustCompleteWithin)
}
