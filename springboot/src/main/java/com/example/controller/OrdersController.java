package com.example.controller;

import com.example.common.Result;
import com.example.entity.Orders;
import com.example.service.OrdersService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前端请求接口
 */
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Orders orders) {
        ordersService.add(orders);         //调用ordersService.add(orders) 保存数据
        return Result.success();           //返回 Result.success() 表示操作成功
    }
    //修改
    @PutMapping("/update")
    public Result update(@RequestBody Orders orders) { //根据 Routes 对象中的 id 更新数据
        ordersService.updateById(orders);              //ordersService.updateById(orders) 更新数据（需确保 orders对象包含 id）
        return Result.success();
    }
    //单个删除
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {//根据 id 删除单条数据
        ordersService.deleteById(id);           // //调用 ordersService.deleteById(id) 删除数据。
        return Result.success();
    }
    //批量删除
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {  //根据 ids 列表批量删除数据
        ordersService.deleteBatch(ids);                     //调用  ordersService.deleteBatch(ids) 执行批量删除
        return Result.success();
    }
    //单个查询
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) { //根据 id 查询单条数据
        Orders orders = ordersService.selectById(id);    //调用 Orders orders = ordersService.selectById(id) 获取数据，返回 Result 对象包含查询结果
        return Result.success(orders);
    }
    //查询所有
    @GetMapping("/selectAll")
    public Result selectAll(Orders orders) { //根据 Routes 对象中的 id 更新数据
        List<Orders> list = ordersService.selectAll(orders); ////调用 ordersService.selectAll(orders) 获取所有数据
        return Result.success(list);
    }

    //分页查询
    @GetMapping("/selectPage")
    public Result selectPage(Orders orders,
                             @RequestParam(defaultValue = "1") Integer pageNum,   //@RequestParam：从查询参数中提取分页参数，pageNum：当前页码，默认 1
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Orders> pageInfo = ordersService.selectPage(orders, pageNum, pageSize);
        return Result.success(pageInfo);
    }

}
