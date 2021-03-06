import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/marking/SmsHomeNewProduct/list',
    method:'get',
    params:params
  })
}

export function updateRecommendStatus(data) {
  return request({
    url:'/marking/SmsHomeNewProduct/update/recommendStatus',
    method:'post',
    data:data
  })
}

export function deleteNewProduct(data) {
  return request({
    url:'/marking/SmsHomeNewProduct/delete',
    method:'post',
    data:data
  })
}

export function createNewProduct(data) {
  return request({
    url:'/marking/SmsHomeNewProduct/create',
    method:'post',
    data:data
  })
}

export function updateNewProductSort(params) {
  return request({
    url:'/marking/SmsHomeNewProduct/update/sort/'+params.id,
    method:'post',
    params:params
  })
}
