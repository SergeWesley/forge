<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as THREE from 'three'
import { createScene, createCamera, createRenderer } from '../game/SceneFactory.js'
import { GameEngine } from '../game/GameEngine.js'
import { useOrientation } from '../composables/useOrientation.js'
import { useDeviceMotion } from '../composables/useDeviceMotion.js'

// Refs
const canvasRef = ref(null)
const containerRef = ref(null)
const feedback = ref('')
const gameStarted = ref(false)

// Composables
const { isPortrait, getEffectiveDimensions, getAspectRatio } = useOrientation()

// Game instances
let renderer = null
let scene = null
let camera = null
let animationId = null
let gameEngine = null
let playerPaddleMesh = null

// Device motion for swing detection and paddle position
const { isSupported, permissionGranted, permissionDenied, paddleX, debugData, requestPermission: requestMotionPermission } = useDeviceMotion(() => {
  if (gameEngine && gameStarted.value) {
    gameEngine.playerSwing()
  }
})

// Show feedback message
const showFeedback = (message, duration = 500) => {
  feedback.value = message
  setTimeout(() => { feedback.value = '' }, duration)
}

// Handle resize
const handleResize = () => {
  if (!renderer || !camera) return
  
  const { width, height } = getEffectiveDimensions()
  camera.aspect = width / height
  camera.updateProjectionMatrix()
  renderer.setSize(width, height)
}

// Start the game (called by button click)
const startGame = async () => {
  // Request device motion permission (must be in user gesture)
  await requestMotionPermission()
  
  // Start the game regardless of permission (touch/tap fallback available)
  gameStarted.value = true
  
  // Start the game engine
  if (gameEngine) {
    gameEngine.start()
  }
}

// Initialize scene (before game starts)
const initScene = () => {
  const canvas = canvasRef.value
  if (!canvas) return
  
  // Create scene
  scene = createScene()
  camera = createCamera(getAspectRatio())
  
  // Create renderer
  renderer = createRenderer(canvas)
  const { width, height } = getEffectiveDimensions()
  renderer.setSize(width, height)
  
  // Setup game engine
  gameEngine = new GameEngine()
  gameEngine.onHit = () => showFeedback('🏓 HIT!')
  gameEngine.onMiss = () => showFeedback('❌ Missed!', 1000)
  gameEngine.onPoint = () => showFeedback('🎉 Point!', 1000)
  gameEngine.onSwingMiss = () => showFeedback('💨 Missed swing!', 500)
  
  // Add ball to scene
  const ballMesh = gameEngine.getBallMesh()
  scene.add(ballMesh)
  
  // Add player paddle to scene (Group for paddle + handle)
  playerPaddleMesh = new THREE.Group()
  
  // Paddle head (Round and Red)
  // Cylinder: radiusTop, radiusBottom, height(thickness), segments
  const headGeometry = new THREE.CylinderGeometry(0.4, 0.4, 0.1, 32)
  const headMaterial = new THREE.MeshStandardMaterial({ color: 0xff3333, roughness: 0.2 })
  const head = new THREE.Mesh(headGeometry, headMaterial)
  head.rotation.x = Math.PI / 2 // Rotate to face the net
  head.position.y = 0.4 // Lift up slightly
  
  // Paddle handle
  const handleGeometry = new THREE.CylinderGeometry(0.08, 0.08, 0.5, 16) // Thinner handle
  const handleMaterial = new THREE.MeshStandardMaterial({ color: 0x885522 })
  const handle = new THREE.Mesh(handleGeometry, handleMaterial)
  handle.position.y = -0.25
  
  playerPaddleMesh.add(head)
  playerPaddleMesh.add(handle)
  
  playerPaddleMesh.position.set(0, 0.5, 5.5) // Lower global height to clear view
  scene.add(playerPaddleMesh)
  
  // Render loop (runs even before game starts)
  const animate = () => {
    animationId = requestAnimationFrame(animate)
    
    // Update paddle position from device orientation
    if (gameEngine && playerPaddleMesh) {
      gameEngine.setPlayerPaddleX(paddleX.value)
      playerPaddleMesh.position.x = gameEngine.getPlayerPaddleWorldX()
    }
    
    if (gameStarted.value) {
      gameEngine.update()
    }
    renderer.render(scene, camera)
  }
  animate()
}

