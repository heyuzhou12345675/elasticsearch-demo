package com.zcy.controller;

import com.zcy.es.entity.Product;
import com.zcy.result.ResponseResult;
import com.zcy.service.EsProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.introspector.PropertyUtils;


import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * saveAll保存的三条数据，时间范围查询大于时没有找到数据，无论在kibana还是java
     */
    @RequestMapping("/saveProduct")
    public ResponseResult saveProduct() {
        List<Product> productList = null;
        try {
            productList = getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Iterable<Product> products = esProductService.saveAll(productList);
        return null;
    }

    /**
     * save保存的数据，时间范围查询大于时kibana查询到了，但是java没有查询到
     */
    @RequestMapping("/saveProductOne")
    public Product saveProductOne(@RequestBody Product product) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        product.setSearchTime(dateFormat.format(new Date()));
        Product save = esProductService.save(product);
        return save;
    }


    @RequestMapping("/selectEsProduct")
    public List<Product> selectEsProduct(@RequestBody Product product) {
        List<Product> byProductName = esProductService.findByProductName(product.getProductName());
        return byProductName;
    }

    @RequestMapping("/selectEsProductByCreateTime")
    public List<Product> selectEsProductByCreateTime(@RequestBody Product product) {
        String startTime = product.getParams().get("startTime").toString();
        String endTime = product.getParams().get("endTime").toString();

        //范围搜索不好使
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                //时间范围查询(现在确实因为分词问题，以String存时间的话，当前版本es会把时间分词，导致搜索无结果)
                //加入keyword关键字
                .withFilter(QueryBuilders.rangeQuery("searchTime.keyword").gte(startTime).lt(endTime))
                //文字匹配
                .withQuery(QueryBuilders.matchQuery("productName", product.getProductName()))
                //排序
                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                //分页
                .withPageable(PageRequest.of(0, 2))
                .build();
        System.out.println(nativeSearchQuery.getFilter());
        SearchHits<Product> search = elasticsearchRestTemplate.search(nativeSearchQuery, Product.class);
        long totalHits = search.getTotalHits();
        System.out.println("搜索命中数" + totalHits);
        //获取查询返回的内容
        List<Product> productList = getResult(search);
        return productList;
    }


    /**
     * 关键字搜索 (匹配查询) 分词
     */
    @RequestMapping("/keywordSearch")
    public List<Product> keywordSearch(String keyword) {
        //关键字搜索匹配，分词
        MultiMatchQueryBuilder searchQueryBuilder = null;
        if (StringUtils.isNotBlank(keyword)) {
            searchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "classification");
        }
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders.multiMatchQuery(keyword, "classification"))
                .withQuery(searchQueryBuilder)
                .build();

        System.out.println(nativeSearchQuery.getQuery());
        SearchHits<Product> search = elasticsearchRestTemplate.search(nativeSearchQuery, Product.class);
        //获取查询返回的内容
        List<Product> productList = getResult(search);
        System.out.println(search.getTotalHits());
        return productList;
    }

    /**
     * 高亮，分词
     */
    @RequestMapping("/getHighLightSearch")
    public List<Product> getHighLightSearch(String keyword) {
        //关键字搜索匹配，分词
        MultiMatchQueryBuilder searchQueryBuilder = null;
        if (StringUtils.isNotBlank(keyword)) {
            searchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "classification");
        }
        //高亮 加入以后返回并没有高亮？
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置高亮字段
        highlightBuilder.field("classification");
        //如果要多个字段高亮,这项要为false
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        //下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等
        highlightBuilder.fragmentSize(800000); //最大高亮分片数
        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(searchQueryBuilder)
                .withHighlightBuilder(highlightBuilder)
                .build();

        System.out.println(nativeSearchQuery.getHighlightFields());
        SearchHits<Product> search = elasticsearchRestTemplate.search(nativeSearchQuery, Product.class);
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        for(SearchHit<Product> hit:searchHits){
            Map<String, List<String>> highlightFields = hit.getHighlightFields();
            System.out.println(highlightFields.get("classification"));
            hit.getContent().setClassification(highlightFields.get("classification").toString());
        }



        List<Product> productList = getResult(search);
        return productList;

    }


    @RequestMapping("/removeProduct")
    public void removeProduct(@RequestBody Product product) {
        esProductService.delete(product);
    }


    @RequestMapping("/createIndex")
    public boolean createIndex() {
        return false;
    }

    public List<Product> getResult(SearchHits<Product> search) {
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> list = new ArrayList<>();
        for (SearchHit<Product> hit : searchHits) {
            list.add(hit.getContent());
        }
        return list;
    }


    public List<Product> getData() throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Product> list = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1);
        product1.setProductName("善良");
        product1.setClassification("非卖品");
        product1.setProductNum(1);
        product1.setPrice(0);
        product1.setDeleteFlag("0");
        product1.setSearchTime(dateFormat.format(new Date()));
        product1.setBirthday(dateFormat.parse("2021-09-01 09:01:21"));
        list.add(product1);
        Product product2 = new Product();
        product2.setId(2);
        product2.setProductName("诚实");
        product2.setClassification("非卖品");
        product2.setProductNum(2);
        product2.setPrice(0);
        product2.setDeleteFlag("0");
        product2.setSearchTime(dateFormat.format(new Date()));
        product2.setBirthday(dateFormat.parse("2021-09-02 09:02:22"));
        list.add(product2);
        Product product3 = new Product();
        product3.setId(3);
        product3.setProductName("勇气");
        product3.setClassification("非卖品");
        product3.setProductNum(3);
        product3.setPrice(0);
        product3.setDeleteFlag("0");
        product3.setSearchTime(dateFormat.format(new Date()));
        product3.setBirthday(dateFormat.parse("2021-09-03 09:03:23"));
        list.add(product3);
        return list;
    }
}
