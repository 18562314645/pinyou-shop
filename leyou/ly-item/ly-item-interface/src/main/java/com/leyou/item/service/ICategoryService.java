package com.leyou.item.service;

import com.leyou.item.pojo.Category;

import java.util.List;

public interface ICategoryService {
    public List<Category> queryCategory(Long id);
}
