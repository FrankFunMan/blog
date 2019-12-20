import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/marking/SmsHomeBrand/list',
    method:'get',
    params:params
  })
}

export function updateRecommendStatus(data) {
  return request({
    url:'/marking/SmsHomeBrand/update/recommendStatus',
    method:'post',
    data:data
  })
}

export function deleteHomeBrand(data) {
  return request({
    url:'/marking/SmsHomeBrand/delete',
    method:'post',
    data:data
  })
}

export function createHomeBrand(data) {
  return request({
    url:'/marking/SmsHomeBrand/create',
    method:'post',
    data:data
  })
}

export function updateHomeBrandSort(params) {
  return request({
    url:'/marking/SmsHomeBrand/update/sort/'+params.id,
    method:'post',
    params:params
  })
}
