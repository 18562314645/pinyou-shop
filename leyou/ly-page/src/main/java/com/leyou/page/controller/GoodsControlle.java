package com.leyou.page.controller;

import com.leyou.page.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("item")
public class GoodsControlle {

    @Autowired
    private GoodsService goodsService;
    /**
     * 跳转到商品详情页
     * @param id
     * @return
     */
    @GetMapping("{id}.html")
    public String toItemPage(Model model,@PathVariable("id") Long id){
        Map<String, Object> modelMap = goodsService.loadData(id);
        model.addAttribute(modelMap);
       // System.out.println("======================================model"+model);
        return "item";
    }
}
