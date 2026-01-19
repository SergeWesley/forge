<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue';
import { Terminal } from 'xterm';
import { FitAddon } from 'xterm-addon-fit';
import 'xterm/css/xterm.css';
import { useTerminalInput } from '../composables/useTerminalInput';
import { useWebSocket } from '../composables/useWebSocket';

const terminalContainer = ref(null);
let term = null;
let fitAddon = null;

// WebSocket Hook
let wsClient = null;

const handleCommand = (cmd) => {
  // If local commands (handled in useTerminalInput -> enterHandler) didn't catch it,
  // it comes here. Now we send it to the backend via WebSocket.
  if (wsClient && wsClient.isConnected.value) {
     wsClient.sendCommand(cmd);
  } else {
     term.writeln('Error: Server disconnected.');
     term.write('$ ');
  }
};

onMounted(() => {
  term = new Terminal({
    cursorBlink: true,
    fontFamily: '"Fira Code", monospace',
    fontSize: 14,
    theme: {
      background: '#0d1117',
      foreground: '#c9d1d9',
      cursor: '#58a6ff',
      black: '#0d1117',
      red: '#ff7b72',
      green: '#3fb950',
      yellow: '#d29922',
      blue: '#58a6ff',
      magenta: '#bc8cff',
      cyan: '#39c5cf',
      white: '#b1bac4',
      brightBlack: '#484f58',
      brightRed: '#ffa198',
      brightGreen: '#56d364',
      brightYellow: '#e3b341',
      brightBlue: '#79c0ff',
      brightMagenta: '#d2a8ff',
      brightCyan: '#56d4dd',
      brightWhite: '#f0f6fc',
    },
    fontFamily: '"JetBrains Mono", "Fira Code", monospace',
    fontSize: 14,
  });

  fitAddon = new FitAddon();
  term.loadAddon(fitAddon);

  term.open(terminalContainer.value);
  fitAddon.fit();

  term.writeln('\x1b[1;32mWelcome to FORGE Terminal v1.0.0\x1b[0m');
  term.writeln('System ready. Initializing connection...');
  
  // Initialize WebSocket
  wsClient = useWebSocket((output) => {
    // When message received from server
    term.writeln(output.replace(/\n/g, '\r\n'));
    term.write('$ ');
  }, () => {
    // onConnected
    term.writeln('Connected to server.');
    term.write('$ ');
  });
  wsClient.connect();

  term.writeln('');
  term.write('$ ');

  const { handleInput } = useTerminalInput(term, handleCommand);
  term.onData(handleInput);

  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  if (term) term.dispose();
  if (wsClient) wsClient.disconnect();
});

const handleResize = () => {
  if (fitAddon) fitAddon.fit();
};
</script>

<template>
  <div class="terminal-wrapper" ref="terminalContainer"></div>
</template>

<style scoped>
.terminal-wrapper {
  width: 100vw;
  height: 100vh;
  background-color: #0d1117;
  padding: 10px;
  box-sizing: border-box;
  overflow: hidden; 
}

:deep(.xterm-viewport) {
  overflow-y: auto;
}
</style>
