package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务
 */
@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private  CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;
    
    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    public PageResult<Spu> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows){
        //开始分页
        PageHelper.startPage(page,rows);

        //搜索条件
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if (saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        criteria.andEqualTo("valid",1);
        example.setOrderByClause("last_update_time desc");
        //执行查询
        List<Spu> spus = spuMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnums.GOODS_NOT_FIND);
        }

        //对查询结果中的分类名和品牌名进行处理
        handlerCategoryAndBrand(spus);
        //分析分页结果
        PageInfo pageInfo=new PageInfo(spus);
        return new PageResult<>(pageInfo.getTotal(),spus);
    }

    /**
     * 对查询结果中的分类名和品牌名进行处理
     * @param spus
     */
    private void handlerCategoryAndBrand(List<Spu> spus) {
        for (Spu spu : spus) {
            //查询分类名称
            List<String> names = categoryService.queryCategoryList(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names,"/"));
            //查询寻品牌名称
            spu.setBname(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
        }
    }

    /**
     * 保存商品信息
     * @param spu
     */
    public void saveGoods(Spu spu) {

        //新增spu
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);
        int spuCount = spuMapper.insert(spu);
        if(spuCount!=1){
            throw new LyException(ExceptionEnums.GOODS_SAVE_ERROR);
        }
        //新增spudetails
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        int spuDetailCount = spuDetailMapper.insert(spuDetail);
        if(spuDetailCount!=1){
            throw new LyException(ExceptionEnums.SAVE_SPUDETAIL_ERROR);
        }
        //新增sku
        List<Stock> stockList=new ArrayList<>();
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setSpuId(spu.getId());
            sku.setLastUpdateTime(sku.getCreateTime());
            int skuCount = skuMapper.insert(sku);
            if(skuCount!=1){
                throw new LyException(ExceptionEnums.SAVE_SKU_ERROR);
            }

            //新增库存
            Stock stock=new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);
        }
        //批量新增
        int stockCount = stockMapper.insertList(stockList);
        if(stockCount==0){
            throw new LyException(ExceptionEnums.SAVE_STOCK_ERROR);
        }
    }

    /**
     * 查询spudetail信息
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetail(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if(spuDetail==null){
            throw new LyException(ExceptionEnums.SPUDETAIL_NOT_FIND);
        }
        return spuDetail;
    }

    /**
     * 查询sku信息
     * @param spuId
     * @return
     */
    public List<Sku> querySku(Long spuId) {
        Sku sku=new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        for (Sku s : skus) {
            //拿到库存
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        }
        return skus;
    }

    /**
     * 修改商品信息
     * @param spu
     */
    public void updateGoods(Spu spu) {
       if(spu.getId()==0){
           throw new LyException(ExceptionEnums.INVALID_PARAM);
       }
       //查询以前的sku
        Sku sku=new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skuList = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skuList)){
            //删除sku
            skuMapper.delete(sku);
            //删除库存
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }
        //更新数据库spu spudetail
        spu.setLastUpdateTime(new Date());
        //更新spu
        int spuCount = spuMapper.updateByPrimaryKeySelective(spu);
        if(spuCount==0){
            throw new LyException(ExceptionEnums.UPDAE_SPU_ERROR);
        }
        //更新spudetail
        SpuDetail spuDetail=new SpuDetail();
        spuDetail.setSpuId(spu.getId());
        int spudetailCount = spuDetailMapper.updateByPrimaryKeySelective(spuDetail);
        if (spudetailCount==0){
            throw new LyException(ExceptionEnums.UPDAE_SPUDETAIL_ERROR);
        }
        //更新sku和stock
        List<Stock> stockList=new ArrayList<>();
        List<Sku> skus = spu.getSkus();
        for (Sku s1 : skus) {
            s1.setSpuId(spu.getId());
            s1.setCreateTime(new Date());
            s1.setLastUpdateTime(s1.getCreateTime());
            int skuCount = skuMapper.insert(s1);
            if (skuCount!=1){
                throw new LyException(ExceptionEnums.UPDATE_SKU_FAILE);
            }
            Stock stock=new Stock();
            stock.setSkuId(s1.getId());
            stock.setStock(s1.getStock());
            stockList.add(stock);
        }
        //集中插入更新
        int stockCount = stockMapper.insertList(stockList);
        if (stockCount==0){
            throw new LyException(ExceptionEnums.DELETE_STOCK_FAILE);
        }

    }

    /**
     * 根据spuId删除商品
     * @param spuId
     */
    public void deleteGoodsBySpuId(Long spuId) {
        if (spuId==null){
            throw new LyException(ExceptionEnums.INVALID_PARAM);
        }
        //如果有spuId就进行逻辑上的删除
        Spu spu=new Spu();
        spu.setId(spuId);
        spu.setValid(false);
        int deleteCount = spuMapper.updateByPrimaryKeySelective(spu);
        System.out.println("================================="+deleteCount);
        if (deleteCount==0){
            throw new LyException(ExceptionEnums.DELE_SPU_ERROR);
        }
    }

    /**
     * 根据spuid查询spu信息
     * @param id
     * @return
     */
    public Spu querySpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu==null){
            throw new LyException(ExceptionEnums.SPU_NOT_FIND);
        }
        return spu;
    }
}
