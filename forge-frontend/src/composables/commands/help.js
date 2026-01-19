import { localCommands } from '../localCommands';

export const help = (term) => {
  term.writeln('Available commands:');
  const commands = Object.keys(localCommands).sort();
  commands.forEach(cmd => {
    term.writeln(`  - ${cmd}`);
  });
  term.writeln('  - <any other command> will be sent to the server');
};
