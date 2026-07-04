export const history = (term, args, { history }) => {
  history.value.forEach((cmd, index) => {
    term.writeln(`  ${index + 1}  ${cmd}`);
  });
};
