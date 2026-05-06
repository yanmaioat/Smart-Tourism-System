package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Train;
import com.example.mapper.TrainMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 火车票相关的业务层服务类
 */
@Service
public class TrainService {

    @Resource
    private TrainMapper trainMapper;// 注入火车票数据的mapper接口
    /**
     * 添加火车票记录的方法
     *
     * @param train 要添加的火车票对象
     */
    public void add(Train train) {
        train.setDate(DateUtil.today());// 设置火车票日期为今天
        trainMapper.insert(train);// 插入新的火车票记录到数据库
    }
    /**
     * 根据ID更新火车票记录的方法
     *
     * @param train 包含新值的火车票对象
     */
    public void updateById(Train train) {
        trainMapper.updateById(train);
    } // 更新指定ID的火车票记录
    /**
     * 根据ID删除火车票记录的方法
     *
     * @param id 要删除的火车票记录ID
     */
    public void deleteById(Integer id) {
        trainMapper.deleteById(id);
    }// 删除指定ID的火车票记录
    /**
     * 批量删除火车票记录的方法
     *
     * @param ids 要删除的火车票记录ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            trainMapper.deleteById(id);// 循环删除每个指定ID的火车票记录
        }
    }
    /**
     * 根据ID查询单条火车票记录的方法
     *
     * @param id 要查询的火车票记录ID
     * @return 查询到的火车票对象
     */
    public Train selectById(Integer id) {
        return trainMapper.selectById(id);
    }// 返回指定ID的火车票记录
    /**
     * 查询所有符合条件的火车票记录的方法
     *
     * @param train 包含查询条件的对象
     * @return 符合条件的所有火车票记录列表
     */
    public List<Train> selectAll(Train train) {
        return trainMapper.selectAll(train);
    }// 返回所有符合查询条件的火车票记录
    /**
     * 分页查询火车票记录的方法
     *
     * @param train   包含查询条件的对象
     * @param pageNum   当前页码
     * @param pageSize  每页显示数量
     * @return 分页后的火车票记录信息
     */
    public PageInfo<Train> selectPage(Train train, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);// 开启分页插件，设置当前页码和每页大小
        List<Train> list = trainMapper.selectAll(train);// 查询符合条件的数据
        return PageInfo.of(list);// 将结果封装成PageInfo对象返回
    }

}
