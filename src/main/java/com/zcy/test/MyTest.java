package com.zcy.test;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouchunyang
 * @Date: Created in 9:35 2021/9/1
 * @Description:
 */
public class MyTest {
    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("startTime","123");
        map.put("endTime","456");
        JSONObject jsonObject = new JSONObject(map);
        System.out.println(jsonObject.toJSONString());
    }
}
