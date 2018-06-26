package com.real.doctor.realdoc.model;

public class LabelBean {

    private int id;
    private String name;

    public LabelBean(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "LabelBean{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
