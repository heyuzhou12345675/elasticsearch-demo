package com.zcy.controller;

import cn.hutool.core.date.DateUtil;
import com.zcy.constant.Constants;
import com.zcy.es.entity.*;
import com.zcy.utils.ServletUtils;
import com.zcy.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouchunyang
 * @Date: Created in 17:26 2021/12/1
 * @Description:
 */

@Slf4j
@RestController
@RequestMapping("/aceLog")
public class AceLogController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 分组查询
     */
    @RequestMapping("/groupBySearch")
    public List<AceLog> groupBySearch(Pageable pageable, AceLogSearch search){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (search.getStart() != null && search.getEnd() != null) {
            String start = DateUtil.format(DateUtil.offsetHour(search.getStart(), -8), "yyyy-MM-dd HH:mm:ss.SSS");
            String end = DateUtil.format(DateUtil.offsetHour(search.getEnd(), -8), "yyyy-MM-dd HH:mm:ss.SSS");
            boolQueryBuilder.must(QueryBuilders.rangeQuery("firstTimestamp").from(start).to(end));
        } else {
            if (search.getStart() != null) {
                String start = DateUtil.format(DateUtil.offsetHour(search.getStart(), -8), "yyyy-MM-dd HH:mm:ss.SSS");
                boolQueryBuilder.must(QueryBuilders.rangeQuery("firstTimestamp").from(start));
            }
            if (search.getEnd() != null) {
                String end = DateUtil.format(DateUtil.offsetHour(search.getEnd(), -8), "yyyy-MM-dd HH:mm:ss.SSS");
                boolQueryBuilder.must(QueryBuilders.rangeQuery("firstTimestamp").to(end));
            }
        }
//        boolQueryBuilder.must(QueryBuilders.matchQuery("sourceSysID",""))

        // 创建terms桶聚合，聚合名字=by_shop, 字段=shop_id，根据sourceSysID分组
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("by_shop")
                .script(new Script("doc['sourceSysID'] +'#'+doc['targetSysID'] +'#'+doc['targetService']"))
//                .script(new Script("doc['sourceSysID'] +'#'+doc['targetSysID']"))
//                .field("targetService")
                .size(2000);
        NativeSearchQuery nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .addAggregation(aggregationBuilder)
                .withPageable(pageable)
                .build();
        SearchHits<AceLog> searchHits = elasticsearchRestTemplate.search(nativeSearchQueryBuilder, AceLog.class);
        List<AceLog> resultAceLog = getResultAceLog(searchHits);
        Aggregations aggregations = searchHits.getAggregations();
        // 根据by_shop名字查询terms聚合结果
        Terms byShopAggregation = aggregations.get("by_shop");
        // 遍历terms聚合结果
        System.out.println("总记录数:"+byShopAggregation.getBuckets().size());
        for (Terms.Bucket bucket  : byShopAggregation.getBuckets()) {
            //获取文档数
            long docCount = bucket.getDocCount();
            Object key = bucket.getKey();
            System.out.println(key.toString()+":"+docCount);
            Aggregations bucketAggregations = bucket.getAggregations();
            System.out.println();
        }
        return resultAceLog;
    }


    public List<AceLog> getResultAceLog(SearchHits<AceLog> search) {
        List<SearchHit<AceLog>> searchHits = search.getSearchHits();
        List<AceLog> list = new ArrayList<>();
        for (SearchHit<AceLog> hit : searchHits) {
            list.add(hit.getContent());
        }
        return list;
    }

    /**
     * 设置请求分页数据 用于 es
     */
    protected PageDomain startPageForEs() {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(ServletUtils.getParameterToInt(Constants.PAGE_NUM));
        pageDomain.setPageSize(ServletUtils.getParameterToInt(Constants.PAGE_SIZE));
        pageDomain.setOrderByColumn(ServletUtils.getParameter(Constants.ORDER_BY_COLUMN));
        pageDomain.setIsAsc(ServletUtils.getParameter(Constants.IS_ASC));

        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            return pageDomain;
        } else {
            return null;
        }
    }

    @RequestMapping("/selectAceLogByCreateTime")
    public List<AceLog> selectAceLogByCreateTime(@RequestBody AceLog acelog) {
        String startTime = "2022-01-13 15:40:22.000";
        String endTime = "2022-01-13 15:40:22.999";

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                //时间范围查询(现在确实因为分词问题版本号7.7.0，以String存时间的话，当前版本es会把时间分词，导致搜索无结果)
                //加入keyword关键字
//                .withFilter(QueryBuilders.rangeQuery("logTime").gte(startTime).lt(endTime))
                //文字匹配
                .withQuery(QueryBuilders.matchQuery("errorCode", "301" ))
                //排序
//                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                //分页
                .withPageable(PageRequest.of(0, 100))
                .build();
        System.out.println(nativeSearchQuery.getFilter());
        SearchHits<AceLog> search = elasticsearchRestTemplate.search(nativeSearchQuery, AceLog.class);
        long totalHits = search.getTotalHits();
        System.out.println("搜索命中数" + totalHits);
        //获取查询返回的内容
        List<AceLog> productList = getResultAceLog(search);
        return productList;
    }

    @RequestMapping("/getTest")
    public List<AceLog> getTest(){

        String startTime = "2021-12-24 00:19:02.780";
        String endTime = "2021-12-24 23:59:02.780";
        String sourceSysID = "EM13";
        String targetSysID  = "ES01";
        String targetService  = "TaskReceiveService";


        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("sourceSysID", sourceSysID))
                .must(QueryBuilders.termQuery("targetSysID", targetSysID))
                .must(QueryBuilders.termQuery("targetService", targetService));

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                //时间范围查询(现在确实因为分词问题版本号7.7.0，以String存时间的话，当前版本es会把时间分词，导致搜索无结果)
                //加入keyword关键字
                .withFilter(QueryBuilders.rangeQuery("firstTimestamp").gte(startTime).lt(endTime))
                //文字匹配
                .withQuery(boolQueryBuilder)
                //排序
//                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                //分页
                .withPageable(PageRequest.of(0, 100))
                .build();

        SearchHits<AceLog> search = elasticsearchRestTemplate.search(nativeSearchQuery, AceLog.class);
        long totalHits = search.getTotalHits();
        System.out.println("搜索命中数" + totalHits);
        //获取查询返回的内容
        List<AceLog> aceLogList = getResultAceLog(search);

        //响应时间数据处理
        long esbRequestManageTime = 0;
        long esbResponseManageTime = 0;

        //服务端响应时长
        long serverResponseTime = 0;
        //总体响应时长
        for (AceLog quartzAceLog : aceLogList) {
            esbRequestManageTime += Long.parseLong(quartzAceLog.getFirstLogInterval());
            esbResponseManageTime += Long.parseLong(quartzAceLog.getThirdLogInterval());
            //服务端响应时长
            serverResponseTime += Long.parseLong(quartzAceLog.getSecondLogInterval());
        }
        // 算平均值(四舍五入)
        System.out.println(Math.round(esbRequestManageTime / aceLogList.size()));
        System.out.println(Math.round(esbResponseManageTime / aceLogList.size()));
        System.out.println(Math.round((esbResponseManageTime+esbRequestManageTime)  / aceLogList.size()));
        System.out.println(Math.round(serverResponseTime / aceLogList.size()));
        System.out.println(Math.round((esbResponseManageTime+esbRequestManageTime+serverResponseTime) / aceLogList.size()));
        return aceLogList;
    }
}
