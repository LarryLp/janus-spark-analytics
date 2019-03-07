package com.seminetwork.janusgraph.analytics.storage;

import com.seminetwork.janusgraph.analytics.model.Result;

public interface Storage {
    void store(Result res) throws StorageException;
}
