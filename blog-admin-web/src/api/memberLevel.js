import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/ums/UmsMemberLevel/list',
    method:'get',
    params:params
  })
}
