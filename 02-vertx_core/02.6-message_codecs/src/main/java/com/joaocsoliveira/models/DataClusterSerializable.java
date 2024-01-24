package com.joaocsoliveira.models;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.shareddata.ClusterSerializable;

public class DataClusterSerializable extends Data implements ClusterSerializable {
    public DataClusterSerializable() {
        super(null);
    }
    public DataClusterSerializable(String name) {
        super(name);
    }

    @Override
    public void writeToBuffer(Buffer buffer) {
        buffer.appendString(String.format("<name: '%s'>", this.name));
    }

    @Override
    public int readFromBuffer(int i, Buffer buffer) {
        int nameStart = 0;
        int nameEnd = 0;
        int bufferEnd = 0;

        StringBuilder nameBuffer = new StringBuilder();
        while (i < buffer.length() && bufferEnd == 0) {
            char c = (char)buffer.getByte(i);
            if (c == '>') {
                bufferEnd = i;
            } else if (nameStart == 0 && c == '\'') {
                nameStart = i;
            } else if (nameStart != 0 && nameEnd == 0) {
                if (c == '\'') {
                    nameEnd = i;
                } else {
                    nameBuffer.append(c);
                }
            }

            ++i;
        }

        this.name = nameBuffer.toString();

        return bufferEnd;
    }
}
