import { ref, onMounted, onUnmounted } from 'vue'

/**
 * Composable for handling orientation and responsive canvas
 */
export function useOrientation() {
  const isPortrait = ref(false)

  const checkOrientation = () => {
    isPortrait.value = window.innerHeight > window.innerWidth
  }

  const getEffectiveDimensions = () => {
    if (isPortrait.value) {
      // Swap dimensions for landscape rendering in portrait mode
      return { width: window.innerHeight, height: window.innerWidth }
    }
    return { width: window.innerWidth, height: window.innerHeight }
  }

  const getAspectRatio = () => {
    const { width, height } = getEffectiveDimensions()
    return width / height
  }

  onMounted(() => {
    checkOrientation()
    window.addEventListener('resize', checkOrientation)
    window.addEventListener('orientationchange', checkOrientation)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', checkOrientation)
    window.removeEventListener('orientationchange', checkOrientation)
  })

  return {
    isPortrait,
    checkOrientation,
    getEffectiveDimensions,
    getAspectRatio
  }
}
