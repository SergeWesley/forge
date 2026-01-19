export const clear = (term) => {
  term.write('\x1b[2J\x1b[H');
};
