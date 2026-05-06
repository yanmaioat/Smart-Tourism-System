package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Article;
import com.example.mapper.ArticleMapper;
import com.example.mapper.CollectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务层方法
 */
/**
 * ArticleService 是文章相关的业务逻辑处理类。
 * 提供了增删改查以及分页等功能。
 */
@Service
public class ArticleService {

    @Resource
    private ArticleMapper articleMapper;// 注入文章数据访问接口
    @Resource
    private CollectMapper collectMapper;// 注入收藏数据访问接口
    /**
     * 添加一篇文章，设置默认阅读次数为 0，日期为当前日期。
     * @param article 要添加的文章对象
     */
    public void add(Article article) {
        article.setReadCount(0);// 设置初始阅读次数为 0
        article.setDate(DateUtil.today()); // 使用 Hutool 设置当前日期（格式为 yyyy-MM-dd）
        articleMapper.insert(article); // 插入到数据库中
    }
    /**
     * 根据 ID 更新文章内容。
     * @param article 要更新的文章对象
     */
    public void updateById(Article article) {
        articleMapper.updateById(article);
    }// 调用 Mapper 方法更新记录
    /**
     * 根据文章 ID 删除文章。
     * @param id 文章 ID
     */
    public void deleteById(Integer id) {
        articleMapper.deleteById(id);
    }// 调用 Mapper 的删除方法
    /**
     * 批量删除文章。
     * @param ids 要删除的文章 ID 列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            articleMapper.deleteById(id); // 循环调用删除单条记录的方法
        }
    }
    /**
     * 根据 ID 查询文章，并查询该文章的收藏总数。
     * @param id 文章 ID
     * @return 查询到的文章对象（包含收藏数）
     */
    public Article selectById(Integer id) {
        Article article = articleMapper.selectById(id); // 查询文章基本信息
        Integer count = collectMapper.selectByFid(article.getId());// 查询该文章被收藏的总次数
        article.setCollectCount(count);  // 将收藏数设置进文章对象中
        return article;
    }
    /**
     * 查询所有文章，支持条件筛选（如标题模糊匹配等）。
     * @param article 查询条件封装对象
     * @return 符合条件的文章列表
     */
    public List<Article> selectAll(Article article) {
        return articleMapper.selectAll(article);
    }// 调用 Mapper 查询
    /**
     * 分页查询文章，返回 PageInfo 对象，包含分页信息。
     * @param article 查询条件
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @return PageInfo<Article> 分页结果对象
     */
    public PageInfo<Article> selectPage(Article article, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);// 开启分页
        List<Article> list = articleMapper.selectAll(article);// 查询当前页的数据
        return PageInfo.of(list); // 返回封装后的分页信息
    }
    /**
     * 查询推荐文章（通常由数据库中指定字段决定是否推荐）
     * @return 推荐文章列表
     */
    public List<Article> selectRecommend() {
        return articleMapper.selectRecommend();
    } // 调用 Mapper 查询推荐文章

}
