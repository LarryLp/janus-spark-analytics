package com.experoinc.janusgraph.analytics.etcd;

public class StorageException extends Exception {
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
