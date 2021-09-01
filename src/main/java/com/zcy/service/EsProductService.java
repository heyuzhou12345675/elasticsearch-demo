package com.zcy.service;

import com.zcy.es.entity.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhouchunyang
 * @Date: Created in 14:55 2021/8/31
 * @Description:继承完ElasticsearchRepository类会提供最基本的增删改查方法，
 * 也可以自己定义一些，自己定义的方法命名需要符合规则，并不需要自己去实现。
 */

@Service
public interface EsProductService extends ElasticsearchRepository<Product,String> {
    List<Product> findByProductName(String productName);

    List<Product> findByProductNameAndPrice(String productName,long price);

}
