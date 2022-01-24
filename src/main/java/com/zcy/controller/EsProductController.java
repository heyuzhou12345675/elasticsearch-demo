package com.zcy.controller;

import cn.hutool.core.date.DateUtil;
import com.zcy.es.entity.AceLog;
import com.zcy.es.entity.AceLogSearch;
import com.zcy.es.entity.Product;
import com.zcy.result.ResponseResult;
import com.zcy.service.EsProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                //时间范围查询(现在确实因为分词问题版本号7.7.0，以String存时间的话，当前版本es会把时间分词，导致搜索无结果)
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

    /**
     * 模糊查询，根据分数返回查询结果
     * */
    @RequestMapping("/resultSort")
    public List<Product> resultSort(@RequestBody Product product){
        String startTime = product.getParams().get("startTime").toString();
        String endTime = product.getParams().get("endTime").toString();

        MultiMatchQueryBuilder searchQueryBuilder = null;
        if (StringUtils.isNotBlank(product.getClassification())) {
            searchQueryBuilder = QueryBuilders.multiMatchQuery(product.getClassification(), "classification");
        }

        NativeSearchQuery nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.rangeQuery("searchTime.keyword").gte(startTime).lt(endTime))
                .withQuery(searchQueryBuilder)
                //根据权重排序
                .withSort(SortBuilders.fieldSort("_score").order(SortOrder.DESC))
                .build();


        return null;
    }



    /**
     * 需求:
     * 1、中文搜索、英文搜索、中英混搜   如：“南京东路”，“cafe 南京东路店”
     * 2、全拼搜索、首字母搜索、中文+全拼、中文+首字母混搜   如：“nanjingdonglu”，“njdl”，“南京donglu”，“南京dl”，“nang南东路”，“njd路”等等组合
     * 3、简繁搜索、特殊符号过滤搜索   如：“龍馬”可通过“龙马”搜索，再比如 L.G.F可以通过lgf搜索，café可能通过cafe搜索
     * 4、排序优先级为： 以关键字开头>包含关键字
     *
     * 解决方案:使用multi_field为搜索字段建立不同类型的索引，有全拼索引、首字母简写索引、Ngram索引以及IK索引，从各个角度分别击破，然后通过char-filter进行特殊符号与简繁转换。
     * 如何创建我想要的索引？
     * */
//    public void selectOne(){
//
//    }

    @RequestMapping("/removeProduct")
    public void removeProduct(@RequestBody Product product) {
        esProductService.delete(product);
    }


    @RequestMapping("/createIndex")
    public boolean createIndex() {
        return false;
    }

    @RequestMapping("/selectOne")
    public Product selectOne(@RequestBody Product product){
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                //文字匹配
                .withQuery(QueryBuilders.matchQuery("id", product.getId()))
                .build();
        SearchHits<Product> search = elasticsearchRestTemplate.search(nativeSearchQuery, Product.class);
        List<Product> result = getResult(search);
        return result.get(0);
    }


    /**
     * 同一条件 or查询 name = "善良" or name = "勇气"
     * @return
     */
    @RequestMapping("/selectListByOr")
    public List<Product> selectListByOr(){
        List<String> list = new ArrayList<>();
        list.add("善良");
        list.add("勇气");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for(int i = 0; i < list.size(); i++){
            boolQueryBuilder.should(QueryBuilders.matchQuery("productName", list.get(i)));
        }
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withFilter(boolQueryBuilder)
                .build();

        SearchHits<Product> search = elasticsearchRestTemplate.search(nativeSearchQuery, Product.class);
        List<Product> productList = getResult(search);
        for(Product product: productList){
            System.out.println("搜索到了"+product.getProductName());
        }
        return productList;
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
