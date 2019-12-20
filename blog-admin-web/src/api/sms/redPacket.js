import request from '@/utils/request'
export function fetchList(params) {
    return request({
        url:'/marking/SmsRedPacket/list',
        method:'get',
        params:params
    })
}
export function createRedPacket(data) {
    return request({
        url:'/marking/SmsRedPacket/create',
        method:'post',
        data:data
    })
}

export function deleteRedPacket(id) {
    return request({
        url:'/marking/SmsRedPacket/delete/'+id,
        method:'get',
    })
}

export function getRedPacket(id) {
    return request({
        url:'/marking/SmsRedPacket/'+id,
        method:'get',
    })
}

export function updateRedPacket(id,data) {
    return request({
        url:'/marking/SmsRedPacket/update/'+id,
        method:'post',
        data:data
    })
}

