package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Orders;
import com.example.entity.Tourism;
import com.example.exception.CustomException;
import com.example.mapper.OrdersMapper;
import com.example.mapper.TourismMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 业务层方法
 */
@Service
public class OrdersService {

    // 注入 OrdersMapper 和 TourismMapper，用于操作数据库
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private TourismMapper tourismMapper;

    /**
     * 添加订单的方法（下单功能）
     * 1. 获取当前登录用户信息。
     * 2. 设置订单的基本信息（用户 ID、时间、状态、订单编号等）。
     * 3. 检查商品库存是否充足。
     * 4. 插入订单记录，并更新商品库存。
     */
    @Transactional  // 开启事务管理，确保操作的原子性（要么全部成功，要么全部失败）
    public void add(Orders orders) {
        // 获取当前登录用户信息
        Account currentUser = TokenUtils.getCurrentUser();
        // 设置订单的用户 ID 为当前用户的 ID
        orders.setUserId(currentUser.getId());
        // 设置订单时间为当前时间
        orders.setTime(DateUtil.now());
        // 设置订单状态为“待支付”
        orders.setStatus("待支付");
        // 使用雪花算法生成唯一的订单编号，它一种分布式唯一ID生成算法。它能够生成全局唯一的、趋势递增的64位长整型ID，适用于分布式系统环境下的ID生成需求。
        orders.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        // 获取订单关联的商品 ID
        Integer tourismId = orders.getTourismId();
        // 根据商品 ID 查询商品信息
        Tourism tourism = tourismMapper.selectById(tourismId);
        // 如果商品不存在，抛出自定义异常
        if (tourism == null) {
            throw new CustomException("500", "商品不存在");
        }
        // 如果商品库存不足，抛出自定义异常
        if (tourism.getStore() < orders.getNum()) {
            throw new CustomException("500", "库存不足");
        }
        // 插入订单记录到数据库
        ordersMapper.insert(orders);
        // 更新商品库存（减少已下单的数量）
        tourism.setStore(tourism.getStore() - orders.getNum());
        tourismMapper.updateById(tourism);
    }
    /**
     * 根据主键 ID 更新订单信息
     * 调用 Mapper 层的 updateById 方法实现更新操作。
     */
    public void updateById(Orders orders) {
        ordersMapper.updateById(orders);
    }
    /**
     * 根据主键 ID 删除订单
     * 调用 Mapper 层的 deleteById 方法实现删除操作。
     */
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }
    /**
     * 批量删除订单
     * 遍历传入的 ID 列表，逐个调用 deleteById 方法删除订单。
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            ordersMapper.deleteById(id);
        }
    }
    /**
     * 根据主键 ID 查询订单信息
     * 调用 Mapper 层的 selectById 方法实现查询操作。
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }
    /**
     * 查询所有订单信息
     * 调用 Mapper 层的 selectAll 方法实现查询操作。
     * 可以根据传入的 Orders 对象作为条件进行筛选。
     */
    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }
    /**
     * 分页查询订单信息
     * 1. 获取当前登录用户信息。
     * 2. 如果是普通用户，则限制只能查询自己的订单。
     * 3. 使用 PageHelper 实现分页查询。
     * 4. 返回分页结果（PageInfo）。
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        // 获取当前登录用户信息
        Account currentUser = TokenUtils.getCurrentUser();

        // 如果当前用户是普通用户，限制只能查询自己的订单
        if (currentUser.getRole().equals(RoleEnum.USER.name())) {
            orders.setUserId(currentUser.getId());
        }
        // 使用 PageHelper 开始分页
        PageHelper.startPage(pageNum, pageSize);
        // 查询符合条件的订单列表
        List<Orders> list = ordersMapper.selectAll(orders);
        // 将查询结果封装为分页对象并返回
        return PageInfo.of(list);
    }
    /**
     * 根据订单编号查询订单信息
     * 调用 Mapper 层的 selectByOrderNo 方法实现查询操作。
     */
    public Orders selectByOrderNo(String orderNo) {
        return ordersMapper.selectByOrderNo(orderNo);
    }
}