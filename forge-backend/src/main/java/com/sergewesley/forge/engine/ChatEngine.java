package com.sergewesley.forge.engine;

import com.sergewesley.forge.dto.CommandResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatEngine {

    private final SimpMessagingTemplate messagingTemplate;

    // Room Code -> Set of User Principals
    private final Map<String, Set<String>> rooms = new ConcurrentHashMap<>();

    // User Principal -> Room Code
    private final Map<String, String> userRoomMap = new ConcurrentHashMap<>();

    @Autowired
    public ChatEngine(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public boolean isUserInChat(String principalName) {
        return userRoomMap.containsKey(principalName);
    }

    public String createRoom(String creatorPrincipal) {
        String roomCode = generateRoomCode();
        rooms.put(roomCode, ConcurrentHashMap.newKeySet());
        joinRoom(creatorPrincipal, roomCode);
        return roomCode;
    }

    public boolean joinRoom(String principalName, String roomCode) {
        if (!rooms.containsKey(roomCode)) {
            return false;
        }

        // Remove from old room if present
        leaveRoom(principalName);

        rooms.get(roomCode).add(principalName);
        userRoomMap.put(principalName, roomCode);

        broadcastSystemMessage(roomCode, "User connected.");
        return true;
    }

    public void leaveRoom(String principalName) {
        String roomCode = userRoomMap.remove(principalName);
        if (roomCode != null && rooms.containsKey(roomCode)) {
            rooms.get(roomCode).remove(principalName);

            // Cleanup empty room
            if (rooms.get(roomCode).isEmpty()) {
                rooms.remove(roomCode);
            } else {
                broadcastSystemMessage(roomCode, "User disconnected.");
            }
        }
    }

    public void broadcastMessage(String senderPrincipal, String message) {
        String roomCode = userRoomMap.get(senderPrincipal);
        if (roomCode != null && rooms.containsKey(roomCode)) {
            Set<String> participants = rooms.get(roomCode);

            String formattedMessage = "[USER]: " + message;
            CommandResponse response = new CommandResponse(formattedMessage, "chat");

            for (String participant : participants) {
                // Don't echo back to sender if you want, but typical chat shows your own msg
                // Here we send to everyone
                messagingTemplate.convertAndSendToUser(
                        participant,
                        "/queue/terminal",
                        response);
            }
        }
    }

    public void broadcastTyping(String senderPrincipal, String currentText) {
        String roomCode = userRoomMap.get(senderPrincipal);
        if (roomCode != null && rooms.containsKey(roomCode)) {
            Set<String> participants = rooms.get(roomCode);

            Map<String, String> typingData = new HashMap<>();
            typingData.put("type", "typing");
            typingData.put("text", currentText);

            for (String participant : participants) {
                // Don't send typing events back to sender
                if (!participant.equals(senderPrincipal)) {
                    messagingTemplate.convertAndSendToUser(
                            participant,
                            "/queue/typing",
                            typingData);
                }
            }
        }
    }

    private void broadcastSystemMessage(String roomCode, String message) {
        Set<String> participants = rooms.get(roomCode);
        if (participants == null)
            return;

        CommandResponse response = new CommandResponse("[SYSTEM]: " + message, "system");

        for (String participant : participants) {
            messagingTemplate.convertAndSendToUser(
                    participant,
                    "/queue/terminal",
                    response);
        }
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
