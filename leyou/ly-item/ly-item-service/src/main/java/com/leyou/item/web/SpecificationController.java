package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 根据分类id查询分组
 */
@RequestMapping("spec")
@RestController
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecificationByCategoryId(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(specificationService.queryById(cid));
    }

    /**
     * 新增分组
     */
    @PostMapping("group")
    public ResponseEntity<Void> addGroup(@RequestBody SpecGroup specGroup){
        specificationService.addGroup(specGroup);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 删除分组
     * @return
     */
    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") Long id){
        specificationService.deleGroup(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 更新规格分组
     * @return
     */
    @PutMapping("group")
    public ResponseEntity<Void> updateGroup(@RequestBody SpecGroup specGroup){
        specificationService.updateGroup(specGroup);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 查询规格参数
     * @param gid 分组id
     * @param cid 分类id
     * @param generic 是否是sku通用属性参数
     * @param searching 是否是搜索字段
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching
    ){
            List<SpecParam> params=specificationService.queryParams(gid,cid,generic,searching);
            return ResponseEntity.ok(params);
    }

    /**
     * 根据cid查询规格组及对用的规格参数
     * @return
     */
    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(specificationService.querySpecsByCid(cid));
    }
}
