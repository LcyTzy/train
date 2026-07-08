import request from './request'

export function createPay(data: { orderNo: string }) {
  return request.post('/pay/create', data)
}

export function queryPay(orderNo: string) {
  return request.get(`/pay/query/${orderNo}`)
}
