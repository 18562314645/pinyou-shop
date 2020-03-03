package com.leyou.search.pojo;

import java.util.Map;

public class SearchRequest {

    private String key; //搜索条件
    private Integer page;   //关键字

    private String sortBy;  //排序字段
    private Boolean descending; //升序或者降序

    private Map<String,String> filter; //过滤条件

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    private static final Integer DEFAULT_SIZE=20;   //每页大小，不从页面接受，直接订死
    private static final Integer DEFAULT_PAGE=1;    //默认起始页

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if(page==null){
            return DEFAULT_PAGE;
        }
        //获取页码时做一些校验，页码值不能小于1
        return Math.max(DEFAULT_PAGE,page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}
