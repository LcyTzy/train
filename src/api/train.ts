import request from './request'

export function getStationList(keyword?: string) {
  const params: any = {}
  if (keyword) params.keyword = keyword
  return request.get('/train/station/list', { params })
}

export function queryTrains(params: {
  startStation: string
  endStation: string
  trainDate: string
  trainType?: number
  departTimeStart?: string
  departTimeEnd?: string
}) {
  return request.get('/train/query', { params })
}

export function getTrainDetail(trainId: number) {
  return request.get(`/train/detail/${trainId}`)
}

export function queryStock(params: {
  trainId: number
  trainDate: string
  startStationId: number
  endStationId: number
}) {
  return request.get('/ticket/stock', { params })
}
