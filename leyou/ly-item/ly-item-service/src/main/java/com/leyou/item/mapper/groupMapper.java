package com.leyou.item.mapper;

import com.leyou.item.pojo.SpecGroup;
import tk.mybatis.mapper.common.Mapper;


public interface groupMapper extends Mapper<SpecGroup>{
    /*@Insert("INSERT INTO tb_spec_group (cid,name) VALUES(#{cid},#{name})")
    int insertSpecGroup(@Param("cid") Long cid,@Param("name") String name);*/
}
