package org.example.commands;


import org.example.Collection.Executable;
import org.example.Collection.ExecutionResponse;
import org.example.console.Console;

import java.util.List;

/**
 *  "execute_script" - исполнить скрипт из указанного файла
 */
public class ExecuteScript extends Command implements Executable {
    private final Console console;

    public ExecuteScript(Console console) {
        super("execute_script file_name", "исполнить скрипт из указанного файла");
        this.console = console;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse apply(List<Object> list) {
//        if (list.get(1).toString().isEmpty()) return new ExecutionResponse(false, "Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        return new ExecutionResponse("");
    }
}