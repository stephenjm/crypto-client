package com.cryptoguard.crypto.cache;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OffHeapKeyStoreTest {
    @Test
    void putGetRemoveLifecycle() {
        OffHeapKeyStore store = new OffHeapKeyStore();
        String id = "test-key";
        byte[] key = new byte[] {1,2,3,4};
        store.put(id, key);
        assertTrue(store.get(id).isPresent());
        byte[] out = store.get(id).get();
        assertArrayEquals(key, out);
        java.util.Arrays.fill(out, (byte)0);
        assertTrue(store.remove(id));
        assertFalse(store.get(id).isPresent());
    }
}
