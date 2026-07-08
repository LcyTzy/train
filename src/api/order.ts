import request from './request'

export function submitTicket(data: {
  trainId: number
  trainDate: string
  startStationId: number
  endStationId: number
  seatType: number
  passengerIds: number[]
}) {
  return request.post('/order/ticket/submit', data)
}

export function getTicketResult(requestNo: string) {
  return request.get(`/order/ticket/result/${requestNo}`)
}

export function getOrderList(params: { status?: number; page: number; size: number }) {
  return request.get('/order/list', { params })
}

export function getOrderDetail(orderNo: string) {
  return request.get(`/order/detail/${orderNo}`)
}

export function cancelOrder(orderNo: string) {
  return request.post(`/order/cancel/${orderNo}`)
}

export function refundOrder(orderNo: string) {
  return request.post(`/order/refund/${orderNo}`)
}
