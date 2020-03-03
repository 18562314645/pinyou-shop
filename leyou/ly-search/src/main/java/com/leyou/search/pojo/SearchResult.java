package com.leyou.search.pojo;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.common.vo.PageResult;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResult extends PageResult<Goods> {

    private List<Category> categories;
    private List<Brand> brands;
    private List<Map<String,Object>> specs; //规格参数过滤条件

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }

    public SearchResult() {
    }

    public SearchResult(long total, long totalPages, List<Goods> goodsList, List<Category> categories, List<Brand> brands) {
        super(total, totalPages, goodsList);
        this.categories = categories;

        this.brands = brands;
    }

    public SearchResult(List<Category> categories, List<Brand> brands) {
        this.categories = categories;
        this.brands = brands;
    }

    public SearchResult(long total, long totalPages, List<Goods> goodsList,
                        List<Category> categories, List<Brand> brands,
                        List<Map<String,Object>> specs) {
        super(total, totalPages, goodsList);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }
}
