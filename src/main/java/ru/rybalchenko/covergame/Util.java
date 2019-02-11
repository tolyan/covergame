package ru.rybalchenko.covergame;

public class Util {
    public final static char DELIMITER = ',';

    public Product parseLine(char[] rawLine) {
        StringBuilder currentWord = new StringBuilder();
        int columnIndex = 0;
        int id = -1;
        String name = "";
        String condition = "";
        String state = "";
        for (char symbol : rawLine) {
            if (symbol == DELIMITER) {
                switch (columnIndex) {
                    case 0:
                        id = Integer.valueOf(currentWord.toString());
                    case 1:
                        name = currentWord.toString();
                    case 2:
                        condition = currentWord.toString();
                    case 3:
                        state = currentWord.toString();
                }
                columnIndex++;
                currentWord = new StringBuilder();
            } else {
                currentWord.append(symbol);
            }
        }
        float price = Float.valueOf(currentWord.toString());
        return new Product(id, name, condition, state, price);
    }
}
