package com.seminetwork.janusgraph.analytics.storage;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.seminetwork.janusgraph.analytics.model.Result;
import com.seminetwork.janusgraph.analytics.model.Status;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class EtcdStorageTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Test
    public void store() throws StorageException {
        int port = wireMockRule.port();
        String host = "localhost";

        EtcdStorageConfiguration config = new EtcdStorageConfiguration();
        config.setHost(host);
        config.setPort(port);

        stubFor(any(urlPathEqualTo("/v3alpha/kv/put"))
                .willReturn(aResponse().withStatus(200)));


        Result result = new Result("some-id", Status.INPROGRESS, null);

        final Client client = new JerseyClientBuilder().build();
        Storage storage = new EtcdStorage(client, config);
        storage.store(result);

        String expectedBody = "{" +
                "  \"key\" : \"c29tZS1pZA==\"," +
                "  \"value\" : \"eyJpZCI6InNvbWUtaWQiLCJzdGF0dXMiOiJJTlBST0dSRVNTIiwicmVzdWx0IjpudWxsfQ==\"" +
                "}";

        verify(postRequestedFor(urlPathEqualTo("/v3alpha/kv/put"))
                .withRequestBody(equalToJson(expectedBody )));

    }
}