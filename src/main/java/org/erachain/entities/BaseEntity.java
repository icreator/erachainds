package org.erachain.entities;

public interface BaseEntity<T> {

    default int getLength() {
        return 0;
    }

    default boolean apply(byte[] data,int position) {
        return true;
    }

    default String getErrors() {
        return "";
    }
}

