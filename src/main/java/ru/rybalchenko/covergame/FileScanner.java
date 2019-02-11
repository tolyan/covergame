package ru.rybalchenko.covergame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class FileScanner {

    private final Util util = new Util();

    public static void main(String[] args) {
        String dirName = "./";
        if (args.length == 1) {
            dirName = args[0];
        }
        String output = "./out.csv";

        FileScanner scanner = new FileScanner();
        long start = System.currentTimeMillis();
        Set<Product> result = scanner.findProducts(dirName);
        long time = System.currentTimeMillis() - start;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output), 128 * 1024)) {
            for (Product product : result) {
                writer.write(product.toCSV());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Execution time: " + time);
    }

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

    public Set<Product> findProducts(String dirName) {
        List<File> testFiles = extractTestFiles(dirName);
        List<CompletableFuture<ResultSet>> results = new ArrayList<>();

        for (File file : testFiles) {
            CompletableFuture<ResultSet> future = CompletableFuture.supplyAsync(() -> {
                ResultSet result = new ResultSet(20, 1000);
                Path filePath = FileSystems.getDefault().getPath(dirName, file.getName());
                Stream<String> linesStream = null;
                try {
                    linesStream = Files.lines(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (linesStream != null) {
                    linesStream.forEach(line -> {
                        Product product = util.parseLine(line.toCharArray());
                        result.add(product);
                    });
                }
                return result;
            });
            results.add(future);
        }

        Optional<ResultSet> finalResult = results.stream()
                .map(CompletableFuture::join)
                .reduce(ResultSet::merge);

        return finalResult.get().getResult();
    }


}
