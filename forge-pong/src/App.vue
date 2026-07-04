<script setup>
import PongGame from './components/PongGame.vue'
</script>

<template>
  <PongGame />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #0a0a0a;
  /* Force landscape feel */
  touch-action: none;
  -webkit-touch-callout: none;
  user-select: none;
}
</style>
