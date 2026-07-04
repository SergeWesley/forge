/**
 * Platform detection utilities
 */

const mobileRegex = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i;

export function usePlatform() {
  const isMobile = () => mobileRegex.test(navigator.userAgent);
  const isDesktop = () => !isMobile();

  return {
    isMobile,
    isDesktop
  };
}

// Standalone export for non-Vue usage (like in classes)
export const isMobile = () => mobileRegex.test(navigator.userAgent);
export const isDesktop = () => !isMobile();
