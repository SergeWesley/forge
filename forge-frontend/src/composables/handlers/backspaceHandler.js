export function handleBackspace({ term, commandBuffer }) {
  if (commandBuffer.value.length > 0) {
    commandBuffer.value = commandBuffer.value.slice(0, -1);
    term.write('\b \b');
  }
}
