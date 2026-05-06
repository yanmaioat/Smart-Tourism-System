package com.example.service;

import com.example.entity.Routes;
import com.example.mapper.RoutesMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 路线相关的业务层服务类
 */
@Service
public class RoutesService {

    @Resource
    private RoutesMapper routesMapper;// 注入路线数据的mapper接口
    /**
     * 添加路线记录的方法
     *
     * @param routes 要添加的路线对象
     */
    public void add(Routes routes) {
        routesMapper.insert(routes);
    }// 插入新的路线记录到数据库
    /**
     * 根据ID更新路线记录的方法
     *
     * @param routes 包含新值的路线对象
     */
    public void updateById(Routes routes) {
        routesMapper.updateById(routes);
    }// 更新指定ID的路线记录
    /**
     * 根据ID删除路线记录的方法
     *
     * @param id 要删除的路线记录ID
     */
    public void deleteById(Integer id) {
        routesMapper.deleteById(id);
    }// 删除指定ID的路线记录
    /**
     * 批量删除路线记录的方法
     *
     * @param ids 要删除的路线记录ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            routesMapper.deleteById(id);// 循环删除每个指定ID的路线记录
        }
    }
    /**
     * 根据ID查询单条路线记录的方法
     *
     * @param id 要查询的路线记录ID
     * @return 查询到的路线对象
     */
    public Routes selectById(Integer id) {
        return routesMapper.selectById(id);
    }// 返回指定ID的路线记录
    /**
     * 查询所有符合条件的路线记录的方法
     *
     * @param routes 包含查询条件的对象
     * @return 符合条件的所有路线记录列表
     */
    public List<Routes> selectAll(Routes routes) {
        return routesMapper.selectAll(routes);
    } // 返回所有符合查询条件的路线记录
    /**
     * 分页查询路线记录的方法
     *
     * @param routes   包含查询条件的对象
     * @param pageNum   当前页码
     * @param pageSize  每页显示数量
     * @return 分页后的路线记录信息
     */
    public PageInfo<Routes> selectPage(Routes routes, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);// 开启分页插件，设置当前页码和每页大小
        List<Routes> list = routesMapper.selectAll(routes);// 查询符合条件的数据
        return PageInfo.of(list);// 将结果封装成PageInfo对象返回
    }

}
