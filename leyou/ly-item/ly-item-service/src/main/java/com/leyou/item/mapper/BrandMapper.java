package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import com.leyou.common.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand>{
    @Insert("INSERT INTO tb_category_brand (category_id,brand_id) VALUES(#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid,@Param("bid") Long bid);


    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    int deleteCategoryBrandByBid(Long bid);

    @Select("SELECT * FROM tb_brand WHERE id IN (SELECT brand_id FROM tb_category_brand WHERE category_id=#{cid})")
    List<Brand> queryByCategoryId(Long cid);
}
