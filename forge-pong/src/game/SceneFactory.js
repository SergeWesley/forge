import * as THREE from 'three'
import { GAME_CONFIG } from './config.js'

/**
 * Creates and returns all scene objects
 */
export function createScene() {
  const scene = new THREE.Scene()
  scene.background = new THREE.Color('#228B22') // Forest green
  
  // Lighting
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.8)
  scene.add(ambientLight)
  
  const directionalLight = new THREE.DirectionalLight(0xffffff, 1.5)
  directionalLight.position.set(5, 10, 5)
  scene.add(directionalLight)
  
  const fillLight = new THREE.DirectionalLight(0xffffff, 0.5)
  fillLight.position.set(-5, 5, -5)
  scene.add(fillLight)
  
  // Table
  const tableGeometry = new THREE.BoxGeometry(
    GAME_CONFIG.TABLE_WIDTH, 
    0.2, 
    GAME_CONFIG.TABLE_LENGTH
  )
  const tableMaterial = new THREE.MeshStandardMaterial({ color: 0x1a6b2c })
  const table = new THREE.Mesh(tableGeometry, tableMaterial)
  table.position.set(0, 0, GAME_CONFIG.TABLE_Z_CENTER)
  scene.add(table)
  
  // Center line
  const lineMaterial = new THREE.MeshStandardMaterial({ color: 0xffffff })
  const centerLine = new THREE.Mesh(
    new THREE.BoxGeometry(GAME_CONFIG.TABLE_WIDTH + 0.1, 0.02, 0.05), 
    lineMaterial
  )
  centerLine.position.set(0, 0.12, GAME_CONFIG.TABLE_Z_CENTER)
  scene.add(centerLine)
  
  // Net
  const netMaterial = new THREE.MeshStandardMaterial({ color: 0x333333 })
  const net = new THREE.Mesh(
    new THREE.BoxGeometry(GAME_CONFIG.TABLE_WIDTH + 0.5, 0.25, 0.05), 
    netMaterial
  )
  net.position.set(0, 0.22, GAME_CONFIG.TABLE_Z_CENTER)
  scene.add(net)
  
  // Opponent paddle
  const paddleGeometry = new THREE.BoxGeometry(1.5, 0.1, 0.3)
  const opponentMaterial = new THREE.MeshStandardMaterial({ color: 0xff4444 })
  const opponentPaddle = new THREE.Mesh(paddleGeometry, opponentMaterial)
  opponentPaddle.position.set(0, 0.4, GAME_CONFIG.TABLE_Z_START + 0.5)
  scene.add(opponentPaddle)
  
  // Floor
  const floorGeometry = new THREE.PlaneGeometry(50, 50)
  const floorMaterial = new THREE.MeshStandardMaterial({ color: 0xcccccc })
  const floor = new THREE.Mesh(floorGeometry, floorMaterial)
  floor.rotation.x = -Math.PI / 2
  floor.position.y = -0.5
  scene.add(floor)
  
  return scene
}

/**
 * Creates and configures the camera
 */
export function createCamera(aspectRatio) {
  const camera = new THREE.PerspectiveCamera(75, aspectRatio, 0.1, 100)
  camera.position.set(0, 1.5, 7)
  camera.lookAt(0, 0, -3)
  return camera
}

/**
 * Creates the WebGL renderer
 */
export function createRenderer(canvas) {
  const renderer = new THREE.WebGLRenderer({ canvas, antialias: true })
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  return renderer
}
