package com.example.mapper;

import com.example.entity.Sale;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SaleMapper {

    int insert(Sale sale);

    void updateById(Sale sale);

    void deleteById(Integer id);


    Sale selectById(Integer id);

    List<Sale> selectAll(Sale sale);

    @Select("select * from sale where order_no = #{orderNo}")
    Sale selectByOrderNo(String orderNo);
}
