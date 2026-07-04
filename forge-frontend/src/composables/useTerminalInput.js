import { ref } from 'vue';
import { KEY_CODES } from '../constants/keys';
import { handleEnter } from './handlers/enterHandler';
import { handleBackspace } from './handlers/backspaceHandler';
import { handleCtrlC, handleCtrlL } from './handlers/shortcutHandlers';
import { handlePrintable } from './handlers/printableHandler';

export function useTerminalInput(term, onCommand) {
  const commandBuffer = ref('');
  const history = ref([]);
  const historyIndex = ref(-1);

  const context = {
    term,
    commandBuffer, // ref
    history,       // ref
    historyIndex,  // ref
    onCommand
  };

  const handleInput = (data) => {
    if (!term) return;

    const code = data.charCodeAt(0);

    switch (code) {
      case KEY_CODES.ENTER:
        handleEnter(context);
        break;

      case KEY_CODES.BACKSPACE:
        handleBackspace(context);
        break;

      case KEY_CODES.CTRL_C:
        handleCtrlC(context);
        break;

      case KEY_CODES.CTRL_L:
        handleCtrlL(context);
        break;

      case KEY_CODES.ESCAPE:
        // Future: Arrow keys handling
        break;

      default:
        // Regular printable characters
        if (code >= KEY_CODES.SPACE && code <= KEY_CODES.TILDE) {
          handlePrintable(context, data);
        }
        break;
    }
  };

  return {
    handleInput
  };
}
