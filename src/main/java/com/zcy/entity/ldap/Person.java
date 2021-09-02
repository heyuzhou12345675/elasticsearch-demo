package com.zcy.entity.ldap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

/**
 * @author zhouchunyang
 * @Date: Created in 16:55 2021/9/1
 * @Description:
 */
@Data
@ToString
/**
 * objectClass即OpenDJ中出现的objectclass，这是一个数组。而base即Base DN
 */
@Entry(base = "dc=cctv,dc=com", objectClasses = {"cmgPerson","inetOrgPerson","organizationalPerson","person","top"})
public class Person {

    @Id
    /**
     * name属性无法转为json，使用注解忽略
     * DN: Distinguished Name，每个叶子结点到根的路径就是DN
     * 如: cn=test, ou=ou1, o=zhangyang.com
     */
    @JsonIgnore
    private Name dn;
    /**
     * 其中value指cn=wanda中的“cn”而index表示在dn中的位置。
     */
    @DnAttribute(value = "uid")
    private String uid;
    @Attribute(name = "cn")
    private String cn;
    @Attribute(name = "sn")
    private String sn;
    @Attribute(name="mobile")
    private String mobile;
    @Attribute(name = "cmgOu")
    private String cmgOu;
    @Attribute(name = "st")
    private String st;
    @Attribute(name = "street")
    private String street;
    @Attribute(name = "title")
    private String title;
    @Attribute(name = "description")
    private String description;
    @Attribute(name = "displayName")
    private String displayName;
    @Attribute(name = "userPassword")
    private String userPassword;
}
