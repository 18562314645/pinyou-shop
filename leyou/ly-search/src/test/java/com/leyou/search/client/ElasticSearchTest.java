package com.leyou.search.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.common.vo.PageResult;
import com.leyou.search.feign.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.reponsitory.GoodsResponsitory;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticSearchTest {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsResponsitory responsitory;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;
    /**
     * 创建索引库
     */
    @Test
    public void createIndex(){
        //创建索引库及映射
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }



    @Test
    public void loadData(){

       /* //初始化分页参数
        int page=1;
        int rows=100;

        //查询spu
        do{
        PageResult<Spu>  result= goodsClient.querySpuBoByPage(null, true, page, rows);
        List<Spu> spus = result.getItems();
            System.out.println("spus======================"+spus.size());
        //调用searchservice服务封装goods
        List<Goods> goodsList=new ArrayList<>();
        for (Spu spu : spus) {
            try {
                Goods goods = searchService.bulidGoods(spu);
                System.out.println("=================goods"+goods);
                goodsList.add(goods);
            } catch (Exception e) {
                break;
            }

        }
            System.out.println("====================="+goodsList.size());
        responsitory.saveAll(goodsList);
        //获取当前页的数据条数
           rows  = result.getItems().size();
            page++;
        }while (rows==100);
    }
*/

        int page = 1;
        int size = 0;
        int rows = 100;
        do {
            PageResult<Spu> result = goodsClient.querySpuBoByPage(null, true, page, rows);
            ArrayList<Goods> goodList = new ArrayList<>();
            List<Spu> spus = result.getItems();
            size = spus.size();
            for (Spu spu : spus) {
                try {
                    Goods g = searchService.bulidGoods(spu);
                    goodList.add(g);

                } catch (Exception e) {
                    break;
                }
            }
            this.responsitory.saveAll(goodList);
            page++;
        } while (size == 100);
    }
}
