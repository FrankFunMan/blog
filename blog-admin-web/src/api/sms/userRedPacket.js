import request from '@/utils/request'
export function fetchList(params) {
    return request({
        url:'/marking/SmsUserRedPacket/list',
        method:'get',
        params:params
    })
}
export function createUserRedPacket(data) {
    return request({
        url:'/marking/SmsUserRedPacket/create',
        method:'post',
        data:data
    })
}

export function deleteUserRedPacket(id) {
    return request({
        url:'/marking/SmsUserRedPacket/delete/'+id,
        method:'get',
    })
}

export function getUserRedPacket(id) {
    return request({
        url:'/marking/SmsUserRedPacket/'+id,
        method:'get',
    })
}

export function updateUserRedPacket(id,data) {
    return request({
        url:'/marking/SmsUserRedPacket/update/'+id,
        method:'post',
        data:data
    })
}

