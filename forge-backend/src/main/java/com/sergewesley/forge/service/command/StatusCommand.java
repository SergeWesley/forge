package com.sergewesley.forge.service.command;

import com.sergewesley.forge.service.command.api.Command;
import org.springframework.stereotype.Component;

@Component
public class StatusCommand implements Command {
    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String execute(String args) {
        return "System Status: ONLINE\nActive connections: 1\nBackend: Spring Boot Running";
    }

    @Override
    public String getDescription() {
        return "Checks system status.";
    }
}
