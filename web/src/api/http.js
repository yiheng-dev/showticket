import axios from 'axios'
import { auth, logout } from '../store/auth'

const http = axios.create({
  baseURL: '/api',
  timeout: 20000
})

http.interceptors.request.use((config) => {
  if (auth.token) {
    config.headers.Authorization = auth.token
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      logout()
      if (window.location.pathname !== '/login') {
        const redirect = encodeURIComponent(window.location.pathname + window.location.search)
        window.location.assign(`/login?redirect=${redirect}`)
      }
    }
    return Promise.reject(error)
  }
)

export default http
