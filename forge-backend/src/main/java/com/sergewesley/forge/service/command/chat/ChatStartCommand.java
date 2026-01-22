package com.sergewesley.forge.service.command.chat;

import com.sergewesley.forge.engine.ChatEngine;
import com.sergewesley.forge.service.command.base.AbstractChatCommand;
import org.springframework.stereotype.Component;

@Component
public class ChatStartCommand extends AbstractChatCommand {

    public ChatStartCommand(ChatEngine chatEngine) {
        super(chatEngine);
    }

    @Override
    public String getName() {
        return "chat-start";
    }

    @Override
    public String getDescription() {
        return "Starts a new chat room and returns a unique code";
    }

    @Override
    protected String getUsage() {
        return "";
    }

    @Override
    public String executeWithSession(String args, String principalName) {
        String code = chatEngine.createRoom(principalName);
        return "Chat room started. Code: " + code;
    }
}
