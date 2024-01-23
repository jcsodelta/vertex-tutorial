package com.joaocsoliveira.models;

import java.io.Serializable;

public class DataSerializable implements Serializable {
    private String name;

    public DataSerializable() {}

    public DataSerializable(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
