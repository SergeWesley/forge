export function handleCtrlC({ term, commandBuffer }) {
  term.write('^C\r\n$ ');
  commandBuffer.value = '';
}

export function handleCtrlL({ term, commandBuffer }) {
  // Use ANSI escape code to Clear Screen (2J) and Move Cursor to Top Left (H)
  term.write('\x1b[2J\x1b[H');
  term.write('$ ' + commandBuffer.value);
}
