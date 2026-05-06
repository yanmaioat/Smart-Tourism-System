package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Feedback;
import com.example.mapper.FeedbackMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 反馈相关的业务层服务类
 */
@Service
public class FeedbackService {

    @Resource
    private FeedbackMapper feedbackMapper; // 注入反馈数据的mapper接口
    /**
     * 添加反馈记录的方法
     *
     * @param feedback 要添加的反馈对象
     */
    public void add(Feedback feedback) {
        Account currentUser = TokenUtils.getCurrentUser();// 获取当前登录用户信息
        feedback.setUserId(currentUser.getId()); // 设置反馈者的ID
        feedback.setTime(DateUtil.now());// 设置反馈的时间为当前时间
        feedbackMapper.insert(feedback);// 插入新的反馈记录到数据库
    }
    /**
     * 根据ID更新反馈记录的方法
     *
     * @param feedback 包含新值的反馈对象
     */
    public void updateById(Feedback feedback) {
        feedbackMapper.updateById(feedback);
    }// 更新指定ID的反馈记录
    /**
     * 根据ID删除反馈记录的方法
     *
     * @param id 要删除的反馈记录ID
     */
    public void deleteById(Integer id) {
        feedbackMapper.deleteById(id);// 删除指定ID的反馈记录
    }
    /**
     * 批量删除反馈记录的方法
     *
     * @param ids 要删除的反馈记录ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            feedbackMapper.deleteById(id);// 循环删除每个指定ID的反馈记录
        }
    }
    /**
     * 根据ID查询单条反馈记录的方法
     *
     * @param id 要查询的反馈记录ID
     * @return 查询到的反馈对象
     */
    public Feedback selectById(Integer id) {
        return feedbackMapper.selectById(id);
    }// 返回指定ID的反馈记录
    /**
     * 查询所有符合条件的反馈记录的方法
     *
     * @param feedback 包含查询条件的对象
     * @return 符合条件的所有反馈记录列表
     */
    public List<Feedback> selectAll(Feedback feedback) {
        return feedbackMapper.selectAll(feedback);
    }// 返回所有符合查询条件的反馈记录
    /**
     * 分页查询反馈记录的方法
     *
     * @param feedback   包含查询条件的对象
     * @param pageNum   当前页码
     * @param pageSize  每页显示数量
     * @return 分页后的反馈记录信息
     */
    public PageInfo<Feedback> selectPage(Feedback feedback, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();// 获取当前登录用户信息
        if(currentUser.getRole().equals(RoleEnum.USER.name())) {
            feedback.setUserId(currentUser.getId()); // 如果当前用户是普通用户，只查询自己的反馈记录
        }
        PageHelper.startPage(pageNum, pageSize);// 开启分页插件，设置当前页码和每页大小
        List<Feedback> list = feedbackMapper.selectAll(feedback); // 查询符合条件的数据
        return PageInfo.of(list); // 将结果封装成PageInfo对象返回
    }

}
