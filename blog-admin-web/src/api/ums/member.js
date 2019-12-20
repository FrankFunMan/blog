import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/ums/UmsMember/list',
    method:'get',
    params:params
  })
}
export function createMember(data) {
  return request({
    url:'/ums/UmsMember/create',
    method:'post',
    data:data
  })
}
export function updateShowStatus(data) {
  return request({
    url:'/ums/UmsMember/update/showStatus',
    method:'post',
    data:data
  })
}

export function updateFactoryStatus(data) {
  return request({
    url:'/ums/UmsMember/update/factoryStatus',
    method:'post',
    data:data
  })
}

export function deleteMember(id) {
  return request({
    url:'/ums/UmsMember/delete/'+id,
    method:'get',
  })
}

export function getMember(id) {
  return request({
    url:'/ums/UmsMember/'+id,
    method:'get',
  })
}

export function updateMember(id,data) {
  return request({
    url:'/ums/UmsMember/update/'+id,
    method:'post',
    data:data
  })
}

export function fetchBlanceList(id) {
  return request({
    url:'/ums/UmsMember/fetchBlanceList/'+id,
    method:'get',
  })
}
