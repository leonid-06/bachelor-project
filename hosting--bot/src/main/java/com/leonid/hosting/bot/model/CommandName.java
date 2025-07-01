package com.leonid.hosting.bot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommandName {

    START("/start"),
    MY_APPS("/my-apps"),
    DEPLOY("/deploy");

    private final String commandName;

    static public CommandName value(String str) {
        for (CommandName commandName : CommandName.values()) {
            if (str.equals(commandName.getCommandName())) {
                return commandName;
            }
        }
        throw new IllegalArgumentException("Unknown command name: " + str);
    }
}