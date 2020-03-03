package com.leyou.item.api;

import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.SpecGroup;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface SpecApi {
    @GetMapping("spec/groups/{cid}")
    List<SpecGroup> querySpecificationByCategoryId(@PathVariable("cid") Long cid);

    /**
     * 查询规格参数
     * @param gid 分组id
     * @param cid 分类id
     * @param generic 是否是sku通用属性参数
     * @param searching 是否是搜索字段
     * @return
     */
    @GetMapping("spec/params")
    List<SpecParam> queryParams(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching
    );

    /**
     * 根据cid查询规格组及对用的规格参数
     * @return
     */
    @GetMapping("spec/{cid}")
    public List<SpecGroup> querySpecsByCid(@PathVariable("cid") Long cid);
}
