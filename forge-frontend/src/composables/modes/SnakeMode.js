import { GameMode } from './GameMode.js';
import { ModeManager } from './ModeManager.js';
import { isMobile } from '../usePlatform.js';

/**
 * Snake game as a modal game mode.
 * Extends GameMode and handles all snake game logic.
 */
export class SnakeMode extends GameMode {
  constructor() {
    super();
    this.WIDTH = 30;
    this.HEIGHT = 15;
    this.snake = [];
    this.direction = { x: 1, y: 0 };
    this.food = { x: 20, y: 7 };
    this.score = 0;
    this.gameOver = false;
    this.gameInterval = null;
    this.dpad = null;
    
    this.colors = {
      green: '\x1b[32m',
      brightGreen: '\x1b[92m',
      red: '\x1b[91m',
      yellow: '\x1b[33m',
      cyan: '\x1b[36m',
      reset: '\x1b[0m',
      dim: '\x1b[2m'
    };
  }

  onEnter(term) {
    super.onEnter(term);
    
    // Responsive sizing
    const termCols = term.cols || 80;
    const maxWidth = Math.floor(termCols * 0.9);
    const targetWidth = termCols > 80 ? Math.floor(termCols * 0.5) : maxWidth;
    this.WIDTH = Math.max(20, Math.min(60, targetWidth));
    this.HEIGHT = Math.floor(this.WIDTH * 0.5);
    
    this.startNewGame();
  }

  startNewGame() {
    // Reset game state
    this.snake = [{ x: Math.floor(this.WIDTH / 2), y: Math.floor(this.HEIGHT / 2) }];
    this.direction = { x: 1, y: 0 };
    this.score = 0;
    this.gameOver = false;
    
    this.spawnFood();
    
    // Show instructions
    this.term.write('\x1b[2J'); // Clear screen
    this.term.write('\x1b[H');  // Home
    
    this.term.writeln(`${this.colors.brightGreen}🐍 SNAKE GAME${this.colors.reset}`);
    this.term.writeln(`${this.colors.dim}Arrows/D-pad: Move | Q: Quit | R: Restart${this.colors.reset}`);
    this.term.writeln('');
    
    // Create D-pad
    this.createDpad();
    
    // On mobile: blur to close virtual keyboard
    // On desktop: keep focus for keyboard input
    if (isMobile()) {
      this.term.blur();
    } else {
      this.term.focus();
    }
    
    // Start game loop
    this.gameInterval = setInterval(() => this.update(), 150);
    
    this.drawGame();
  }

  onExit() {
    // Stop game loop
    if (this.gameInterval) {
      clearInterval(this.gameInterval);
      this.gameInterval = null;
    }
    
    // Remove D-pad controls
    this.removeDpad();
    
    // Clear screen and restore terminal
    if (this.term) {
      this.term.write('\x1b[2J'); // Clear entire screen
      this.term.write('\x1b[H');  // Move cursor to home
      this.term.writeln('Thanks for playing Snake! 🐍');
      this.term.write('$ ');
      this.term.focus(); // Restore focus for next commands
    }
    
    super.onExit();
  }

  handleInput(data) {
    // Handle game controls
    if (data === '\x1b[A' && this.direction.y !== 1) { // Up
      this.direction = { x: 0, y: -1 };
    } else if (data === '\x1b[B' && this.direction.y !== -1) { // Down
      this.direction = { x: 0, y: 1 };
    } else if (data === '\x1b[C' && this.direction.x !== -1) { // Right
      this.direction = { x: 1, y: 0 };
    } else if (data === '\x1b[D' && this.direction.x !== 1) { // Left
      this.direction = { x: -1, y: 0 };
    } else if (data.toLowerCase() === 'q') {
      // Quit
      ModeManager.exitMode();
    } else if (data.toLowerCase() === 'r') {
      // Restart
      if (this.gameInterval) clearInterval(this.gameInterval);
      this.removeDpad();
      this.startNewGame();
    }
    
    return true; // Always consume input in game mode
  }

  setDirection(dx, dy) {
    if (this.direction.x !== -dx || this.direction.y !== -dy) {
      this.direction = { x: dx, y: dy };
    }
  }

  spawnFood() {
    do {
      this.food = {
        x: Math.floor(Math.random() * (this.WIDTH - 2)) + 1,
        y: Math.floor(Math.random() * (this.HEIGHT - 2)) + 1
      };
    } while (this.snake.some(s => s.x === this.food.x && s.y === this.food.y));
  }

  update() {
    if (this.gameOver) return;
    
    const newHead = {
      x: this.snake[0].x + this.direction.x,
      y: this.snake[0].y + this.direction.y
    };
    
    // Wall collision
    if (newHead.x < 0 || newHead.x >= this.WIDTH || newHead.y < 0 || newHead.y >= this.HEIGHT) {
      this.endGame();
      return;
    }
    
    // Self collision
    if (this.snake.some(s => s.x === newHead.x && s.y === newHead.y)) {
      this.endGame();
      return;
    }
    
    this.snake.unshift(newHead);
    
    if (newHead.x === this.food.x && newHead.y === this.food.y) {
      this.score += 10;
      this.spawnFood();
    } else {
      this.snake.pop();
    }
    
    this.drawGame();
  }

