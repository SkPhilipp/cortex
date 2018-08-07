package com.hileco.cortex.context.layer;

import java.util.Arrays;

public class LayeredBytes {

    private byte[] bytes;

    public LayeredBytes() {
        bytes = new byte[8192];
    }

    public byte[] read(int offset, int length) {
        return Arrays.copyOfRange(bytes, offset, offset + length);
    }

    public void clear() {
        bytes = new byte[8192];
    }

    public void write(int offset, byte[] bytesToWrite) {
        write(offset, bytesToWrite, bytesToWrite.length);
    }

    public void write(int offset, byte[] bytesToWrite, int writeLength) {
        System.arraycopy(bytesToWrite, 0, bytes, offset, writeLength);
    }

    public LayeredBytes copy() {
        throw new UnsupportedOperationException("Copying LayeredBytes is currently not supported.");
    }

    @Override
    public String toString() {
        return String.format("LayeredBytes{size %d}", bytes.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LayeredBytes that = (LayeredBytes) o;
        return Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
