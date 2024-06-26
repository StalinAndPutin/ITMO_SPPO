package org.example.commands;

import org.example.Collection.ExecutionResponse;
import org.example.console.Console;
import org.example.managers.CollectionManager;

import java.util.List;

/**
 *  "clear" - очистить коллекцию
 */
public class Clear extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Clear(Console console, CollectionManager collectionManager) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;
        this.console = console;
    }
    @Override
    public ExecutionResponse apply(List<Object> list) {

        collectionManager.clearCollection();
        return new ExecutionResponse("Коллекция успешно очищена");
    }
}
