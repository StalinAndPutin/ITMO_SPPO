package project.lab5.commands;

import project.lab5.Collection.ExecutionResponse;
import project.lab5.console.Console;
import project.lab5.managers.CollectionManager;

/**
 * Команда clear - очистить коллекцию
 */
public class Clear extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public Clear(Console console, CollectionManager collectionManager) {
        super("clear", "Очистить коллекцию");
        this.collectionManager = collectionManager;
        this.console = console;
    }
    @Override
    public ExecutionResponse apply(String argument) {
        if (argument.isEmpty()) return new ExecutionResponse(false,
                "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        collectionManager.clearCollection();
        return new ExecutionResponse("Коллекция успешно очищена");
    }
}
