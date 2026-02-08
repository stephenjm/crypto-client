package com.crypto.security.cache;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class OffHeapKeyStore implements DataKeyStore {

    private final Map<String, ByteBuffer> store = new ConcurrentHashMap<>();

    @Override
    public void put(String id, byte[] key) {
        if (id == null || key == null) {
            throw new IllegalArgumentException("id and key must not be null");
        }
        ByteBuffer buf = ByteBuffer.allocateDirect(key.length);
        buf.put(key);
        buf.flip();
        ByteBuffer previous = store.put(id, buf);
        if (previous != null) {
            zeroDirect(previous);
        }
    }

    @Override
    public Optional<byte[]> get(String id) {
        ByteBuffer buf = store.get(id);
        if (buf == null) return Optional.empty();
        byte[] out = new byte[buf.remaining()];
        ByteBuffer dup = buf.duplicate();
        dup.get(out);
        return Optional.of(out);
    }

    @Override
    public boolean remove(String id) {
        ByteBuffer removed = store.remove(id);
        if (removed == null) return false;
        zeroDirect(removed);
        return true;
    }

    @Override
    public void clear() {
        for (Map.Entry<String, ByteBuffer> e : store.entrySet()) {
            zeroDirect(e.getValue());
        }
        store.clear();
    }

    private static void zeroDirect(ByteBuffer direct) {
        if (direct == null) return;
        try {
            ByteBuffer dup = direct.duplicate();
            dup.clear();
            while (dup.remaining() > 0) {
                int chunk = Math.min(dup.remaining(), 4096);
                byte[] zeros = new byte[chunk];
                dup.put(zeros);
            }
        } catch (Throwable t) { /* best-effort */ }
    }
}
