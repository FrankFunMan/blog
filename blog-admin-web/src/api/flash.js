import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/marking/SmsFlashPromotion/list',
    method:'get',
    params:params
  })
}
export function updateStatus(id,params) {
  return request({
    url:'/marking/SmsFlashPromotion/update/status/'+id,
    method:'post',
    params:params
  })
}
export function deleteFlash(id) {
  return request({
    url:'/marking/SmsFlashPromotion/delete/'+id,
    method:'post'
  })
}
export function createFlash(data) {
  return request({
    url:'/marking/SmsFlashPromotion/create',
    method:'post',
    data:data
  })
}
export function updateFlash(id,data) {
  return request({
    url:'/marking/SmsFlashPromotion/update/'+id,
    method:'post',
    data:data
  })
}
