package org.example.commands;

import org.example.Collection.ExecutionResponse;
import org.example.console.Console;

/**
 * "exit" - выйти из программы без сохранения
 */
public class Exit extends Command {
    private final Console console;

    public Exit(Console console){
        super("exit", "выйти из программы без сохранения");
        this.console = console;
    }
    @Override
    public ExecutionResponse apply(String argument) {
            console.println("Выход из программы");
            System.exit(1);
            return new ExecutionResponse("huy pososi");
    }
}
