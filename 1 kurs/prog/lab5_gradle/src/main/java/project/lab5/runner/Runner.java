package project.lab5.runner;

import project.lab5.Collection.ExecutionResponse;
import project.lab5.commands.Command;
import project.lab5.console.Console;
import project.lab5.managers.CommandManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс для запуска программы и работы со скриптом
 */

public class Runner {
    private final Console console;
    private final CommandManager commandManager;;
    private final List<String> scriptStack = new ArrayList<>(1024);
    private int lengthRecursion = -1;


    public Runner(Console console, CommandManager commandManager) {
        this.console = console;
        this.commandManager = commandManager;
    }

    /**
     * Интерактивный режим
     */
    public void interactiveMode() throws Exception {
        try {
            ExecutionResponse commandStatus;
            String[] userCommand = {"", ""};

            while (true) {
                console.prompt();
                userCommand = (console.readln().trim() + " ").split(" ", 2);
                userCommand[0] = userCommand[0].trim();
                userCommand[1] = userCommand[1].trim();
                commandStatus = launchCommand(userCommand);

                if (commandStatus.getMessage().equals("exit")) break;
                console.println(commandStatus.getMessage());
            }
        } catch (Exception exception) {
            console.printError("Непредвиденная ошибка!");
        }

    }

    private boolean checkRecursion(String argument, Scanner scriptScanner) {
        var i = 1;
        for (String script : scriptStack) {
            i++;
            if (argument.equals(script)) {
                if (lengthRecursion < 0) {
                    console.selectConsoleScanner();
                    console.println("Была замечена рекурсия! Введите максимальную глубину рекурсии (0..500)");
                    while (lengthRecursion < 0 || lengthRecursion > 500) {
                        try { console.print("> "); lengthRecursion = Integer.parseInt(console.readln().trim()); } catch (NumberFormatException e) { console.println("длина не распознана"); }
                    }
                    console.selectFileScanner(scriptScanner);
                }

                if (i > lengthRecursion || i > 500){
                    lengthRecursion = -1;
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Режим для запуска скрипта.
     * @param argument Аргумент скрипта
     * @return Код завершения.
     */
    private ExecutionResponse scriptMode(String argument) {
        String[] userCommand = {"", ""};
        StringBuilder executionOutput = new StringBuilder();

        if (!new File(argument).exists()) return new ExecutionResponse(false, "Файл не существет!");
        if (!Files.isReadable(Paths.get(argument))) return new ExecutionResponse(false, "Прав для чтения нет!");

        scriptStack.add(argument);
        try (Scanner scriptScanner = new Scanner(new File(argument))) {

            ExecutionResponse commandStatus;

            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            console.selectFileScanner(scriptScanner);
            do {
                userCommand = (console.readln().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                while (console.isCanReadln() && userCommand[0].isEmpty()) {
                    userCommand = (console.readln().trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                }
                executionOutput.append(console.getPrompt() + String.join(" ", userCommand) + "\n");
                boolean needLaunch = true;
                if (userCommand[0].equals("execute_script")) {
                    needLaunch = checkRecursion(userCommand[1], scriptScanner);
                }

                commandStatus = needLaunch ? launchCommand(userCommand) : new ExecutionResponse("Превышена глубина рекурсии");

//                System.out.println(commandStatus);
                if (userCommand[0].equals("execute_script")) console.selectFileScanner(scriptScanner);
                executionOutput.append(commandStatus.getMessage()+"\n");
            } while (!commandStatus.getMessage().equals("exit") && console.isCanReadln());

            console.selectConsoleScanner();

            return new ExecutionResponse(commandStatus.getExitCode(), executionOutput.toString());
        } catch (FileNotFoundException exception) {
            return new ExecutionResponse(false, "Файл со скриптом не найден!");
        } catch (NoSuchElementException exception) {
            return new ExecutionResponse(false, "Файл со скриптом пуст!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            scriptStack.remove(scriptStack.size()-1);
        }
        return new ExecutionResponse("");
    }



    /**
     * Загружает команды.
     * @param userCommand Команда для запуска
     * @return Код завершения.
     */
    private ExecutionResponse launchCommand(String[] userCommand) {
        if (userCommand[0].equals("")) return new ExecutionResponse("");
        Command command = commandManager.getCommands().get(userCommand[0]);

        if (command == null) return new ExecutionResponse(false, "Команда '" + userCommand[0] + "' не найдена.");

        switch (userCommand[0]) {
            case "execute_script":
                ExecutionResponse response1 = command.apply(userCommand[0]);
                if(response1.getExitCode()){
                    ExecutionResponse response2 = scriptMode(userCommand[1]);
                    return new ExecutionResponse(response2.getExitCode(), "\n"+response2.getMessage().trim());
                }
                else{
                    return new ExecutionResponse(response1.getMessage());
                }
            case "remove_by_id", "remove_greater", "counter_greater_than_soundtrack_name", "count_by_mood", "update":
                return command.apply(userCommand[1].trim());
            default:
                return command.apply(userCommand[0]); }
    }
}
