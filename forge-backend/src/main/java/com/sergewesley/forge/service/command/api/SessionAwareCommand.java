package com.sergewesley.forge.service.command.api;

/**
 * Extended Command interface for commands that require session/user context.
 * Session-aware commands have access to the user's principal name to perform
 * user-specific operations.
 */
public interface SessionAwareCommand extends Command {

    /**
     * Execute the command with session context.
     * 
     * @param args          The command arguments
     * @param principalName The user's principal name (session identifier)
     * @return The command output
     */
    String executeWithSession(String args, String principalName);
}
