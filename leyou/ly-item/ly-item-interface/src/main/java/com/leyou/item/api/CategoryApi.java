package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
public interface CategoryApi {
    /**
     * 根据父id查出商品分类列表
     * @param id
     * @return
     */
    @GetMapping("category/list")
    List<Category> queryCategoryListByPid(@RequestParam("pid")Long id);

    /**
     * 通过品牌id查询商品分类
     * @param bid
     * @return
     */
    @GetMapping("category/bid/{bid}")
    List<Category> queryByBrandId(@PathVariable("bid") Long bid);

    /**
     * 根据分类id查询商品分类信息
     * @return
     */
    @GetMapping("category/list/ids")
    List<Category> queryByCategoryIds(@RequestParam("ids") List<Long> ids);
}
