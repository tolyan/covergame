package ru.rybalchenko.covergame;

import javax.xml.crypto.Data;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class FileScanner {

    public static final int AVERAGE_LINE_LENGTH = 20;

    public static void main(String[] args) throws IOException {
        FileScanner scanner = new FileScanner();
        Long start = System.currentTimeMillis();
        String result = scanner.findProducts();
        Long time = System.currentTimeMillis() - start;
        System.out.println(result);
        System.out.println("Time: " + time);
    }

    private final static char DELIMITER = ',';
    private final static int BUFFER_SIZE = 32 * 1024; // typical L1 cache size per core

    private List<File> extractTestFiles(String dirName) {
        File dir = new File(dirName);
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IllegalStateException("No input files in directory " + dirName);
        }
        List<File> result = new ArrayList<>();
        for (File file : files) {
            if (file.getName().startsWith("testFile")) {
                result.add(file);
            }
        }
        return result;
    }

    public String findProducts() throws IOException {
        String dirName = "./src/test/resources";
        List<File> testFiles = extractTestFiles(dirName);
        Result result = new Result(1000, 20);

        for (File file : testFiles) {
            Path filePath = FileSystems.getDefault().getPath(dirName, file.getName());
            Stream<String> linesStream = Files.lines(filePath);
            StringBuffer currentWord = new StringBuffer();
            linesStream.forEach(line -> {
                Product product = parseLine(line.toCharArray(), currentWord);
                result.add(product);
            });
        }

        return result.toString();
    }

    private void processProduct(Product product){


    }

    private Product parseLine(char[] rawLine, StringBuffer currentWord) {
        recycleBuilder(currentWord);
        int columnIndex = 0;
        int id = -1;
        String name = "";
        String condition = "";
        String state = "";
        for (char symbol : rawLine) {
            if (symbol == DELIMITER) {
                switch (columnIndex) {
                    case 0: id = Integer.valueOf(currentWord.toString());
                    case 1: name = currentWord.toString();
                    case 2: condition = currentWord.toString();
                    case 3: state = currentWord.toString();
                }
                columnIndex++;
                recycleBuilder(currentWord);
            } else {
                currentWord.append(symbol);
            }
        }
        float price = Float.valueOf(currentWord.toString());
        return new Product(id, name, condition, state, price);
    }

    private void recycleBuilder(StringBuffer currentWord) {
        currentWord.setLength(0);
        currentWord.ensureCapacity(AVERAGE_LINE_LENGTH);
    }
}
