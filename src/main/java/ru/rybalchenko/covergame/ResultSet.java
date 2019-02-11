package ru.rybalchenko.covergame;

import java.util.*;


public class ResultSet {
    private volatile SortedSet<Product> resultSet;
    private final int bucketSize;
    private final int capacity;

    public ResultSet(int bucketSize, int capacity) {
        this.bucketSize = bucketSize;
        this.capacity = capacity;
        this.resultSet = new TreeSet<>();
    }

    public ResultSet(int bucketSize, int capacity, SortedSet<Product> resultSet) {
        this.resultSet = resultSet;
        this.bucketSize = bucketSize;
        this.capacity = capacity;
    }

    public SortedSet<Product> getResult() {
        return resultSet;
    }

    public void add(Product product) {
        int size = resultSet.size();

        if (size < capacity) {
            if (fitsToBucket(product)) {
                resultSet.add(product);
            }
        } else {
            if (product.getPrice() < resultSet.last().getPrice()) {
                if (fitsToBucket(product)) {
                    resultSet.add(product);
                    resultSet.remove(resultSet.last());
                } else {
                    Product maxProduct;
                    Optional<Product> max = resultSet.stream()
                            .filter(it -> it.getId() == product.getId())
                            .max(Product::compareTo);
                    maxProduct = max.get();
                    if (maxProduct.getPrice() > product.getPrice()) {
                        resultSet.remove(maxProduct);
                        resultSet.add(product);
                    }
                }
            }
        }
    }

    public ResultSet merge(ResultSet other) {
        SortedSet<Product> copy = new TreeSet<>(resultSet);
        for (Product product : other.getResult()) {
            if (product.getPrice() < copy.last().getPrice()) {
                if (copy.stream().filter(it -> it.getId() == product.getId()).count() < bucketSize) {
                    copy.remove(copy.last());
                    copy.add(product);
                }
            }
        }
        return new ResultSet(bucketSize, capacity, copy);
    }

    private boolean fitsToBucket(Product product) {
        long count;
        count = resultSet.stream().filter(it -> it.getId() == product.getId()).count();
        return count < bucketSize;
    }


    public String toString() {
        List<Product> result = new ArrayList<>(resultSet);
        StringBuilder builder = new StringBuilder();
        result.forEach(product -> builder.append(product.toString()).append("\n"));
        return builder.toString();
    }
}
