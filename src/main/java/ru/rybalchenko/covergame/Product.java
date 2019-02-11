package ru.rybalchenko.covergame;

import java.util.Objects;

public class Product implements Comparable {
    private final int id;
    private final String name;
    private final String condition;
    private final String state;
    private final float price;


    public Product(int id, String name, String condition, String state, float price) {
        this.id = id;
        this.name = name;
        this.condition = condition;
        this.state = state;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public String getState() {
        return state;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id &&
                Float.compare(product.price, price) == 0 &&
                Objects.equals(name, product.name) &&
                Objects.equals(condition, product.condition) &&
                Objects.equals(state, product.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, condition, state, price);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", condition='" + condition + '\'' +
                ", state='" + state + '\'' +
                ", price=" + price +
                '}';
    }

    public String toCSV() {
        return id + "," + name + "," + condition + "," + state + "," + price + '\n';
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) throw new NullPointerException();
        Product other = (Product) o;
        if (this.equals(other)) return 0;
        int compare = Float.compare(this.price, other.price);
        if (compare == 0) return 1;
        return compare;
    }
}
