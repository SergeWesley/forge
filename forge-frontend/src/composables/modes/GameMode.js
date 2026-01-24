/**
 * Base class for game modes.
 * Extend this class to create new game modes.
 */
export class GameMode {
  constructor() {
    this.term = null;
    this.active = false;
  }

  /**
   * Called when entering this mode
   */
  onEnter(term) {
    this.term = term;
    this.active = true;
  }

  /**
   * Called when exiting this mode
   */
  onExit() {
    this.active = false;
    this.term = null;
  }

  /**
   * Handle input while in this mode
   * @returns {boolean} true if input was handled
   */
  handleInput(data) {
    return false;
  }

  /**
   * Check if mode is active
   */
  isActive() {
    return this.active;
  }
}
