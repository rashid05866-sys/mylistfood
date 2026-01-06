package com.example.myfood.entity;

public class Product {
    private String name;
    private boolean active;

    public Product() {
    }
    public Product(String name,  boolean active) {
        this.name = name;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
