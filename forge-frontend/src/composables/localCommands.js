import { clear } from './commands/clear';
import { date } from './commands/date';
import { time } from './commands/time';
import { help } from './commands/help';
import { whoami } from './commands/whoami';
import { version } from './commands/version';
import { echo } from './commands/echo';
import { history } from './commands/history';

export const localCommands = {
  clear,
  date,
  time,
  help,
  whoami,
  version,
  echo,
  history
};

export function executeLocalCommand(cmd, term, context) {
  const parts = cmd.trim().split(' ');
  const commandName = parts[0];
  const args = parts.slice(1);

  if (localCommands[commandName]) {
    localCommands[commandName](term, args, context);
    return true;
  }
  return false;
}
