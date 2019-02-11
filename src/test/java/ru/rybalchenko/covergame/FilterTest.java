package ru.rybalchenko.covergame;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.rybalchenko.covergame.generator.FileGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FilterTest {
    private static final String SRC_TEST_RESOURCES = "./src/test/resources/";
    private static Set<Product> refProducts = new TreeSet<>();
    private static Util util = new Util();

    @BeforeClass
    public static void generate() throws IOException {
        FileGenerator generator = new FileGenerator();
        List<String> ref = generator.generateRefProducts();
        for (String productString : ref) {
            refProducts.add(util.parseLine(productString.toCharArray()));
        }
        generator.writeFiles(SRC_TEST_RESOURCES, 1000000, 5, ref);
    }

    @Test
    public void testFilter() {
        FileScanner scanner = new FileScanner();
        Set<Product> result = scanner.findProducts(SRC_TEST_RESOURCES);
        result.retainAll(refProducts);
        Assert.assertEquals(result.size(), 1000);
    }
}
