package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.item.pojo.Brand;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.BrandVo;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 品牌服务
 */
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询品牌列表
     * @return
     */
    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key){
        //从数据库中要得到1.总条数 2.总页数 3.当前页数据(list)

        //1.开始分页
        PageHelper.startPage(page,rows);


        //2.过滤(按照查询条件name或首字母)
        Example example=new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key.toUpperCase());
        }
        if(StringUtils.isNotBlank(sortBy)){
            //3.排序
            String orderClause=sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderClause);
        }

        //4.查询
        Page pageInfo = (Page) brandMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(pageInfo.getResult())){
            throw new LyException(ExceptionEnums.BRAND_NOT_FIND);
        }

        //5.解析分页结果

        //6.输出结果
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getResult());
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    public void saveBrand(Brand brand, List<Long> cids) {

        //1.新增品牌信息(insertSelective只会给有值的字段赋值)
        int count = brandMapper.insertSelective(brand);
        if(count!=1){
            //新增失败
            throw new LyException(ExceptionEnums.SAVE_BRAND_FAILE);
        }
        //2.新增品牌和分类信息的中间表
        for (Long cid : cids) {
            brandMapper.insertCategoryBrand(cid,brand.getId());
        }
    }

    /**
     * 修改品牌
     */
    public void updataBrand(BrandVo brandVo){
        //获取品牌
        Brand brand=new Brand();
        brand.setId(brandVo.getId());
        brand.setName(brandVo.getName());
        brand.setImage(brandVo.getImage());
        brand.setLetter(brandVo.getLetter());
        int count = brandMapper.updateByPrimaryKeySelective(brand);
        if(count==0){
            throw new LyException(ExceptionEnums.UPDATE_BRAND_FAILE);
        }

        //获取分类id
        List<Long> cids = brandVo.getCids();
        brandMapper.deleteCategoryBrandByBid(brandVo.getId());
        for (Long cid : cids) {
            int categoryCount = brandMapper.insertCategoryBrand(cid, brandVo.getId());
            if (categoryCount==0){
                throw new LyException(ExceptionEnums.UPDATE_BRAND_FAILE);
            }
        }
    }

    /**
     * 删除品牌
     */
    public void deleteBrand(Long bid){
        //1.删除品牌
        int resultCount = brandMapper.deleteByPrimaryKey(bid);
        if(resultCount==0){
            throw new LyException(ExceptionEnums.DELETE_BRAND_FAILE);
        }
        //2.删除中间表
        int categoryCount = brandMapper.deleteCategoryBrandByBid(bid);
        if(categoryCount==0){
            throw new LyException(ExceptionEnums.DELETE_BRAND_FAILE);
        }
    }

    /**
     * 根据分类id查询品牌
     */
    public List<Brand> queryByCategoryId(Long cid){
        List<Brand> brandList=brandMapper.queryByCategoryId(cid);
        if(CollectionUtils.isEmpty(brandList)){
            throw new LyException(ExceptionEnums.BRAND_NOT_FIND);
        }
        return brandList;
    }

    public Brand queryByBrandId(Long id) {
        Brand brand=new Brand();
        brand.setId(id);
        Brand brand1 = brandMapper.selectByPrimaryKey(brand);
        if (brand1 == null) {
            throw new LyException(ExceptionEnums.BRAND_NOT_FIND);
        }
        return brand1;
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> brands = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnums.BRAND_NOT_FIND);
        }
        return brands;
    }


}
