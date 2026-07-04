import { ref } from 'vue';

/**
 * Singleton that manages the current active mode (e.g., game mode).
 * When a mode is active, all terminal input is delegated to it.
 */
const currentMode = ref(null);
const terminalRef = ref(null);

export const ModeManager = {
  /**
   * Set the terminal reference for modes to use
   */
  setTerminal(term) {
    terminalRef.value = term;
  },

  getTerminal() {
    return terminalRef.value;
  },

  /**
   * Enter a new mode
   */
  enterMode(mode) {
    if (currentMode.value) {
      currentMode.value.onExit();
    }
    currentMode.value = mode;
    if (mode && terminalRef.value) {
      mode.onEnter(terminalRef.value);
    }
  },

  /**
   * Exit current mode and return to normal terminal
   */
  exitMode() {
    if (currentMode.value) {
      currentMode.value.onExit();
      currentMode.value = null;
    }
  },

  /**
   * Check if a mode is currently active
   */
  isActive() {
    return currentMode.value !== null;
  },

  /**
   * Get current mode
   */
  getCurrentMode() {
    return currentMode.value;
  },

  /**
   * Handle input - returns true if mode handled it
   */
  handleInput(data) {
    if (currentMode.value) {
      return currentMode.value.handleInput(data);
    }
    return false;
  }
};
