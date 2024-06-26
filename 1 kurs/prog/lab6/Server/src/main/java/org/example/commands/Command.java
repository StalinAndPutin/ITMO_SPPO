package org.example.commands;

import org.example.Collection.Describable;
import org.example.Collection.Executable;

/**
 * Абстрактный класс для команд (паттерн команд)
 */

public abstract class Command implements Describable, Executable {
    private final String name;
    private final String description;
    private boolean dropServer;


    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setDropServer(boolean dropServer) {
        this.dropServer = dropServer;
    }

    /**
     * @return Название и использование команды.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Описание команды.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Command command = (Command) obj;
        return name.equals(command.name) && description.equals(command.description);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + description.hashCode();
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}