import { Ball } from './Ball.js'
import { GAME_CONFIG } from './config.js'

/**
 * Game engine managing game state and logic
 */
export class GameEngine {
  constructor() {
    this.ball = new Ball()
    this.score = { player: 0, opponent: 0 }
    this.state = 'waiting' // 'waiting' | 'playing' | 'paused'
    this.canSwing = true
    this.playerPaddleX = 0 // -1 (left) to 1 (right), updated from device orientation
    
    // Callbacks
    this.onHit = null
    this.onMiss = null
    this.onPoint = null
    this.onSwingMiss = null // Swing but paddle not in position
  }

  /**
   * Start a new game
   */
  start() {
    this.ball.reset()
    this.state = 'playing'
    
    // Opponent serves after delay
    setTimeout(() => {
      this.opponentServe()
    }, GAME_CONFIG.SERVE_DELAY_MS)
  }

  /**
   * Update player paddle position (from device orientation)
   * @param {number} normalizedX - Position from -1 (left) to 1 (right)
   */
  setPlayerPaddleX(normalizedX) {
    this.playerPaddleX = normalizedX
  }

  /**
   * Opponent serves the ball
   */
  opponentServe() {
    if (this.state !== 'playing') return
    this.ball.hit('opponent')
  }

  /**
   * Player attempts to hit the ball
   * @returns {boolean} true if hit was successful
   */
  playerSwing() {
    if (!this.canSwing || this.state !== 'playing') return false
    
    // Check timing (ball in Z hit zone)
    if (!this.ball.isInPlayerHitZone()) {
      return false
    }
    
    // Check positioning (paddle X close to ball X)
    const paddleReach = GAME_CONFIG.PADDLE_REACH || 1.5 // How far paddle can reach
    const paddleWorldX = this.playerPaddleX * (GAME_CONFIG.TABLE_WIDTH / 2 - paddleReach / 2)
    const ballX = this.ball.position.x
    const distance = Math.abs(ballX - paddleWorldX)
    
    if (distance > paddleReach) {
      // Swing but paddle not in position!
      this.triggerCooldown()
      if (this.onSwingMiss) this.onSwingMiss()
      return false
    }
    
    // Successful hit!
    this.ball.hit('player')
    this.triggerCooldown()
    
    if (this.onHit) this.onHit()
    return true
  }

  /**
   * Trigger swing cooldown
   */
  triggerCooldown() {
    this.canSwing = false
    setTimeout(() => {
      this.canSwing = true
    }, GAME_CONFIG.SWING_COOLDOWN_MS)
  }

  /**
   * Update game state (call every frame)
   */
  update() {
    if (this.state !== 'playing') return
    
    const event = this.ball.update()
    
    if (event === 'opponent_hit') {
      // Opponent auto-hits back
      this.ball.hit('opponent')
    } else if (event === 'player_missed') {
      this.score.opponent++
      if (this.onMiss) this.onMiss()
      this.scheduleReset()
    } else if (event === 'opponent_missed') {
      this.score.player++
      if (this.onPoint) this.onPoint()
      this.scheduleReset()
    }
  }

  /**
   * Schedule ball reset and new serve
   */
  scheduleReset() {
    setTimeout(() => {
      this.ball.reset()
      setTimeout(() => {
        this.opponentServe()
      }, GAME_CONFIG.SERVE_DELAY_MS)
    }, GAME_CONFIG.RESET_DELAY_MS)
  }

  /**
   * Get ball mesh for scene
   */
  getBallMesh() {
    return this.ball.createMesh()
  }

  /**
   * Get current player paddle X position in world coordinates
   */
  getPlayerPaddleWorldX() {
    const paddleReach = GAME_CONFIG.PADDLE_REACH || 1.5
    return this.playerPaddleX * (GAME_CONFIG.TABLE_WIDTH / 2 - paddleReach / 2)
  }
}
