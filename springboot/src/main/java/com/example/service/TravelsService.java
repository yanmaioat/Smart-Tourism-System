package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Travels;
import com.example.mapper.PraiseMapper;
import com.example.mapper.TravelsMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 游记相关的业务层服务类
 */
@Service
public class TravelsService {

    @Resource
    private TravelsMapper travelsMapper;// 注入游记数据的mapper接口

    @Resource
    private PraiseMapper praiseMapper;// 注入点赞数据的mapper接口
    /**
     * 添加游记记录的方法
     *
     * @param travels 要添加的游记对象
     */
    public void add(Travels travels) {
        travels.setReadCount(0);// 初始化阅读次数为0
        travels.setTime(DateUtil.now()); // 设置游记发布时间为当前时间
        travels.setStatus("待审核");// 设置游记状态为“待审核”
        Account currentUser = TokenUtils.getCurrentUser();// 获取当前登录用户信息
        travels.setUserId(currentUser.getId());// 设置游记作者ID
        travelsMapper.insert(travels);// 插入新的游记记录到数据库
    }
    /**
     * 根据ID更新游记记录的方法
     *
     * @param travels 包含新值的游记对象
     */
    public void updateById(Travels travels) {// 获取当前登录用户信息
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            travels.setStatus("待审核");// 如果是普通用户，更新后设置状态为“待审核”
        }
        travelsMapper.updateById(travels); // 更新指定ID的游记记录
    }
    /**
     * 根据ID删除游记记录的方法
     *
     * @param id 要删除的游记记录ID
     */
    public void deleteById(Integer id) {
        travelsMapper.deleteById(id);
    } // 删除指定ID的游记记录
    /**
     * 批量删除游记记录的方法
     *
     * @param ids 要删除的游记记录ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            travelsMapper.deleteById(id);// 循环删除每个指定ID的游记记录
        }
    }
    /**
     * 根据ID查询单条游记记录的方法
     *
     * @param id 要查询的游记记录ID
     * @return 查询到的游记对象
     */
    public Travels selectById(Integer id) {
        Travels travels = travelsMapper.selectById(id); // 查询指定ID的游记记录
        setTravelData(travels); // 设置游记的相关统计数据（如点赞数）
        return travels;
    }
    /**
     * 查询所有符合条件的游记记录的方法
     *
     * @param travels 包含查询条件的对象
     * @return 符合条件的所有游记记录列表
     */
    public List<Travels> selectAll(Travels travels) {
        return travelsMapper.selectAll(travels);
    } // 返回所有符合查询条件的游记记录
    /**
     * 分页查询游记记录的方法
     *
     * @param travels   包含查询条件的对象
     * @param pageNum   当前页码
     * @param pageSize  每页显示数量
     * @return 分页后的游记记录信息
     */
    public PageInfo<Travels> selectPage(Travels travels, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();// 获取当前登录用户信息
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            travels.setUserId(currentUser.getId()); // 如果是普通用户，只查询自己的游记记录
        }
        PageHelper.startPage(pageNum, pageSize);// 开启分页插件，设置当前页码和每页大小
        List<Travels> list = travelsMapper.selectAll(travels);// 查询符合条件的数据
        return PageInfo.of(list);// 将结果封装成PageInfo对象返回
    }

    /**
     * 分页查询用户可见的游记记录的方法
     *
     * @param travels   包含查询条件的对象
     * @param pageNum   当前页码
     * @param pageSize  每页显示数量
     * @return 分页后的游记记录信息（仅包含状态为“通过”的游记）
     */
    public PageInfo<Travels> selectFrontPage(Travels travels, Integer pageNum, Integer pageSize) {
        travels.setStatus("通过");// 只查询状态为“通过”的游记
        PageHelper.startPage(pageNum, pageSize);// 开启分页插件，设置当前页码和每页大小
        List<Travels> list = travelsMapper.selectAll(travels);// 查询符合条件的数据
        for (Travels t : list) {
            setTravelData(t);// 设置每个游记的相关统计数据（如点赞数）
        }
        return PageInfo.of(list);// 将结果封装成PageInfo对象返回
    }
    /**
     * 更新游记阅读次数的方法
     *
     * @param id 游记记录ID
     */
    public void updateReadCount(Integer id) {
        travelsMapper.updateReadCount(id);
    } // 增加指定ID游记的阅读次数
    /**
     * 设置游记的相关统计数据的方法
     *
     * @param travels 游记对象
     */
    private void setTravelData(Travels travels) {
        Integer count = praiseMapper.selectCount(travels.getId());// 查询游记的点赞数
        travels.setPraiseCount(count);// 设置游记的点赞数
    }

}
