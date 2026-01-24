import { executeLocalCommand } from "../processors/localCommandProcessor";

export function handleEnter({ term, commandBuffer, history, historyIndex, onCommand }) {
  term.write('\r\n');
  const cmd = commandBuffer.value.trim();

  if (cmd.length > 0) {
    history.value.push(cmd);
    historyIndex.value = history.value.length;

    // Try local command first
    const isHandledLocally = executeLocalCommand(cmd, term, { history });

    // If not local, send to server (via onCommand callback)
    if (!isHandledLocally && onCommand) {
      onCommand(cmd);
    }
    
    if (isHandledLocally) {
       term.write('$ ');
    }
  } else {
    term.write('$ ');
  }
  commandBuffer.value = '';
}
