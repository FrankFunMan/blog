import request from '@/utils/request'

export function fetchList(params) {
  return request({
    url: '/sys/sysRole/list',
    method: 'get',
    params: params
  })
}

export function createRole(data) {
  return request({
    url: '/sys/sysRole/create',
    method: 'post',
    data: data
  })
}

export function updateRole(data) {
  return request({
    url: '/sys/sysRole/update/',
    method: 'post',
    data: data
  })
}

export function updateRoleStatus(data) {
  return request({
    url: '/sys/sysRole/updateRoleStatus',
    method: 'post',
    data: data
  })
}

export function updateBatchRoleStatus(data) {
  return request({
    url: '/sys/sysRole/update/batch/status',
    method: 'post',
    data: data
  })
}

export function deleteRole(id) {
  return request({
    url: '/sys/sysRole/delete/' + id,
    method: 'delete',
  })
}

export function rolePermission(id) {
  return request({
    url: '/sys/sysRole/permission/' + id,
    method: 'get'
  })
}






export function deleteBatchRole(data) {
  return request({
    url: '/sys/sysRole/delete/batch/',
    method: 'post',
    data: data
  })
}

export function getRole(id) {
  return request({
    url: '/sys/sysRole/' + id,
    method: 'get',
  })
}


