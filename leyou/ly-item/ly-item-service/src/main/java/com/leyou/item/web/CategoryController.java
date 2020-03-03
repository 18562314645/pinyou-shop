package com.leyou.item.web;

import com.leyou.item.service.CategoryService;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父id查出商品分类列表
     * @param id
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam("pid")Long id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.queryCategory(id));
    }

    /**
     * 通过品牌id查询商品分类
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
   public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid){
        List<Category> categoryList = categoryService.queryBrandById(bid);
        if(categoryList.size()<1 || categoryList==null){
            throw new LyException(ExceptionEnums.CATEGORY_NOT_FIND);
        }
        return ResponseEntity.ok(categoryList);
    }

    /**
     * 根据分类id查询商品分类信息
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryByCategoryIds(@RequestParam("ids") List<Long> ids){
       return ResponseEntity.ok(categoryService.queryCategoryList(ids)) ;
    }
}
