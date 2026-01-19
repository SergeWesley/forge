package com.sergewesley.forge.service.command;

import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {

    // Dynamic help deferred closer to Strategy Pattern cleanup.
    // For now, static help string.

    @Override
    public String getName() {
        return "server-help";
    }

    @Override
    public String execute(String args) {
        return "Available Server Commands:\n - status\n - echo <text>\n - server-help";
    }

    @Override
    public String getDescription() {
        return "Lists server commands.";
    }
}
