// Package client is a MANUALLY created go client for the analytics API
// It is used in the journey tests for the analytics app.
package client

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"net/url"

	"golang.org/x/net/context/ctxhttp"
)

// QueryParams contain a unique ID and the Gremlin Query as strings. The id
// should be something that can be generated from the user's original query
// (e.g. a hash) so that it can consistently be reproduced.
type QueryParams struct {
	ID    string `json:"id"`
	Query string `json:"query"`
}

// Validate whether all required fields are set on QueryParams
func (p QueryParams) Validate() error {
	if p.ID == "" {
		return fmt.Errorf("ID cannot be empty")
	}

	if p.Query == "" {
		return fmt.Errorf("Query cannot be empty")
	}

	return nil
}

// Client to interact with the Analytics API
type Client struct {
	BaseURL *url.URL
}

// New Client to interact with the Analytics API
func New(baseURL *url.URL) *Client {
	return &Client{
		BaseURL: baseURL,
	}
}

// Schedule an analytics job
func (c *Client) Schedule(ctx context.Context, params QueryParams) error {
	if err := params.Validate(); err != nil {
		return fmt.Errorf("invalid params: %v", err)
	}

	paramsBytes, err := json.Marshal(params)
	if err != nil {
		return fmt.Errorf("could not marshal params to JSON: %v", err)
	}

	if err := ctx.Err(); err != nil {
		return fmt.Errorf("cannot start request: %v", err)
	}

	url := c.BaseURL
	url.Path = "/analytics"
	req, err := http.NewRequest("POST", url.String(), bytes.NewReader(paramsBytes))
	if err != nil {
		return fmt.Errorf("cannot build POST request: %v", err)
	}

	req.Header.Add("Content-Type", "application/json")

	client := &http.Client{}
	res, err := ctxhttp.Do(ctx, client, req)
	if err != nil {
		return fmt.Errorf("POST request failed: %v", err)
	}

	if res.StatusCode != http.StatusAccepted {
		var body string
		bb, err := ioutil.ReadAll(res.Body)
		defer res.Body.Close()
		if err != nil {
			body = err.Error()
		}
		body = string(bb)

		return fmt.Errorf("expected status code 202, but received '%d' with body '%s'", res.StatusCode, body)
	}

	return nil
}
