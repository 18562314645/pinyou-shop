package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.*;
import com.leyou.search.feign.BrandClient;
import com.leyou.search.feign.CategoryClient;
import com.leyou.search.feign.GoodsClient;
import com.leyou.search.feign.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.reponsitory.GoodsResponsitory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SearchService {
    
    @Autowired
    private CategoryClient categoryClient;
    
    @Autowired
    private BrandClient brandClient;
    
    @Autowired
    private SpecClient specClient;
    
    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsResponsitory responsitory;

    @Autowired
    private ElasticsearchTemplate template;
    /**
     * 导入商品服务
     * @param spu
     * @return
     */
    public Goods bulidGoods(Spu spu){

        System.out.println("=====================被调用了");
        Long spuId = spu.getId();
        //查询商品分类名
        List<String> names = categoryClient.queryByCategoryIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        //查询商品品牌
        Brand brand = brandClient.queryByBrandId(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionEnums.BRAND_NOT_FIND);
        }
        //所有的搜索字段拼接到all中，all存入索引库，并进行分词处理，搜索时与all中的字段进行匹配查询
        String all = spu.getTitle() + StringUtils.join(names, " ") + brand.getName();

        //查询sku
        List<Sku> skuList = goodsClient.querySku(spuId);

        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnums.GOODS_NOT_FIND);
        }

        //存储price的集合
        TreeSet<Double> priceSet = new TreeSet<>();

        //设置存储skus的json结构的集合，用map结果转化sku对象，转化为json之后与对象结构相似（或者重新定义一个对象，存储前台要展示的数据，并把sku对象转化成自己定义的对象）
        List<Map<String, Object>> skus = new ArrayList<>();
        //从sku中取出要进行展示的字段，并将sku转换成json格式
        for (Sku sku : skuList) {
            priceSet.add(sku.getPrice());
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            //sku中有多个图片，只展示第一张
            map.put("image", StringUtils.substringBefore(sku.getImages(), ","));
            map.put("price", sku.getPrice());
            skus.add(map);
        }


        //查询规格参数，规格参数中分为通用规格参数和特有规格参数
        List<SpecParam> params = specClient.queryParams(null, spu.getCid3(), null, true);
        if (CollectionUtils.isEmpty(params)) {
            throw new LyException(ExceptionEnums.SPECPARAMS_NOT_FIND);
        }
        //查询商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetail(spuId);

        //获取通用规格参数
        Map<Long, String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获取特有规格参数
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });

        //定义spec对应的map
        HashMap<String, Object> map = new HashMap<>();
        //对规格进行遍历，并封装spec，其中spec的key是规格参数的名称，值是商品详情中的值
        for (SpecParam param : params) {
            //key是规格参数的名称
            String key = param.getName();
            Object value = "";

            if (param.getGeneric()) {
                //参数是通用属性，通过规格参数的ID从商品详情存储的规格参数中查出值
                value = genericSpec.get(param.getId());
                if (param.getNumeric()) {
                    //参数是数值类型，处理成段，方便后期对数值类型进行范围过滤
                    value = chooseSegment(value.toString(), param);
                }
            } else {
                //参数不是通用类型
                value = specialSpec.get(param.getId());
            }
            value = (value == null ? "其他" : value);
            //存入map
            map.put(key, value);
        }


        Goods goods = new Goods();
        goods.setId(spuId);
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(all);
        goods.setPrice(priceSet);
        goods.setSubTitle(spu.getSubTitle());
        goods.setSpecs(map);
        goods.setSkus(JsonUtils.toString(skus));
        System.out.println("====================searchgoods"+goods);
        return goods;
    }

    /**
     * 将规格参数为数值型的参数划分为段
     *
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 搜索商品
     * @param request
     * @return
     */
    public SearchResult search(SearchRequest request) {

        //1.获取请求参数
        Integer page = request.getPage();
        Integer size = request.getSize();
        String sortBy = request.getSortBy();
        Boolean descending = request.getDescending();

        //2.创建查询构建器
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();

        //3.开始分页
        queryBuilder.withPageable(PageRequest.of(page-1,size));

        //4.过滤
        /*queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()));*/

        //基本查询条件
       // QueryBuilder basicQuery= QueryBuilders.matchQuery("all",request.getKey());
        QueryBuilder basicQuery=buildBasicQuery(request);
        queryBuilder.withQuery(basicQuery);
        //通过sourcefilter设置返回的字段，只要subtitle，id，sku
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));

        //排序
        if(StringUtils.isNotBlank(sortBy)){
            //则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending ? SortOrder.DESC:SortOrder.ASC));
        }

        //添加聚合(按照分类id)
        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));

        //添加聚合(按照品牌id)
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));


        //5.执行查询
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);

        //解析聚合结果
        Aggregations aggs = result.getAggregations();

        //解析分类结果
       List<Category> categories = handleCategoryAgg(aggs.get(categoryAggName));

        //完成规格参数的聚合
        List<Map<String,Object>> specs=null;
        //根据商品分类判断是否需要聚合
        if(categories!=null && categories.size()==1){
            System.out.println("=============================="+categories.get(0).getId());
            specs=handleSpecs(categories.get(0).getId(),basicQuery);
        }

        //解析品牌结果
        List<Brand> brands=handleBrandAgg(aggs.get(brandAggName));
        //6.解析结果
        long total = result.getTotalElements(); //总条数
        long totalPages = result.getTotalPages(); //总页数
        List<Goods> goodsList = result.getContent(); //当前页内容

        //返回结果
        return new SearchResult(total,totalPages,goodsList,categories,brands,specs);

    }

    /**
     * 对基本查询条件的封装
     * @param request
     * @return
     */
    private QueryBuilder buildBasicQuery(SearchRequest request) {

        //布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()));

        //过滤条件
        Map<String, String> map = request.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            //处理key
            if(!"cid3".equals(key) && !"brandId".equals(key)){
                key="specs." + key + ".keyword";
            }
            String value = entry.getValue();
            queryBuilder.filter(QueryBuilders.termQuery(key,value));
        }
        return queryBuilder;
    }

    /**
     * 解析商品规格聚合
     * @param
     * @param basicQuery
     * @return
     */
    /*private List<Map<String,Object>> bulidSpecificcationAgg(Long cid, QueryBuilder basicQuery) {
        List<Map<String,Object>> specs=new ArrayList<>();

        //1.查询所有规格参数
        List<SpecParam> params = specClient.queryParams(null, cid, null, true);

        //2.创建查询构建器
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();

        //2.1带上查询条件
        queryBuilder.withQuery(basicQuery);
        queryBuilder.withPageable(PageRequest.of(0, 1));
        //2.2遍历param开始聚合
        for (SpecParam param : params) {
            //获取规格名称
            String name = param.getName();
            //添加聚合
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs."+name+".keyword"));
        }

        //3.获取聚合结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);

        //4.解析聚合结果
        Aggregations aggs = result.getAggregations();
        for (SpecParam param : params) {
            String name = param.getName();

            //拿到聚合名称
            Terms terms = aggs.get(name);
            List<Object> options = terms.getBuckets()
                    .stream().map(s -> s.getKey())
                    .collect(Collectors.toList());
            //构建map集合
            Map<String,Object> map=new HashMap<>();
            map.put("k",name);
            map.put("options",options);
            specs.add(map);
        }

        return specs;
    }*/

    private List<Map<String, Object>> handleSpecs(Long cid, QueryBuilder basicQuery) {
        List<Map<String, Object>> specs = new ArrayList<>();

        //查询可过滤的规格参数
        List<SpecParam> params = specClient.queryParams(null, cid, null, true);

        //基本查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        queryBuilder.withPageable(PageRequest.of(0, 1));

        for (SpecParam param : params) {
            //聚合
            String name = param.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name + ".keyword"));
        }
        //查询
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);

        //对聚合结果进行解析
        Aggregations aggs = result.getAggregations();
        for (SpecParam param : params) {
            String name = param.getName();
            Terms terms = aggs.get(name);
            //创建聚合结果
            HashMap<String, Object> map = new HashMap<>();
            map.put("k", name);
            map.put("options", terms.getBuckets()
                    .stream()
                    .map(b -> b.getKey())
                    .collect(Collectors.toList()));
            specs.add(map);
        }
        return specs;
    }


    /**
     * 解析分类聚合结果
     * @param terms
     * @return
     */
    private List<Category> handleCategoryAgg(LongTerms terms) {

        //获取ids
        try {
            List<Long> ids = terms.getBuckets().stream()
                    .map(c -> c.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            List<Category> categories = categoryClient.queryByCategoryIds(ids);
            return categories;
        } catch (Exception e) {
            log.error("[搜索服务]查询分类异常:",e);
            return null;
        }
    }

    /**
     * 解析品牌聚合结果
     * @param terms
     * @return
     */
    private List<Brand> handleBrandAgg(LongTerms terms) {
        try {
            //获取brand ids
            List<Long> ids = terms.getBuckets()
                    .stream()
                    .map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrandsByIds(ids);
            return brands;
        } catch (Exception e) {
           log.error("[搜索服务]查询品牌异常:",e);
           return null;
        }
    }
}
