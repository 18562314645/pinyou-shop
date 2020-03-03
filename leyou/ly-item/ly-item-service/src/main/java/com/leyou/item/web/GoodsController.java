package com.leyou.item.web;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuBoByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    ){
        PageResult<Spu> pageResult = goodsService.querySpuBoByPage(key, saleable, page, rows);
        if(CollectionUtils.isEmpty(pageResult.getItems())){
            throw new LyException(ExceptionEnums.GOODS_NOT_FIND);
        }
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 保存商品信息
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询spuDetail信息
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetail(@PathVariable("spuId") Long spuId){
        SpuDetail spuDetail=goodsService.querySpuDetail(spuId);
        return ResponseEntity.ok(spuDetail);
    }

    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySku(@RequestParam("id") Long id){
        List<Sku> skuList=goodsService.querySku(id);
        return ResponseEntity.ok(skuList);
    }

    /**
     * 修改商品信息
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
        goodsService.updateGoods(spu);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除商品
     * @param spuId
     * @return
     */
    @DeleteMapping("spu/spuId/{spuId}")
    public ResponseEntity<Void> deleteGoodsBySpuId(@PathVariable("spuId") Long spuId){
        goodsService.deleteGoodsBySpuId(spuId);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据spuid查询spu信息
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsService.querySpuById(id));
    }
}
