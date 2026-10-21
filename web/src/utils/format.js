export function formatPrice(cents) {
  if (cents === null || cents === undefined) return '--'
  return `¥${(Number(cents) / 100).toFixed(2)}`
}

export function formatDate(value) {
  if (!value) return '--'
  return String(value).replace('T', ' ').slice(0, 16)
}

export function formatStatus(status) {
  const map = {
    1: { text: '待支付', tone: 'warn' },
    2: { text: '已支付待出票', tone: 'info' },
    3: { text: '已出票', tone: 'success' },
    4: { text: '已完成', tone: 'success' },
    5: { text: '已关闭', tone: 'muted' },
    6: { text: '已评价', tone: 'muted' }
  }
  return map[status] || { text: '未知状态', tone: 'muted' }
}

export function errorMessage(error, fallback = '请求失败，请稍后重试') {
  const data = error?.response?.data
  if (typeof data === 'string' && data) return data
  return data?.msg || data?.message || data?.error || error?.message || fallback
}

export function firstChar(text) {
  return text ? String(text).trim().slice(0, 1) : '演'
}
