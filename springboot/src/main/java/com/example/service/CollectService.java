package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Collect;
import com.example.exception.CustomException;
import com.example.mapper.CollectMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏相关的业务层服务类
 */
@Service
public class CollectService {

    @Resource
    private CollectMapper collectMapper;// 注入收集数据的mapper接口
    /**
     * 添加收藏记录的方法
     *
     * @param collect 要添加的收藏对象
     */
    public void add(Collect collect) {
        Account currentUser = TokenUtils.getCurrentUser(); // 获取当前登录用户信息
        // 检查该用户是否已经收藏过此攻略（通过userId和fid判断）
        Collect dbCollect = collectMapper.selectUserCollect(currentUser.getId(), collect.getFid());
        if (dbCollect != null) {
            throw new CustomException("500", "该攻略已经收藏过了");// 如果已存在，则抛出自定义异常
        }
        collect.setUserId(currentUser.getId());// 设置收藏者的ID
        collect.setTime(DateUtil.now());// 设置收藏的时间为当前时间
        collectMapper.insert(collect);// 插入新的收藏记录到数据库
    }
    /**
     * 根据ID更新收藏记录的方法
     *
     * @param collect 包含新值的收藏对象
     */
    public void updateById(Collect collect) {
        collectMapper.updateById(collect);
    }// 更新指定ID的收藏记录
    /**
     * 根据ID删除收藏记录的方法
     *
     * @param id 要删除的收藏记录ID
     */
    public void deleteById(Integer id) {
        collectMapper.deleteById(id);
    }// 删除指定ID的收藏记录
    /**
     * 批量删除收藏记录的方法
     *
     * @param ids 要删除的收藏记录ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            collectMapper.deleteById(id);// 循环删除每个指定ID的收藏记录
        }
    }
    /**
     * 根据ID查询单条收藏记录的方法
     *
     * @param id 要查询的收藏记录ID
     * @return 查询到的收藏对象
     */
    public Collect selectById(Integer id) {
        return collectMapper.selectById(id);
    }// 返回指定ID的收藏记录
    /**
     * 查询所有符合条件的收藏记录的方法
     *
     * @param collect 包含查询条件的对象
     * @return 符合条件的所有收藏记录列表
     */
    public List<Collect> selectAll(Collect collect) {
        return collectMapper.selectAll(collect);
    }// 返回所有符合查询条件的收藏记录
    /**
     * 分页查询收藏记录的方法
     *
     * @param collect   包含查询条件的对象
     * @param pageNum   当前页码
     * @param pageSize  每页显示数量
     * @return 分页后的收藏记录信息
     */
    public PageInfo<Collect> selectPage(Collect collect, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();// 获取当前登录用户信息
        // 如果当前用户的角色是普通用户，则仅查询其自己的收藏记录
        if (currentUser.getRole().equals(RoleEnum.USER.name())) {
            collect.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize); // 开启分页插件，设置当前页码和每页大小
        List<Collect> list = collectMapper.selectAll(collect);// 查询符合条件的数据
        return PageInfo.of(list);// 将结果封装成PageInfo对象返回
    }

}
