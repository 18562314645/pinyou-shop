package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService implements ICategoryService{
  @Autowired
  private CategoryMapper categoryMapper;


    public List<Category> queryCategory(Long id) {
        //查询条件，mapper会把对象中的非空属性作为查询条件
        Category category=new Category();
        category.setParentId(id);
        List<Category> categoryList = categoryMapper.select(category);
        //判断结果
        /*if(categoryList==null || categoryList.isEmpty()){
            throw new LyException(ExceptionEnums.CATEGORY_NOT_FIND);
        }*/
        if(CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnums.CATEGORY_NOT_FIND);
        }
        return categoryList;
    }

    /**
     * 根据品牌id查询商品分类
     */
    public List<Category> queryBrandById(Long bid){

        return categoryMapper.queryByBrandId(bid);
    }

    /**
     * 根据ids查询分类集合
     * @param ids
     * @return
     */
    public List<Category> queryCategoryList(List<Long> ids){
        List<Category> categoryList = categoryMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnums.CATEGORY_NOT_FIND);
        }
        return categoryList;
    }
}

