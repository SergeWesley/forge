package com.sergewesley.forge.service.command;

import org.springframework.stereotype.Component;

@Component
public class EchoCommand implements Command {
    @Override
    public String getName() {
        return "echo";
    }

    @Override
    public String execute(String args) {
        return args != null ? args : "";
    }

    @Override
    public String getDescription() {
        return "Echoes input text.";
    }
}
