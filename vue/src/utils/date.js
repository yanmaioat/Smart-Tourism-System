/**
 * 日期格式化工具函数
 * @param {number|string|Date} timestamp - 时间戳或日期对象
 * @param {string} format - 格式化模板，默认：'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(timestamp, format = 'YYYY-MM-DD HH:mm:ss') {
    const date = new Date(timestamp)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')

    return format
        .replace('YYYY', year)
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hours)
        .replace('mm', minutes)
        .replace('ss', seconds)
}

/**
 * 获取友好的时间显示
 * @param {number|string|Date} timestamp - 时间戳或日期对象
 * @returns {string} 友好的时间显示
 */
export function friendlyTime(timestamp) {
    const now = Date.now()
    const diff = now - new Date(timestamp).getTime()

    if (diff < 60000) { // 1分钟内
        return '刚刚'
    } else if (diff < 3600000) { // 1小时内
        return `${Math.floor(diff / 60000)}分钟前`
    } else if (diff < 86400000) { // 1天内
        return `${Math.floor(diff / 3600000)}小时前`
    } else {
        return formatDate(timestamp, 'MM-DD HH:mm')
    }
}