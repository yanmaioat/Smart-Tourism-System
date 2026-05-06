package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Notice;
import com.example.mapper.NoticeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知相关的业务层服务类
 */
@Service
public class NoticeService {

    @Resource
    private NoticeMapper noticeMapper; // 注入通知数据的mapper接口
    /**
     * 添加通知记录的方法
     *
     * @param notice 要添加的通知对象
     */
    public void add(Notice notice) {
        notice.setTime(DateUtil.now());// 设置通知时间为当前时间
        noticeMapper.insert(notice);// 插入新的通知记录到数据库
    }
    /**
     * 根据ID更新通知记录的方法
     *
     * @param notice 包含新值的通知对象
     */
    public void updateById(Notice notice) {
        noticeMapper.updateById(notice);
    }// 更新指定ID的通知记录
    /**
     * 根据ID删除通知记录的方法
     *
     * @param id 要删除的通知记录ID
     */
    public void deleteById(Integer id) {
        noticeMapper.deleteById(id);
    }// 删除指定ID的通知记录
    /**
     * 批量删除通知记录的方法
     *
     * @param ids 要删除的通知记录ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            noticeMapper.deleteById(id);// 循环删除每个指定ID的通知记录
        }
    }
    /**
     * 根据ID查询单条通知记录的方法
     *
     * @param id 要查询的通知记录ID
     * @return 查询到的通知对象
     */
    public Notice selectById(Integer id) {
        return noticeMapper.selectById(id);
    }// 返回指定ID的通知记录
    /**
     * 查询所有符合条件的通知记录的方法
     *
     * @param notice 包含查询条件的对象
     * @return 符合条件的所有通知记录列表
     */
    public List<Notice> selectAll(Notice notice) {
        return noticeMapper.selectAll(notice);
    }// 返回所有符合查询条件的通知记录
    /**
     * 分页查询通知记录的方法
     *
     * @param notice   包含查询条件的对象
     * @param pageNum   当前页码
     * @param pageSize  每页显示数量
     * @return 分页后的通知记录信息
     */
    public PageInfo<Notice> selectPage(Notice notice, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);// 开启分页插件，设置当前页码和每页大小
        List<Notice> list = noticeMapper.selectAll(notice);// 查询符合条件的数据
        return PageInfo.of(list); // 将结果封装成PageInfo对象返回
    }

}
