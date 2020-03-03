package com.leyou.page.service;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.*;
import com.leyou.page.feign.BrandClient;
import com.leyou.page.feign.CategoryClient;
import com.leyou.page.feign.GoodsClient;
import com.leyou.page.feign.SpecClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    public Map<String,Object> loadData(Long spuId){
        Map<String,Object> map=new HashMap<>();
        //1.查询spu
        Spu spu = goodsClient.querySpuById(spuId);

        //2.查询spudetail
        SpuDetail spuDetail = goodsClient.querySpuDetail(spuId);

        //3.查询sku集合
        List<Sku> skus = goodsClient.querySku(spuId);

        //4.查询分类
        List<Long> cids=Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3());
        List<Category> categories = categoryClient.queryByCategoryIds(cids);
        List<Map<String,Object>> categoryList=new ArrayList<>();
        for(int i=0;i<cids.size();i++){
            Map<String,Object> categoryMap=new HashMap<>();
            categoryMap.put("id",cids.get(i));
            categoryMap.put("name",categories.get(i).getName());
            categoryList.add(categoryMap);
        }

        //5.查询商品品牌
        Brand brand = brandClient.queryByBrandId(spu.getBrandId());
        System.out.println("====================================="+brand);
        //6.查询规格参数组
        List<SpecGroup> specGroups = specClient.querySpecsByCid(spu.getCid3());

        //7.查询特殊规格参数
        List<SpecParam> params = specClient.queryParams(null, spu.getCid3(), false, null);
        Map<Long,String> paramMap=new HashMap<>();
        params.forEach(param->{
            paramMap.put(param.getId(),param.getName());
        });

        //8.封装spu
        map.put("spu",spu);

        //9.封装spudetail
        map.put("spuDetail",spuDetail);

        //10.封装sku
        map.put("skus",skus);

        //11.封装分类
        map.put("categoryList",categoryList);

        //12.封装品牌
        map.put("brand",brand);

        //13.封装规格参数
        map.put("specGroups",specGroups);

        //14.封装特殊规格参数
        map.put("paramMap",paramMap);

        //15.返回结果
        System.out.println("========================"+map);
        return map;

    }
}
