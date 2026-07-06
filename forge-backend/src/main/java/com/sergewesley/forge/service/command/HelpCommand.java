package com.sergewesley.forge.service.command;

import com.sergewesley.forge.service.command.api.Command;
import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {

    private final List<Command> commands;

    // Use @Lazy to break circular dependency: CommandEngine -> HelpCommand ->
    // CommandEngine (via list)
    public HelpCommand(@Lazy List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public String getName() {
        return "server-help";
    }

    @Override
    public String execute(String args) {
        StringBuilder sb = new StringBuilder("Available Server Commands:\n");
        for (Command cmd : commands) {
            sb.append(" - ").append(cmd.getName());
            if (cmd.getDescription() != null && !cmd.getDescription().isEmpty()) {
                sb.append(": ").append(cmd.getDescription());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getDescription() {
        return "Lists server commands.";
    }
}
