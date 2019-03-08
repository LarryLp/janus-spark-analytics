package com.seminetwork.janusgraph.analytics.storage;


import com.seminetwork.janusgraph.analytics.model.Result;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class EtcdStorage implements Storage {
    private Client client;
    private EtcdStorageConfiguration config;

    public EtcdStorage(Client client, EtcdStorageConfiguration config) {
        this.client = client;
        this.config = config;
    }

    private void post(String body) throws Exception {
        Response response = client.
                target("http://" + config.getHost() + ":" + config.getPort() + "/v3alpha/kv/put").
                request("applicaction/json").
                post(Entity.json(body));
        if (response.getStatus() != 200) {
            String responseBody = response.readEntity(String.class);
            throw new Exception("wrong status code, expected 200, got: " + response.getStatus() +
                    " with body: " + responseBody);
        }
    }

    public void store(Result res) throws StorageException {
        try {
            String resJson = res.toJson();
            String body = new EtcdHttpRequest(res.getId(), resJson).toJson();
            post(body);
        } catch (IOException e) {
            throw new StorageException("could not build json body to send to etcd", e);
        } catch (Exception e) {
            throw new StorageException("could not send request body to etcd", e);
        }
    }
}
