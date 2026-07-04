/**
 * Game constants and configuration
 */
export const GAME_CONFIG = {
  // Table dimensions
  TABLE_WIDTH: 9,
  TABLE_LENGTH: 16,
  TABLE_Z_CENTER: -2,
  TABLE_Z_START: -10,  // Far end (opponent)
  TABLE_Z_END: 6,      // Near end (player)
  
  // Hit zones
  PLAYER_HIT_ZONE_Z: 4,
  OPPONENT_HIT_ZONE_Z: -8,
  HIT_ZONE_TOLERANCE: 2,
  PADDLE_REACH: 1.5, // How far paddle can reach from its center
  
  // Ball physics
  BALL_RADIUS: 0.2,
  BALL_SPEED: 0.15,
  BALL_INITIAL_Y: 0.3,
  GRAVITY: -0.003,
  BOUNCE_DAMPING: 0.7,
  SIDE_BOUNCE_DAMPING: 0.8,
  
  // Swing detection
  SWING_THRESHOLD: 15,
  SWING_COOLDOWN_MS: 500,
  
  // Timing
  SERVE_DELAY_MS: 1500,
  RESET_DELAY_MS: 1000
}
