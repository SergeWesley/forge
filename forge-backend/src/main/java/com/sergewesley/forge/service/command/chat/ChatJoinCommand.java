package com.sergewesley.forge.service.command.chat;

import com.sergewesley.forge.engine.ChatEngine;
import com.sergewesley.forge.service.command.base.AbstractChatCommand;
import org.springframework.stereotype.Component;

@Component
public class ChatJoinCommand extends AbstractChatCommand {

    public ChatJoinCommand(ChatEngine chatEngine) {
        super(chatEngine);
    }

    @Override
    public String getName() {
        return "chat-join";
    }

    @Override
    public String getDescription() {
        return "Joins an existing chat room";
    }

    @Override
    protected String getUsage() {
        return "<code>";
    }

    @Override
    public String executeWithSession(String args, String principalName) {
        String code = args.trim();

        if (code.isEmpty()) {
            return "Usage: chat-join <code>";
        }

        boolean joined = chatEngine.joinRoom(principalName, code);
        if (joined) {
            return "Joined room " + code + ". You are now in chat mode. Type /exit to leave.";
        } else {
            return "Room not found: " + code;
        }
    }
}