onMounted(() => {
  // Setup listeners
  window.addEventListener('resize', handleResize)
  window.addEventListener('orientationchange', handleResize)
  
  // Keyboard fallback for desktop
  window.addEventListener('keydown', (e) => {
    if (e.code === 'Space' && gameEngine && gameStarted.value) {
      gameEngine.playerSwing()
    }
  })
  
  // Initialize scene
  initScene()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('orientationchange', handleResize)
  if (animationId) cancelAnimationFrame(animationId)
  if (renderer) renderer.dispose()
})
</script>

<template>
  <div class="game-wrapper">
    <div 
      ref="containerRef" 
      class="game-container"
      :class="{ 'portrait-mode': isPortrait }"
    >
      <canvas ref="canvasRef" class="game-canvas"></canvas>
      
      <!-- Start screen overlay -->
      <div v-if="!gameStarted" class="start-overlay">
        <div class="start-content">
          <h1>🏓 Forge Pong</h1>
          <p>Hold your phone like a racket and swing to hit!</p>
          
          <button 
            v-if="!permissionDenied"
            class="start-button" 
            @click="startGame"
          >
            Start Game
          </button>
          
          <div v-else class="permission-error">
            <p>⚠️ Motion permission denied</p>
            <p class="hint">Please allow motion access in your browser settings</p>
            <button class="start-button" @click="startGame">
              Try Again
            </button>
          </div>
          
          <p class="desktop-hint">
            💻 On desktop, use <kbd>Space</kbd> to hit
          </p>
        </div>
      </div>
      
      <!-- Feedback overlay -->
      <Transition name="fade">
        <div v-if="feedback" class="feedback">{{ feedback }}</div>
      </Transition>
      
      <!-- Instructions (during game) -->

    </div>
  </div>
</template>

<style scoped>
.game-wrapper {
  width: 100vw;
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
  background: #0a0a0a;
  overflow: hidden;
}

.game-container {
  width: 100%;
  height: 100%;
  position: relative;
}

/* Rotate container for landscape in portrait mode */
.game-container.portrait-mode {
  transform: rotate(90deg);
  transform-origin: center center;
  width: 100vh;
  height: 100vw;
  position: absolute;
  top: 50%;
  left: 50%;
  margin-left: -50vh;
  margin-top: -50vw;
}

.game-canvas {
  display: block;
}

/* Start overlay */
.start-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 30;
}

.start-content {
  text-align: center;
  color: white;
  padding: 2rem;
}

.start-content h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.start-content p {
  font-size: 1rem;
  color: #ccc;
  margin-bottom: 1.5rem;
}

.start-button {
  background: linear-gradient(135deg, #00ff88 0%, #00cc66 100%);
  color: #000;
  border: none;
  padding: 1rem 3rem;
  font-size: 1.2rem;
  font-weight: bold;
  border-radius: 50px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.start-button:hover {
  transform: scale(1.05);
  box-shadow: 0 0 30px rgba(0, 255, 136, 0.5);
}

.start-button:active {
  transform: scale(0.98);
}

.permission-error {
  background: rgba(255, 0, 0, 0.2);
  padding: 1rem;
  border-radius: 10px;
  margin-bottom: 1rem;
}

.permission-error .hint {
  font-size: 0.8rem;
  color: #ff6666;
}

.desktop-hint {
  margin-top: 2rem;
  font-size: 0.8rem;
  color: #666;
}

.desktop-hint kbd {
  background: #333;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-family: monospace;
}

/* Feedback */
.feedback {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 3rem;
  color: white;
  text-shadow: 0 0 20px rgba(0, 0, 0, 0.8);
  pointer-events: none;
  z-index: 20;
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
