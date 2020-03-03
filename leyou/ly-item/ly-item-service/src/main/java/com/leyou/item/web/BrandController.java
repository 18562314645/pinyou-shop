package com.leyou.item.web;

import com.leyou.common.vo.BrandVo;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 查询品牌列表
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
        @RequestParam(value = "page",defaultValue = "1") Integer page,
        @RequestParam(value = "rows",defaultValue = "5") Integer rows,
        @RequestParam(value = "sortBy",required = false) String sortBy,
        @RequestParam(value = "desc",defaultValue = "false") Boolean desc,
        @RequestParam(value = "key",required = false) String key
      ){
        PageResult<Brand> result = brandService.queryBrandByPageAndSort(page, rows, sortBy, desc, key);
         if (result == null || result.getItems().size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids){
        brandService.saveBrand(brand,cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 修改品牌
     */
    @PutMapping
    public ResponseEntity<Void> updataBrand(BrandVo brandVo){
        brandService.updataBrand(brandVo);
        return  new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 删除品牌
     */
    @DeleteMapping("bid/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid") Long bid){
        brandService.deleteBrand(bid);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据商品分类id查询品牌
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryByCategoryId(@PathVariable("cid") Long cid){
       return ResponseEntity.ok(brandService.queryByCategoryId(cid));
    }

    /**
     * 根据商品品牌id查询商品品牌
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryByBrandId(@PathVariable("id") Long id){
        return ResponseEntity.ok(brandService.queryByBrandId(id));
    }

    /**
     * 根据ids查询品牌
     * @param ids
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandsByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(brandService.queryBrandByIds(ids));
    }


}
