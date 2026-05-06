package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.entity.Sale;
import com.example.exception.CustomException;
import com.example.mapper.SaleMapper;
import com.example.mapper.TrainMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 销售相关的业务层服务类
 */
@Service
public class SaleService {

    @Resource
    private SaleMapper saleMapper;// 注入销售数据的mapper接口
    @Resource
    private TrainMapper trainMapper;// 注入火车票数据的mapper接口
    /**
     * 添加销售记录的方法
     *
     * @param sale 要添加的销售对象
     */
    @Transactional
    public void add(Sale sale) {
        Account currentUser = TokenUtils.getCurrentUser(); // 获取当前登录用户信息
        sale.setUserId(currentUser.getId());// 设置购买者的ID
        sale.setTime(DateUtil.now());// 设置购买时间为当前时间
        sale.setStatus("待支付");// 设置订单状态为“待支付”
        sale.setOrderNo(IdUtil.getSnowflakeNextIdStr());  // 订单编号使用雪花算法生成，避免重复
        // 直接下单 之前要判断库存
        Integer trainId = sale.getTrainId();// 查询对应的火车票信息
        Train train = trainMapper.selectById(trainId);// 如果火车票不存在，抛出异常
        if (train == null) {
            throw new CustomException("500", "车票不存在");
        }
        if (train.getStore() < sale.getNum()) {   // 插入新的销售记录到数据库
            throw new CustomException("500", "库存不足");
        }
        saleMapper.insert(sale);// 插入新的销售记录到数据库
        train.setStore(train.getStore() - sale.getNum());// 更新火车票库存
        trainMapper.updateById(train);// 更新火车票信息到数据库

    }
    /**
     * 根据ID更新销售记录的方法
     *
     * @param sale 包含新值的销售对象
     */
    public void updateById(Sale sale) {
        saleMapper.updateById(sale);
    }// 更新指定ID的销售记录
    /**
     * 根据ID删除销售记录的方法
     *
     * @param id 要删除的销售记录ID
     */
    public void deleteById(Integer id) {
        saleMapper.deleteById(id);
    }// 删除指定ID的销售记录
    /**
     * 批量删除销售记录的方法
     *
     * @param ids 要删除的销售记录ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            saleMapper.deleteById(id);// 循环删除每个指定ID的销售记录
        }
    }
    /**
     * 根据ID查询单条销售记录的方法
     *
     * @param id 要查询的销售记录ID
     * @return 查询到的销售对象
     */
    public Sale selectById(Integer id) {
        return saleMapper.selectById(id);
    } // 返回指定ID的销售记录
    /**
     * 查询所有符合条件的销售记录的方法
     *
     * @param sale 包含查询条件的对象
     * @return 符合条件的所有销售记录列表
     */
    public List<Sale> selectAll(Sale sale) {
        return saleMapper.selectAll(sale);
    }// 返回所有符合查询条件的销售记录
    /**
     * 分页查询销售记录的方法
     *
     * @param sale   包含查询条件的对象
     * @param pageNum   当前页码
     * @param pageSize  每页显示数量
     * @return 分页后的销售记录信息
     */
    public PageInfo<Sale> selectPage(Sale sale, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser(); // 获取当前登录用户信息
        if (currentUser.getRole().equals(RoleEnum.USER.name())) {
            sale.setUserId(currentUser.getId());// 如果当前用户是普通用户，只查询自己的销售记录
        }
        PageHelper.startPage(pageNum, pageSize);// 开启分页插件，设置当前页码和每页大小
        List<Sale> list = saleMapper.selectAll(sale);// 查询符合条件的数据
        return PageInfo.of(list);// 将结果封装成PageInfo对象返回
    }
    /**
     * 根据订单编号查询销售记录的方法
     *
     * @param saleNo 订单编号
     * @return 查询到的销售对象
     */
    public Sale selectByOrderNo(String saleNo) {
        return saleMapper.selectByOrderNo(saleNo);
    }// 返回指定订单编号的销售记录
}
