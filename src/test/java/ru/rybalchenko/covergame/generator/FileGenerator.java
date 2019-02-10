package ru.rybalchenko.covergame.generator;

import ru.rybalchenko.covergame.Condition;
import ru.rybalchenko.covergame.State;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class FileGenerator {
    public static final String DELIMITER = ",";
    private static final int bufferSize = 1024 * 1024;
    private static final int productRefSize = 1000;

    public static void main(String[] args) throws IOException {
        FileGenerator generator = new FileGenerator();
        String fileName = "./src/test/resources/testFile.csv";
        List<String> ref = generator.generateRefProducts();

        generator.writeFiles("./src/test/resources/", 100000, 5, ref);
    }

    public void writeReferenceFile(String filename, List<String> referenceProducts) throws IOException {
        FileWriter writer = new FileWriter(filename);
        for (String referenceProduct : referenceProducts) {
            writer.write(referenceProduct);
        }
        writer.close();
    }

    public void writeFiles(String dirName, int size, int filesAmount, List<String> refProducts) throws IOException {
        int start = 0;
        List<String> ref = new ArrayList<>();
        for (int i = 1; i < filesAmount + 1; i++) {
            int end = (refProducts.size() * i) / filesAmount;
            List<String> subList = refProducts.subList(start, end);
            ref.addAll(subList);
            String filename = dirName + "/testFile" + i + ".csv";
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename), bufferSize)) {
                for (String line : subList) {
                    writer.write(line);
                }
                int index = 0;
                while (index < size) {
                    String line = generateLine(start + index, false);
                    writer.write(line);
                    index++;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            start = end;
        }
        writeReferenceFile("./src/test/resources/refFile.csv", ref);
    }


    private List<String> generateRefProducts() {
        int index = 0;
        Map<Integer, ArrayList<String>> products = new HashMap<>();
        while (index < productRefSize) {
            int id = getReferenceId(products);
            String product = generateLine(id, true);
            products.get(id).add(product);
            index++;
        }
        ArrayList<String> result = new ArrayList<>();
        products.values().forEach(result::addAll);
        return result;
    }

    private int getReferenceId(Map<Integer, ArrayList<String>> refProducts) {
        int result;
        do {
            result = ThreadLocalRandom.current().nextInt(0, productRefSize);
            refProducts.computeIfAbsent(result, bucket -> new ArrayList<>());
        } while (refProducts.get(result).size() >= 20);
        return result;
    }

    private String generateLine(int index, boolean ref) {
        int id = index;
        String name = "product_" + index;
        Condition[] conditions = Condition.values();
        State[] states = State.values();
        String condition = conditions[ThreadLocalRandom.current().nextInt(0, conditions.length)].name();
        String state = states[ThreadLocalRandom.current().nextInt(0, states.length)].name();
        float price;
        if (ref) {
            price = ThreadLocalRandom.current().nextFloat() * 0.4f;
        } else {
            price = 0.5f + ThreadLocalRandom.current().nextFloat() * 0.5f;
        }
        return id + DELIMITER + name + DELIMITER + condition + DELIMITER + state + DELIMITER + price + "\n";
    }
}

