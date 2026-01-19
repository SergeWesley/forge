package com.sergewesley.forge.service;

import com.sergewesley.forge.dto.CommandResponse;
import com.sergewesley.forge.service.command.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class CommandEngineService {

    private final Map<String, Command> commandMap = new HashMap<>();

    @Autowired
    public CommandEngineService(List<Command> commands) {
        for (Command cmd : commands) {
            commandMap.put(cmd.getName().toLowerCase(), cmd);
        }
    }

    public CommandResponse processCommand(String commandLine) {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return new CommandResponse("");
        }

        String[] parts = commandLine.trim().split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        Command cmd = commandMap.get(commandName);
        if (cmd != null) {
            return new CommandResponse(cmd.execute(args));
        }

        return new CommandResponse("Unknown server command: " + commandName);
    }
}
