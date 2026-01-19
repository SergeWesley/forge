export function handlePrintable({ term, commandBuffer }, data) {
  commandBuffer.value += data;
  term.write(data);
}
