package com.example.mapper;

import com.example.entity.Train;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TrainMapper {

    int insert(Train train);

    void updateById(Train train);

    void deleteById(Integer id);

    @Select("select * from `train` where id = #{id}")
    Train selectById(Integer id);

    List<Train> selectAll(Train train);

}
