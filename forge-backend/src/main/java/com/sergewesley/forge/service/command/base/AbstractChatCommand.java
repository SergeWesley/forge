package com.sergewesley.forge.service.command.base;

import com.sergewesley.forge.engine.ChatEngine;
import com.sergewesley.forge.service.command.api.SessionAwareCommand;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract base class for all chat-related commands. Provides common functionality and access to
 * the ChatEngine.
 */
public abstract class AbstractChatCommand implements SessionAwareCommand {

    protected final ChatEngine chatEngine;

    @Autowired
    public AbstractChatCommand(ChatEngine chatEngine) {
        this.chatEngine = chatEngine;
    }

    @Override
    public String execute(String args) {
        return "Usage: " + getName() + " " + getUsage();
    }

    /**
     * Returns the usage string for this command (arguments only).
     *
     * @return Usage string without the command name
     */
    protected abstract String getUsage();
}
