package com.leyou.item.service;

import com.leyou.item.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ItemService {

    //新增商品
    public Item saveItem(Item item){
        int id=new Random().nextInt(100);
        item.setId(id);
        return item;
    }
}
