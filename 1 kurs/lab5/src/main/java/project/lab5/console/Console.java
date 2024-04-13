package project.lab5.console;

import java.util.Scanner;

/**
 * Консоль для ввода команд и вывода результата
 */
public interface Console {
    void print(Object o);
    void println(Object o);
    String readln();
    boolean isCanReadln();
    void printError(Object o);
    void printTable(Object o1, Object o2);
    void prompt();
    String getPrompt();
    void selectFileScanner(Scanner o);
    void selectConsoleScanner();
}
