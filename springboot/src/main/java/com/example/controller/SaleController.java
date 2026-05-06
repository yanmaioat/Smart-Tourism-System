package com.example.controller;

import com.example.common.Result;
import com.example.entity.Sale;
import com.example.service.SaleService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前端请求接口
 */
@RestController
@RequestMapping("/sale")
public class SaleController {

    @Resource
    private SaleService saleService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Sale sale) {
        saleService.add(sale);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody Sale sale) {
        saleService.updateById(sale);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        saleService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        saleService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 单个查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Sale sale = saleService.selectById(id);
        return Result.success(sale);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Sale sale) {
        List<Sale> list = saleService.selectAll(sale);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Sale sale,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Sale> pageInfo = saleService.selectPage(sale, pageNum, pageSize);
        return Result.success(pageInfo);
    }

}