  drawGame() {
    this.term.write('\x1b[4;1H'); // Move to line 4 (after header)
    
    let output = '';
    output += this.colors.cyan + '╔' + '═'.repeat(this.WIDTH) + '╗' + this.colors.reset + '\r\n';
    
    for (let y = 0; y < this.HEIGHT; y++) {
      output += this.colors.cyan + '║' + this.colors.reset;
      
      for (let x = 0; x < this.WIDTH; x++) {
        const isHead = this.snake[0].x === x && this.snake[0].y === y;
        const isBody = this.snake.slice(1).some(s => s.x === x && s.y === y);
        const isFood = this.food.x === x && this.food.y === y;
        
        if (isHead) {
          output += this.colors.brightGreen + '●' + this.colors.reset;
        } else if (isBody) {
          output += this.colors.green + '○' + this.colors.reset;
        } else if (isFood) {
          output += this.colors.red + '◆' + this.colors.reset;
        } else {
          output += ' ';
        }
      }
      
      output += this.colors.cyan + '║' + this.colors.reset + '\r\n';
    }
    
    output += this.colors.cyan + '╚' + '═'.repeat(this.WIDTH) + '╝' + this.colors.reset + '\r\n';
    output += `${this.colors.yellow}Score: ${this.score}${this.colors.reset}\r\n`;
    
    this.term.write(output);
  }

  endGame() {
    this.gameOver = true;
    if (this.gameInterval) clearInterval(this.gameInterval);
    
    this.term.write('\r\n');
    this.term.writeln(`${this.colors.red}╔════════════════════════╗${this.colors.reset}`);
    this.term.writeln(`${this.colors.red}║      GAME OVER!        ║${this.colors.reset}`);
    this.term.writeln(`${this.colors.red}║   Final Score: ${this.score.toString().padStart(4)}     ║${this.colors.reset}`);
    this.term.writeln(`${this.colors.red}║   R: Restart  Q: Quit  ║${this.colors.reset}`);
    this.term.writeln(`${this.colors.red}╚════════════════════════╝${this.colors.reset}`);
  }

  createDpad() {
    this.dpad = document.createElement('div');
    this.dpad.id = 'snake-dpad';
    this.dpad.innerHTML = `
      <style>
        #snake-dpad {
          position: fixed;
          bottom: 10px;
          left: 50%;
          transform: translateX(-50%);
          display: grid;
          grid-template-columns: repeat(3, 60px);
          grid-template-rows: repeat(3, 60px);
          gap: 5px;
          padding: 20px;
          z-index: 1000;
          touch-action: none;
          -webkit-touch-callout: none;
        }
        .dpad-btn {
          background: rgba(0, 255, 0, 0.3);
          border: 2px solid #00ff00;
          border-radius: 10px;
          color: #00ff00;
          font-size: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          user-select: none;
          -webkit-tap-highlight-color: transparent;
          pointer-events: none;
        }
        .dpad-btn:active, .dpad-btn.active {
          background: rgba(0, 255, 0, 0.6);
        }
        .dpad-cell { 
          background: transparent; 
          pointer-events: none;
        }
      </style>
      <div class="dpad-cell"></div>
      <button class="dpad-btn" data-dir="up">▲</button>
      <div class="dpad-cell"></div>
      <button class="dpad-btn" data-dir="left">◀</button>
      <div class="dpad-cell"></div>
      <button class="dpad-btn" data-dir="right">▶</button>
      <div class="dpad-cell"></div>
      <button class="dpad-btn" data-dir="down">▼</button>
      <div class="dpad-cell"></div>
    `;
    document.body.appendChild(this.dpad);

    const buttons = this.dpad.querySelectorAll('.dpad-btn');
    
    // Find closest button to touch point
    const findClosestButton = (x, y) => {
      let closest = null;
      let minDist = Infinity;
      
      buttons.forEach(btn => {
        const rect = btn.getBoundingClientRect();
        const centerX = rect.left + rect.width / 2;
        const centerY = rect.top + rect.height / 2;
        const dist = Math.sqrt((x - centerX) ** 2 + (y - centerY) ** 2);
        
        if (dist < minDist) {
          minDist = dist;
          closest = btn;
        }
      });
      
      return closest;
    };
    
    // Trigger direction based on button
    const triggerDirection = (btn) => {
      if (!btn) return;
      const dir = btn.dataset.dir;
      if (dir === 'up') this.setDirection(0, -1);
      else if (dir === 'down') this.setDirection(0, 1);
      else if (dir === 'left') this.setDirection(-1, 0);
      else if (dir === 'right') this.setDirection(1, 0);
      
      // Visual feedback
      btn.classList.add('active');
      setTimeout(() => btn.classList.remove('active'), 100);
    };
    
    // Handle touch on entire D-pad area
    this.dpad.addEventListener('touchstart', (e) => {
      e.preventDefault();
      e.stopPropagation();
      const touch = e.touches[0];
      const btn = findClosestButton(touch.clientX, touch.clientY);
      triggerDirection(btn);
    }, { passive: false });
    
    // Handle mouse click for desktop
    this.dpad.addEventListener('click', (e) => {
      e.preventDefault();
      e.stopPropagation();
      const btn = findClosestButton(e.clientX, e.clientY);
      triggerDirection(btn);
      if (!isMobile()) this.term.focus();
    });
  }

  removeDpad() {
    if (this.dpad) {
      this.dpad.remove();
      this.dpad = null;
    }
  }
}
