package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Comment;
import com.example.mapper.CommentMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论相关的业务层服务类
 */
@Service
public class CommentService {

    @Resource
    private CommentMapper commentMapper;// 注入评论数据的mapper接口
    /**
     * 添加评论的方法
     *
     * @param comment 要添加的评论对象
     */
    public void add(Comment comment) {
        comment.setTime(DateUtil.now()); // 设置评论时间为当前时间
        Account currentUser = TokenUtils.getCurrentUser(); // 获取当前登录用户信息
        comment.setUserId(currentUser.getId()); // 设置评论者的ID
        commentMapper.insert(comment);  // 插入到数据库的数据会出现自增id，这个id就可以作为自己的root_id

        if (comment.getPid() != null) {  // 有pid说明它是子评论  子评论必须有root_id
            Comment parentComment = this.selectById(comment.getPid()); // 查询父评论
            comment.setRootId(parentComment.getRootId());   // 设置子评论的root_id为父评论的root_id
        } else { // 否则，它是根节点
            comment.setRootId(comment.getId()); // 根节点的root_id为其自身ID
        }
        this.updateById(comment); // 更新评论记录以保存root_id
    }
    /**
     * 根据ID更新评论记录的方法
     *
     * @param comment 包含新值的评论对象
     */
    public void updateById(Comment comment) {
        commentMapper.updateById(comment);
    }// 更新指定ID的评论记录
    /**
     * 根据ID删除评论记录的方法
     *
     * @param id 要删除的评论记录ID
     */
    public void deleteById(Integer id) {
        this.deepDelete(id);
    }// 调用深度删除方法，递归删除子评论
    /**
     * 深度删除评论及其所有子评论的方法
     *
     * @param pid 父评论ID
     */
    private void deepDelete(Integer pid) {
        List<Comment> children = commentMapper.selectByPid(pid); // 查询所有子评论
        commentMapper.deleteById(pid); // 删除父评论
        for (Comment child : children) {
            deepDelete(child.getId());  // 递归删除每个子评论
        }
    }
    /**
     * 批量删除评论记录的方法
     *
     * @param ids 要删除的评论记录ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            commentMapper.deleteById(id);// 循环删除每个指定ID的评论记录
        }
    }
    /**
     * 根据ID查询单条评论记录的方法
     *
     * @param id 要查询的评论记录ID
     * @return 查询到的评论对象
     */
    public Comment selectById(Integer id) {
        return commentMapper.selectById(id);
    } // 返回指定ID的评论记录
    /**
     * 查询所有符合条件的评论记录的方法
     *
     * @param comment 包含查询条件的对象
     * @return 符合条件的所有评论记录列表
     */
    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    } // 返回所有符合查询条件的评论记录
    /**
     * 分页查询树形结构评论的方法
     *
     * @param comment   包含查询条件的对象
     * @param pageNum   当前页码
     * @param pageSize  每页显示数量
     * @return 分页后的评论记录信息（树形结构）
     */
    public PageInfo<Comment> selectTree(Comment comment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);// 开启分页插件，设置当前页码和每页大小
        List<Comment> list = commentMapper.selectRoot(comment);  // 查询一级节点
        for (Comment root : list) {
            List<Comment> children = commentMapper.selectByRootId(root.getId());  // 查询每个根节点的子评论
            root.setChildren(children);// 将子评论设置到根节点的children属性中
        }
        return PageInfo.of(list); // 将结果封装成PageInfo对象返回
    }
    /**
     * 分页查询评论记录的方法
     *
     * @param comment   包含查询条件的对象
     * @param pageNum   当前页码
     * @param pageSize  每页显示数量
     * @return 分页后的评论记录信息
     */
    public PageInfo<Comment> selectPage(Comment comment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);// 开启分页插件，设置当前页码和每页大小
        List<Comment> list = commentMapper.selectAll(comment); // 查询符合条件的数据
        return PageInfo.of(list); // 将结果封装成PageInfo对象返回
    }
    /**
     * 根据fid和module统计评论数量的方法
     *
     * @param fid    关联的实体ID
     * @param module 实体类型
     * @return 评论总数
     */
    public Integer selectCount(Integer fid, String module) {
        return commentMapper.selectCount(fid, module);
    }// 返回指定fid和module的评论总数
}
