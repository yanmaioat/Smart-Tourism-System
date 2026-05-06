package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Tourism;
import com.example.mapper.TourismMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务层方法
 */
@Service
public class TourismService {

    // 注入 TourismMapper，用于操作数据库
    @Resource
    private TourismMapper tourismMapper;

    /**
     * 添加旅游商品信息
     * 1. 设置当前日期。
     * 2. 插入旅游商品记录到数据库。
     */
    public void add(Tourism tourism) {
        // 设置旅游商品的日期为当前日期
        tourism.setDate(DateUtil.today());
        // 调用 Mapper 层的 insert 方法插入数据
        tourismMapper.insert(tourism);
    }

    /**
     * 根据主键 ID 更新旅游商品信息
     * 调用 Mapper 层的 updateById 方法实现更新操作。
     */
    public void updateById(Tourism tourism) {
        // 调用 Mapper 层的 updateById 方法更新数据
        tourismMapper.updateById(tourism);
    }

    /**
     * 根据主键 ID 删除旅游商品
     * 调用 Mapper 层的 deleteById 方法实现删除操作。
     */
    public void deleteById(Integer id) {
        // 调用 Mapper 层的 deleteById 方法删除指定 ID 的记录
        tourismMapper.deleteById(id);
    }

    /**
     * 批量删除旅游商品
     * 遍历传入的 ID 列表，逐个调用 deleteById 方法删除记录。
     */
    public void deleteBatch(List<Integer> ids) {
        // 遍历 ID 列表，逐个删除
        for (Integer id : ids) {
            tourismMapper.deleteById(id);
        }
    }

    /**
     * 根据主键 ID 查询旅游商品信息
     * 调用 Mapper 层的 selectById 方法实现查询操作。
     */
    public Tourism selectById(Integer id) {
        // 调用 Mapper 层的 selectById 方法查询指定 ID 的记录
        return tourismMapper.selectById(id);
    }

    /**
     * 查询所有旅游商品信息
     * 调用 Mapper 层的 selectAll 方法实现查询操作。
     * 可以根据传入的 Tourism 对象作为条件进行筛选。
     */
    public List<Tourism> selectAll(Tourism tourism) {
        // 调用 Mapper 层的 selectAll 方法查询符合条件的记录列表
        return tourismMapper.selectAll(tourism);
    }

    /**
     * 分页查询旅游商品信息
     * 1. 使用 PageHelper 实现分页查询。
     * 2. 返回分页结果（PageInfo）。
     */
    public PageInfo<Tourism> selectPage(Tourism tourism, Integer pageNum, Integer pageSize) {
        // 使用 PageHelper 开始分页
        PageHelper.startPage(pageNum, pageSize);
        // 查询符合条件的旅游商品列表
        List<Tourism> list = tourismMapper.selectAll(tourism);
        // 将查询结果封装为分页对象并返回
        return PageInfo.of(list);
    }
}
