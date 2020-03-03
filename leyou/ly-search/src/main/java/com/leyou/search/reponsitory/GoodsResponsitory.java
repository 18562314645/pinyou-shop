package com.leyou.search.reponsitory;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsResponsitory extends ElasticsearchRepository<Goods,Long>{

}
