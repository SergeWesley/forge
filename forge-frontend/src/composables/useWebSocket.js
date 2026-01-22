import { Client } from "@stomp/stompjs";
import { ref } from "vue";

export function useWebSocket(onMessageReceived, onConnected, onTypingReceived) {
  const client = ref(null);
  const isConnected = ref(false);

  const connect = () => {
    client.value = new Client({
      brokerURL: import.meta.env.VITE_WS_URL,
      onConnect: () => {
        isConnected.value = true;
        if (onConnected) onConnected();

        client.value.subscribe("/user/queue/terminal", (message) => {
          if (message.body) {
            const response = JSON.parse(message.body);
            onMessageReceived(response.output);
          }
        });

        // Subscribe to typing events
        client.value.subscribe("/user/queue/typing", (message) => {
          if (message.body) {
            const typingData = JSON.parse(message.body);
            if (onTypingReceived) {
              onTypingReceived(typingData.text);
            }
          }
        });
      },
      onDisconnect: () => {
        isConnected.value = false;
      },
      onWebSocketClose: () => {
        isConnected.value = false;
      },
    });

    client.value.activate();
  };

  const sendCommand = (command) => {
    if (client.value && client.value.connected) {
      client.value.publish({
        destination: "/app/command",
        body: JSON.stringify({ command: command }),
      });
    } else {
      console.error("WebSocket is not connected.");
    }
  };

  const sendTyping = (currentText) => {
    if (client.value && client.value.connected) {
      client.value.publish({
        destination: "/app/typing",
        body: JSON.stringify({ currentText: currentText }),
      });
    }
  };

  const disconnect = () => {
    if (client.value) {
      client.value.deactivate();
    }
  };

  return {
    connect,
    disconnect,
    sendCommand,
    sendTyping,
    isConnected,
  };
}
