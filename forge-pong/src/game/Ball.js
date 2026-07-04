import * as THREE from 'three'
import { GAME_CONFIG } from './config.js'

/**
 * Ball state and physics management
 */
export class Ball {
  constructor() {
    this.position = new THREE.Vector3(0, GAME_CONFIG.BALL_INITIAL_Y, 0)
    this.velocity = new THREE.Vector3(0, 0, 0)
    this.isMoving = false
    this.lastHitBy = null // 'player' | 'opponent' | null
    this.mesh = null
  }

  /**
   * Create the ball mesh
   */
  createMesh() {
    const geometry = new THREE.SphereGeometry(GAME_CONFIG.BALL_RADIUS, 32, 32)
    const material = new THREE.MeshStandardMaterial({ color: 0xffffff })
    this.mesh = new THREE.Mesh(geometry, material)
    this.mesh.position.copy(this.position)
    return this.mesh
  }

  /**
   * Hit the ball in a direction
   */
  hit(by) {
    this.lastHitBy = by
    this.isMoving = true
    
    const randomX = (Math.random() - 0.5) * 0.05
    const upwardVelocity = 0.08
    
    if (by === 'player') {
      // Toward opponent (negative Z)
      this.velocity.set(randomX, upwardVelocity, -GAME_CONFIG.BALL_SPEED)
    } else {
      // Toward player (positive Z)
      this.velocity.set(randomX, upwardVelocity, GAME_CONFIG.BALL_SPEED)
    }
  }

  /**
   * Reset ball to center
   */
  reset() {
    this.position.set(0, GAME_CONFIG.BALL_INITIAL_Y, 0)
    this.velocity.set(0, 0, 0)
    this.isMoving = false
    this.lastHitBy = null
    this.syncMesh()
  }

  /**
   * Update ball physics for one frame
   * @returns {string|null} Event: 'player_missed', 'opponent_missed', 'opponent_hit', null
   */
  update() {
    if (!this.isMoving) return null
    
    // Apply gravity
    this.velocity.y += GAME_CONFIG.GRAVITY
    
    // Update position
    this.position.add(this.velocity)
    
    // Bounce on table surface
    if (this.position.y < GAME_CONFIG.BALL_INITIAL_Y) {
      this.position.y = GAME_CONFIG.BALL_INITIAL_Y
      this.velocity.y = -this.velocity.y * GAME_CONFIG.BOUNCE_DAMPING
      
      if (Math.abs(this.velocity.y) < 0.01) {
        this.velocity.y = 0
      }
    }
    
    // Bounce on side walls
    const halfWidth = GAME_CONFIG.TABLE_WIDTH / 2 - 0.5
    if (Math.abs(this.position.x) > halfWidth) {
      this.position.x = Math.sign(this.position.x) * halfWidth
      this.velocity.x = -this.velocity.x * GAME_CONFIG.SIDE_BOUNCE_DAMPING
    }
    
    // Check opponent hit zone
    if (this.position.z < GAME_CONFIG.OPPONENT_HIT_ZONE_Z && this.lastHitBy === 'player') {
      this.syncMesh()
      return 'opponent_hit'
    }
    
    // Check if ball went past player
    if (this.position.z > GAME_CONFIG.TABLE_Z_END) {
      this.isMoving = false
      this.syncMesh()
      return 'player_missed'
    }
    
    // Check if ball went past opponent
    if (this.position.z < GAME_CONFIG.TABLE_Z_START) {
      this.isMoving = false
      this.syncMesh()
      return 'opponent_missed'
    }
    
    this.syncMesh()
    return null
  }

  /**
   * Check if ball is in player's hit zone
   */
  isInPlayerHitZone() {
    const z = this.position.z
    const min = GAME_CONFIG.PLAYER_HIT_ZONE_Z - GAME_CONFIG.HIT_ZONE_TOLERANCE
    const max = GAME_CONFIG.PLAYER_HIT_ZONE_Z + GAME_CONFIG.HIT_ZONE_TOLERANCE
    return z > min && z < max
  }

  /**
   * Sync mesh position with physics position
   */
  syncMesh() {
    if (this.mesh) {
      this.mesh.position.copy(this.position)
    }
  }
}
