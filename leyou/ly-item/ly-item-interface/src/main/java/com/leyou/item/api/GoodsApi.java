package com.leyou.item.api;

import com.leyou.item.pojo.Spu;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GoodsApi {

    @GetMapping("spu/page")
    PageResult<Spu> querySpuBoByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    );


    /**
     * 查询spuDetail信息
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    SpuDetail querySpuDetail(@PathVariable("spuId") Long spuId);

    @GetMapping("sku/list")
   List<Sku> querySku(@RequestParam("id") Long spuId);

    /**
     * 根据spuid查询spu信息
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

}
