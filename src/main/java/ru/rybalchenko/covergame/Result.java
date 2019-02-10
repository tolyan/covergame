package ru.rybalchenko.covergame;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Result {
    private final Map<Integer, SortedSet<Product>> buckets;
    private final SortedSet<Product> resultSet;
    private final int bucketSize;
    private final int capacity;

    private volatile float priceThreshold = Float.MAX_VALUE;
    private LongAdder totalCount = new LongAdder();

    public Result(int capacity, int bucketSize) {
        this.buckets = new ConcurrentHashMap<>(capacity);
        resultSet = new ConcurrentSkipListSet<>();
        this.bucketSize = bucketSize;
        this.capacity = capacity;
    }

    public void add(final Product product) {

        Function<Integer, SortedSet<Product>> generateBucket =
                key -> new ConcurrentSkipListSet<>();

        if (totalCount.intValue() < capacity) {
            SortedSet<Product> bucket = buckets.computeIfAbsent(product.getId(), generateBucket);
            if (bucket.size() < bucketSize) {
                bucket.add(product);
                resultSet.add(product);
                priceThreshold = resultSet.last().getPrice();
                totalCount.increment();
            } else {
                updateBucket(product, bucket);
            }
        } else {
            if (product.getPrice() <= priceThreshold) {
                SortedSet<Product> bucket = buckets.get(product.getId());
                if (bucket != null && bucket.size() > bucketSize) {
                    updateBucket(product, bucket);
                } else {
                    if (product.getPrice() < priceThreshold) {
                        bucket = buckets.computeIfAbsent(product.getId(), generateBucket);
                        Product last = resultSet.last();
                        synchronized (this) {
                            SortedSet<Product> evictionBucket = buckets.get(last.getId());
                            evictionBucket.remove(last);
                            if(evictionBucket.size() == 0) {
                                buckets.remove(last.getId());
                            }
                            bucket.add(product);
                            resultSet.remove(last);
                            resultSet.add(product);
                            priceThreshold = resultSet.last().getPrice();
                        }

                    }
                }

            }
        }
    }

    private void updateBucket(Product product, SortedSet<Product> bucket) {
        Product last = bucket.last();
        if (last.getPrice() > product.getPrice()) {
            synchronized (this) {
                bucket.remove(last);
                bucket.add(product);
                resultSet.remove(product);
                resultSet.add(product);
                priceThreshold = resultSet.last().getPrice();
            }
        }
    }

    public String toString() {
        List<Product> result = new ArrayList<>(resultSet);
        StringBuilder builder = new StringBuilder();
        result.forEach(product -> builder.append(product.toString()).append("\n"));
        return builder.toString();
    }
}
