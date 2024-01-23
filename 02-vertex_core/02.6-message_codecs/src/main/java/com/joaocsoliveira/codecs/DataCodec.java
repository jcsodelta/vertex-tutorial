package com.joaocsoliveira.codecs;

import com.joaocsoliveira.models.Data;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class DataCodec implements MessageCodec<Data, Data> {

    @Override
    public void encodeToWire(Buffer buffer, Data data) {
        buffer.appendString(String.format("<name: '%s'>", data.getName()));
    }

    @Override
    public Data decodeFromWire(int i, Buffer buffer) {
        return new Data("TODO");
    }

    @Override
    public Data transform(Data data) {
        return data;
    }

    @Override
    public String name() {
        return "DataCodec";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
