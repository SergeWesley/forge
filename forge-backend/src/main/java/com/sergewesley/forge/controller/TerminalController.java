package com.sergewesley.forge.controller;

import com.sergewesley.forge.dto.CommandRequest;
import com.sergewesley.forge.dto.CommandResponse;
import com.sergewesley.forge.dto.TypingEvent;
import com.sergewesley.forge.engine.ChatEngine;
import com.sergewesley.forge.engine.CommandEngine;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
// import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class TerminalController {

    private final CommandEngine commandEngine;
    private final ChatEngine chatEngine;

    @Autowired
    public TerminalController(CommandEngine commandEngine, ChatEngine chatEngine) {
        this.commandEngine = commandEngine;
        this.chatEngine = chatEngine;
    }

    @MessageMapping("/command")
    @SendToUser("/queue/terminal")
    public CommandResponse handleCommand(CommandRequest request, Principal principal) {
        String command = request.getCommand();
        // A simple logging or side-effect could be added here
        return commandEngine.processCommand(command, principal.getName());
    }

    @MessageMapping("/typing")
    public void handleTyping(TypingEvent typingEvent, Principal principal) {
        chatEngine.broadcastTyping(principal.getName(), typingEvent.getCurrentText());
    }
}
