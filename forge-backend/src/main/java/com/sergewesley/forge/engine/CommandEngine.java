package com.sergewesley.forge.engine;

import com.sergewesley.forge.dto.CommandResponse;
import com.sergewesley.forge.service.command.api.Command;
import com.sergewesley.forge.service.command.api.SessionAwareCommand;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandEngine {

    private final Map<String, Command> commandMap = new HashMap<>();
    private final ChatModeInterceptor chatModeInterceptor;

    @Autowired
    public CommandEngine(List<Command> commands, ChatModeInterceptor chatModeInterceptor) {
        for (Command cmd : commands) {
            commandMap.put(cmd.getName().toLowerCase(), cmd);
        }
        this.chatModeInterceptor = chatModeInterceptor;
    }

    public CommandResponse processCommand(String commandLine) {
        return processCommand(commandLine, null);
    }

    public CommandResponse processCommand(String commandLine, String principalName) {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return new CommandResponse("");
        }

        // Intercept if user is in chat mode
        Optional<CommandResponse> intercepted =
                chatModeInterceptor.intercept(commandLine, principalName);
        if (intercepted.isPresent()) {
            return intercepted.get();
        }

        String[] parts = commandLine.trim().split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        Command cmd = commandMap.get(commandName);
        if (cmd != null) {
            // Check if command is session-aware
            if (cmd instanceof SessionAwareCommand && principalName != null) {
                String output = ((SessionAwareCommand) cmd).executeWithSession(args, principalName);
                return new CommandResponse(output);
            } else {
                return new CommandResponse(cmd.execute(args));
            }
        }

        return new CommandResponse("Unknown server command: " + commandName);
    }
}
