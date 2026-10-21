import http from './http'

export const login = (data) => http.post('/users/login', data)
export const getMe = () => http.get('/users/me')

export const searchShows = (params) => http.get('/search/list', { params })
export const getShowPage = (params) => http.get('/shows/page', { params })
export const getShow = (id) => http.get(`/shows/${id}`)

export const getSelections = () => http.get('/selections')
export const addSelection = (data) => http.post('/selections', data)
export const updateSelection = (data) => http.put('/selections', data)
export const deleteSelection = (id) => http.delete(`/selections/${id}`)
export const getAddresses = () => http.get('/addresses')

export const createOrder = (data) => http.post('/orders', data)
export const getOrders = (params) => http.get('/orders', { params })
export const getOrder = (id) => http.get(`/orders/${id}`)
export const cancelOrder = (id) => http.put(`/orders/${id}/cancel`)

export const createPayOrder = (data) => http.post('/pay-orders', data)
export const payByBalance = (id, pw) => http.post(`/pay-orders/${id}`, { id, pw })

export const reindex = () => http.post('/search/reindex')
