import { reactive } from 'vue'

function readUser() {
  try {
    return JSON.parse(localStorage.getItem('st_user') || 'null')
  } catch {
    return null
  }
}

export const auth = reactive({
  token: localStorage.getItem('st_token') || '',
  user: readUser()
})

export function setLogin(data) {
  auth.token = data.token
  auth.user = {
    userId: data.userId,
    username: data.username,
    phone: data.phone,
    balance: data.balance
  }
  localStorage.setItem('st_token', auth.token)
  localStorage.setItem('st_user', JSON.stringify(auth.user))
}

export function updateUser(data) {
  auth.user = { ...(auth.user || {}), ...data }
  localStorage.setItem('st_user', JSON.stringify(auth.user))
}

export function logout() {
  auth.token = ''
  auth.user = null
  localStorage.removeItem('st_token')
  localStorage.removeItem('st_user')
}
