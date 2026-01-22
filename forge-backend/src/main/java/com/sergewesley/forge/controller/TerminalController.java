package com.sergewesley.forge.controller;

import com.sergewesley.forge.dto.CommandRequest;
import com.sergewesley.forge.dto.CommandResponse;
import com.sergewesley.forge.service.CommandEngineService;
import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class TerminalController {

    private final CommandEngineService commandEngineService;

    @Autowired
    public TerminalController(CommandEngineService commandEngineService) {
        this.commandEngineService = commandEngineService;
    }

    @MessageMapping("/command")
    @org.springframework.messaging.simp.annotation.SendToUser("/queue/terminal")
    public CommandResponse handleCommand(CommandRequest request) {
        String command = request.getCommand();
        // A simple logging or side-effect could be added here
        return commandEngineService.processCommand(command);
    }
}
