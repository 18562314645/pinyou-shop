package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import com.leyou.common.vo.PageResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface BrandApi {

    /**
     * 查询品牌列表
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    @GetMapping("brand/page")
    PageResult<Brand> queryBrandByPage(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",defaultValue = "false") Boolean desc,
            @RequestParam(value = "key",required = false) String key
    );


    /**
     * 根据ids查询品牌
     * @param ids
     * @return
     */
    @GetMapping("brand/list")
    List<Brand> queryBrandsByIds(@RequestParam("ids") List<Long> ids);


    /**
     * 根据商品分类id查询品牌
     */
    @GetMapping("brand/cid/{cid}")
    List<Brand> queryByCategoryId(@PathVariable("cid") Long cid);

    /**
     * 根据商品品牌id查询商品品牌
     * @return
     */
    @GetMapping("brand/{id}")
   Brand queryByBrandId(@PathVariable("id") Long id);
}
