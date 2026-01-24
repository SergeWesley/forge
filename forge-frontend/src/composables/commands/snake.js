import { ModeManager } from '../modes/ModeManager.js';
import { SnakeMode } from '../modes/SnakeMode.js';

/**
 * Snake command - enters Snake game mode
 */
export function snake(term, args, context) {
  ModeManager.setTerminal(term);
  ModeManager.enterMode(new SnakeMode());
  return true;
}
