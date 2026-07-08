import request from './request'

export function register(data: { username: string; password: string; phone: string }) {
  return request.post('/user/register', data)
}

export function login(data: { username: string; password: string }) {
  return request.post('/user/login', data)
}

export function getUserInfo() {
  return request.get('/user/info')
}

export function getPassengerList() {
  return request.get('/user/passenger/list')
}

export function addPassenger(data: {
  name: string
  idType: number
  idNumber: string
  passengerType: number
  phone: string
}) {
  return request.post('/user/passenger/add', data)
}

export function deletePassenger(id: number) {
  return request.delete(`/user/passenger/${id}`)
}
