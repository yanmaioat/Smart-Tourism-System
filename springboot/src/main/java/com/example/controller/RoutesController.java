package com.example.controller;

import com.example.common.Result;
import com.example.entity.Routes;
import com.example.service.RoutesService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前端请求接口
 */
@RestController
@RequestMapping("/routes")
public class RoutesController {

    @Resource
    private RoutesService routesService;
    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Routes routes) {
        routesService.add(routes); //调用 routesService.add(routes) 保存数据
        return Result.success();   //返回 Result.success() 表示操作成功
    }
    //修改
    @PutMapping("/update")
    public Result update(@RequestBody Routes routes) { //根据 Routes 对象中的 id 更新数据
        routesService.updateById(routes); //调用 routesService.updateById(routes) 更新数据（需确保 routes 对象包含 id）
        return Result.success();
    }
    //单个删除
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {  //根据 id 删除单条数据
        routesService.deleteById(id);    //调用 routesService.deleteById(id) 删除数据。
        return Result.success();
    }
    //批量删除
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {  //根据 ids 列表批量删除数据
        routesService.deleteBatch(ids);  //调用 routesService.deleteBatch(ids) 执行批量删除
        return Result.success();
    }
    //单个查询
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {  //根据 id 查询单条数据
        Routes routes = routesService.selectById(id);     //调用 routesService.selectById(id) 获取数据，返回 Result 对象包含查询结果
        return Result.success(routes);
    }
    //查询所有
    @GetMapping("/selectAll")
    public Result selectAll(Routes routes) {  //根据 Routes 对象中的 id 更新数据
        List<Routes> list = routesService.selectAll(routes);  //调用 routesService.selectAll(routes) 获取所有数据
        return Result.success(list);
    }
    //分页查询
    @GetMapping("/selectPage")
    public Result selectPage(Routes routes,
                             @RequestParam(defaultValue = "1") Integer pageNum, //@RequestParam：从查询参数中提取分页参数，pageNum：当前页码，默认 1
                             @RequestParam(defaultValue = "10") Integer pageSize) {  //pageSize：每页数量，默认 10
        PageInfo<Routes> pageInfo = routesService.selectPage(routes, pageNum, pageSize);//调用 routesService.selectPage(routes, pageNum, pageSize) 获取分页数据
        return Result.success(pageInfo);
    }

}
