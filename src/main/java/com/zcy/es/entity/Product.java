package com.zcy.es.entity;

import com.zcy.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author zhouchunyang
 * @Date: Created in 9:40 2021/8/31
 * @Description:
 */


/**
 * 索引名称与实体类一致
 */
@Data
//@Document(indexName = "product")
public class Product extends BaseEntity {
    @Id
    private long id;

    /**
     * 设置为Text,可以分词
     */
    @Field(type = FieldType.Text)
    private String productName;

    /**
     * 分类 开启ik分词器 (ik_max_word最细粒度分词)
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", store = true, searchAnalyzer = "ik_max_word")
    private String classification;

    @Field(type = FieldType.Long)
    private long price;

    @Field(type = FieldType.Date, name = "startTime", format = DateFormat.date_hour_minute_second, pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @Field(type = FieldType.Date, name = "endTime", format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @Field(type = FieldType.Integer)
    private int productNum;

    /**
     * 删除标识    0:未删除，1:已删除
     * 设置为keyword,但同时也就不能分词
     */
    @Field(type = FieldType.Keyword, name = "deleteFlag")
    private String deleteFlag;

    @Field(type = FieldType.Keyword, name = "searchTime", format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private String searchTime;

    @Field(type = FieldType.Date, name = "birthday", format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;

}
