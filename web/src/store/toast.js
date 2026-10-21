import { reactive } from 'vue'

export const toasts = reactive([])
let seq = 0

export function toast(message, type = 'info', duration = 3200) {
  const item = { id: ++seq, message, type }
  toasts.push(item)
  window.setTimeout(() => {
    const index = toasts.indexOf(item)
    if (index >= 0) toasts.splice(index, 1)
  }, duration)
}
