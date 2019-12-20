import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/marking/SmsCouponHistory/list',
    method:'get',
    params:params
  })
}
