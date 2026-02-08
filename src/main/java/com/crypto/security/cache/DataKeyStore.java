package com.crypto.security.cache;

import java.util.Optional;

/**
 * Simple interface for storing sensitive key material off-heap.
 */
public interface DataKeyStore {
    void put(String id, byte[] key);
    Optional<byte[]> get(String id);
    boolean remove(String id);
    void clear();
}
