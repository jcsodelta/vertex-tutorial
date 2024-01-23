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
        int name_start = 0;
        int name_end = 0;
        int buffer_end = 0;

        StringBuilder name_buffer = new StringBuilder();
        while (i < buffer.length() && buffer_end == 0) {
            char c = (char)buffer.getByte(i);
            if (c == '>') {
                buffer_end = i;
            } else if (name_start == 0 && c == '\'') {
                name_start = i;
            } else if (name_start != 0 && name_end == 0) {
                if (c == '\'') {
                    name_end = i;
                } else {
                    name_buffer.append(c);
                }
            }

            ++i;
        }

        this.name = name_buffer.toString();

        return buffer_end;
    }
}
