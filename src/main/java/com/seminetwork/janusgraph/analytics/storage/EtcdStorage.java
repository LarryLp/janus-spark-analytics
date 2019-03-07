package com.seminetwork.janusgraph.analytics.storage;


import com.seminetwork.janusgraph.analytics.model.Result;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Charsets.UTF_8;

public class EtcdStorage implements Storage {
    private KV kvClient;

    public EtcdStorage(String discoveryUrl) {
        Client client = Client.builder().endpoints(discoveryUrl).build();
        kvClient = client.getKVClient();
    }

    public void store(Result res) throws StorageException {
        ByteSequence key = ByteSequence.from(res.getId(), UTF_8);
        ByteSequence value = null;
        try {
            value = ByteSequence.from(res.toJson().getBytes());
        } catch (IOException e) {
            throw new StorageException(String.format("could not convert results to json sending off to storage", key), e);
        }

        try {
            this.kvClient.put(key, value).get();
        } catch (InterruptedException e) {
            throw new StorageException(String.format("could not put key '%s' to storage", key), e);
        } catch (ExecutionException e) {
            throw new StorageException(String.format("could not put key '%s' to storage", key), e);
        }

    }
}
