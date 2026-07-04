import { ref, onUnmounted } from 'vue'
import { GAME_CONFIG } from '../game/config.js'

/**
 * Composable for detecting device motion (swing) and orientation (paddle position)
 * Handles iOS permission request properly
 */
export function useDeviceMotion(onSwing) {
  const isSupported = ref(true)
  const permissionGranted = ref(false)
  const permissionDenied = ref(false)
  const paddleX = ref(0) // -1 (left) to 1 (right)
  
  let lastAcceleration = 0
  let isListening = false

  const handleMotion = (event) => {
    // Debug rotation rate
    if (debugData.value && event.rotationRate) {
      debugData.value.rAlpha = Math.round(event.rotationRate.alpha || 0)
      debugData.value.rBeta = Math.round(event.rotationRate.beta || 0)
      debugData.value.rGamma = Math.round(event.rotationRate.gamma || 0)
    }

    const rr = event.rotationRate
    if (!rr) return
    
    // Detect swing based on Rotation Rate (Gyroscope)
    // The user describes "tilting screen down to floor" -> Rotation around X axis (Alpha)
    // But depending on landscape orientation, it might be Beta.
    
    // We'll check for a strong rotation on ANY main axis to be safe initially.
    // Threshold usually around 300-500 deg/s for a quick flick.
    const SWING_ROTATION_THRESHOLD = 300 
    
    const maxRotation = Math.max(
      Math.abs(rr.alpha || 0), 
      Math.abs(rr.beta || 0), 
      Math.abs(rr.gamma || 0)
    )
    
    if (maxRotation > SWING_ROTATION_THRESHOLD && !isListening.inCooldown && onSwing) {
       // Simple debounce
       if (!handleMotion.lastSwing || Date.now() - handleMotion.lastSwing > 500) {
         handleMotion.lastSwing = Date.now()
         onSwing()
       }
    }
  }

  const debugData = ref({ 
    alpha: 0, beta: 0, gamma: 0,
    rAlpha: 0, rBeta: 0, rGamma: 0
  })

  const handleOrientation = (event) => {
    // Debug raw values (Orientation)
    if (debugData.value) {
      debugData.value.alpha = Math.round(event.alpha || 0)
      debugData.value.beta = Math.round(event.beta || 0)
      debugData.value.gamma = Math.round(event.gamma || 0)
    }

    const beta = event.beta || 0
    const gamma = event.gamma || 0
    
    let tilt = 0
    
    // HYSTERESIS for stability
    // We track if we are currently in "Upright Mode"
    // Switch UP if Gamma > 65 (enter zone)
    // Switch DOWN if Gamma < 55 (leave zone)
    // This prevents flickering when holding device near 60 degrees.
    
    const UPRIGHT_THRESHOLD_ENTER = 65
    const UPRIGHT_THRESHOLD_EXIT = 55
    
    if (!handleOrientation.isUpright) {
      if (Math.abs(gamma) > UPRIGHT_THRESHOLD_ENTER) handleOrientation.isUpright = true
    } else {
      if (Math.abs(gamma) < UPRIGHT_THRESHOLD_EXIT) handleOrientation.isUpright = false
    }
    
    if (handleOrientation.isUpright) {
      // UPRIGHT MODE (Landscape held vertically)
      // We use BETA relative to 180 degrees.
      
      if (beta > 90) {
        tilt = beta - 180
      } else if (beta < -90) {
        tilt = beta + 180
      } else {
        tilt = beta
      }
    } else {
      // FLAT MODE
      tilt = gamma
    }
    
    // Normalize tilt to -1..1 range
    // 45 degrees tilt is enough for full reach (less sensitive, more stable)
    const maxTilt = 45
    let targetNormalized = Math.max(-1, Math.min(1, tilt / maxTilt))
    
    // SMOOTHING (Lerp)
    // Lower value = smoother/slower movement (filters jitter)
    // 0.1 gives a good "weight" to the paddle
    const smoothing = 0.1
    const current = paddleX.value
    
    // Simple Lerp
    paddleX.value = current + (targetNormalized - current) * smoothing
  }
  
  // Initialize static property for hysteresis state
  handleOrientation.isUpright = false

  const startListening = () => {
    if (isListening) return
    isListening = true
    window.addEventListener('devicemotion', handleMotion)
    window.addEventListener('deviceorientation', handleOrientation)
  }

  const stopListening = () => {
    isListening = false
    window.removeEventListener('devicemotion', handleMotion)
    window.removeEventListener('deviceorientation', handleOrientation)
  }

  /**
   * Request permission for device motion
   * MUST be called from a user gesture (button click)
   * @returns {Promise<boolean>} true if permission granted
   */
  const requestPermission = async () => {
    // Check if DeviceMotionEvent exists
    if (typeof DeviceMotionEvent === 'undefined') {
      console.log('DeviceMotion not supported')
      isSupported.value = false
      // On desktop, we still return true to allow keyboard fallback
      return true
    }

    // iOS 13+ requires permission request
    if (typeof DeviceMotionEvent.requestPermission === 'function') {
      try {
        console.log('Requesting DeviceMotion permission (iOS)...')
        const permission = await DeviceMotionEvent.requestPermission()
        
        if (permission === 'granted') {
          console.log('DeviceMotion permission granted!')
          permissionGranted.value = true
          permissionDenied.value = false
          startListening()
          return true
        } else {
          console.log('DeviceMotion permission denied')
          permissionDenied.value = true
          return false
        }
      } catch (error) {
        console.error('DeviceMotion permission error:', error)
        permissionDenied.value = true
        return false
      }
    } else {
      // Non-iOS devices (Android, desktop) - no permission needed
      console.log('DeviceMotion available without permission')
      permissionGranted.value = true
      startListening()
      return true
    }
  }

  onUnmounted(() => {
    stopListening()
  })

  return {
    isSupported,
    permissionGranted,
    permissionDenied,
    paddleX, // Paddle position from orientation
    debugData, // Raw sensor data
    requestPermission,
    stopListening
  }
}
