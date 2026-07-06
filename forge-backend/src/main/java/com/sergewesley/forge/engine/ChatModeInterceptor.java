package com.sergewesley.forge.engine;

import com.sergewesley.forge.dto.CommandResponse;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Intercepts command input when a user is in chat mode. Handles chat-specific behavior (/exit,
 * message broadcasting) before commands reach the CommandEngine.
 */
@Component
public class ChatModeInterceptor {

    private final ChatEngine chatEngine;

    public ChatModeInterceptor(ChatEngine chatEngine) {
        this.chatEngine = chatEngine;
    }

    /**
     * Intercepts and processes input if user is in chat mode.
     *
     * @param commandLine The input line
     * @param principalName The user's principal
     * @return Optional containing the response if handled, empty if should proceed to CommandEngine
     */
    public Optional<CommandResponse> intercept(String commandLine, String principalName) {
        if (principalName == null || !chatEngine.isUserInChat(principalName)) {
            return Optional.empty();
        }

        // User is in chat mode
        if ("/exit".equalsIgnoreCase(commandLine.trim())) {
            chatEngine.leaveRoom(principalName);
            return Optional.of(new CommandResponse("Exited chat room.", "info"));
        } else {
            chatEngine.broadcastMessage(principalName, commandLine);
            // Return null wrapped in Optional to signal message was broadcasted
            return Optional.of(null);
        }
    }
}
