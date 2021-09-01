package com.zcy.controller;

import com.zcy.es.entity.Product;
import com.zcy.result.ResponseResult;
import com.zcy.service.EsProductService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhouchunyang
 * @Date: Created in 15:03 2021/8/31
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class EsProductController {

    @Autowired
    private EsProductService esProductService;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @RequestMapping("/saveProduct")
    public ResponseResult saveProduct(){
        List<Product> productList = getData();
        Iterable<Product> products = esProductService.saveAll(productList);
        return null;
    }

    @RequestMapping("/selectEsProduct")
    public List<Product> selectEsProduct(@RequestBody Product product){
        List<Product> byProductName = esProductService.findByProductName(product.getProductName());
        return byProductName;
    }

    @RequestMapping("/selectEsProductByCreateTime")
    public List<Product> selectEsProductByCreateTime(@RequestBody Product product){
        String startTime = product.getParams().get("startTime").toString();
        String endTime = product.getParams().get("endTime").toString();
        //加入startTime的Filter以后没有查出来我想要的数据
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.rangeQuery("startTime").gte(startTime).lt(endTime))
                .withQuery(QueryBuilders.matchQuery("productName", product.getProductName()))
                .build();
        SearchHits<Product> search = elasticsearchRestTemplate.search(nativeSearchQuery, Product.class);
        long totalHits = search.getTotalHits();
        //获取查询返回的内容
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> productList = new ArrayList<>();
        for(SearchHit<Product> searchHit:searchHits){
            productList.add(searchHit.getContent());
        }
        return productList;
    }


    @RequestMapping("/removeProduct")
    public void removeProduct(@RequestBody Product product){
        esProductService.delete(product);
    }







    @RequestMapping("/createIndex")
    public boolean createIndex(){
        return false;
    }



    public List<Product> getData(){
        List<Product> list = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1);
        product1.setProductName("善良");
        product1.setClassification("非卖品");
        product1.setProductNum(1);
        product1.setPrice(0);
        product1.setDeleteFlag("0");
        product1.setStartTime(new Date());
        list.add(product1);
        Product product2 = new Product();
        product2.setId(2);
        product2.setProductName("诚实");
        product2.setClassification("非卖品");
        product2.setProductNum(2);
        product2.setPrice(0);
        product2.setDeleteFlag("0");
        product2.setStartTime(new Date());
        list.add(product2);
        Product product3 = new Product();
        product3.setId(3);
        product3.setProductName("勇气");
        product3.setClassification("非卖品");
        product3.setProductNum(3);
        product3.setPrice(0);
        product3.setDeleteFlag("0");
        product3.setStartTime(new Date());
        list.add(product3);
        return list;
    }
}
