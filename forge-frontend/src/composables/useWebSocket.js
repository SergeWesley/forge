import { Client } from '@stomp/stompjs';
import { ref } from 'vue';

export function useWebSocket(onMessageReceived, onConnected) {
  const client = ref(null);
  const isConnected = ref(false);

  const connect = () => {
    client.value = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      onConnect: () => {
        isConnected.value = true;
        if (onConnected) onConnected();
        
        client.value.subscribe('/topic/terminal', (message) => {
          if (message.body) {
            const response = JSON.parse(message.body);
            onMessageReceived(response.output);
          }
        });
      },
      onDisconnect: () => {
        isConnected.value = false;
      },
      onWebSocketClose: () => {
          isConnected.value = false;
      }
    });

    client.value.activate();
  };

  const sendCommand = (command) => {
    if (client.value && client.value.connected) {
      client.value.publish({
        destination: '/app/command',
        body: JSON.stringify({ command: command }),
      });
    } else {
      console.error('WebSocket is not connected.');
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
    isConnected
  };
}
