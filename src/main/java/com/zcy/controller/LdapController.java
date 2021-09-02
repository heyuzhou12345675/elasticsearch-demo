package com.zcy.controller;

import com.zcy.dao.ldap.PersonRepository;
import com.zcy.entity.ldap.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.directory.*;
import java.util.List;

/**
 * @author zhouchunyang
 * @Date: Created in 17:00 2021/9/1
 * @Description:
 */

@RestController
@RequestMapping("/ldap")
public class LdapController {
    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private PersonRepository personRepository;

    @RequestMapping("/findOne")
    public Person findOne(@RequestBody Person person) {
        ContainerCriteria containerCriteria = LdapQueryBuilder.query().where("uid").is(person.getUid());
        Person onePerson = ldapTemplate.findOne(containerCriteria, Person.class);
        System.out.println(onePerson.toString());
        return onePerson;
    }

    @RequestMapping("/findOneByMobile")
    public Person findOneByMobile(@RequestBody Person person) {
        ContainerCriteria containerCriteria = LdapQueryBuilder.query().where("mobile").is(person.getMobile());
        Person one = ldapTemplate.findOne(containerCriteria, Person.class);
        return one;
    }

    @RequestMapping("/searchAll")
    public List<Person> searchAll() {
        List<Person> personList = personRepository.findAll();
        return personList;
    }

    /**
     * lookup是通过DN直接找到某个条目。
     */
    @RequestMapping("/lookupPerson")
    public Person lookupPerson(@RequestBody Person person) {
//        ldapTemplate.lookup();
        return null;
    }

    /**
     * search是对每一个entry进行查询
     */
    @RequestMapping("/searchPerson")
    public Person searchPerson(@RequestBody Person person) {
//        ldapTemplate.search();
        return null;
    }

    /**
     * 绑定人员要把所有必填属性哦度加上
     */
    @RequestMapping("/addPerson")
    public boolean addPerson(@RequestBody Person person) {
//        //首先是objectClass属性
//        BasicAttribute ocattr = new BasicAttribute("objectClass");
//        ocattr.add("top");
//        ocattr.add("person");
//        ocattr.add("uidObject");
//        ocattr.add("inetOrgPerson");
//        ocattr.add("organizationalPerson");
//        // 用户属性
//        Attributes attrs = new BasicAttributes();
//        attrs.put(ocattr);
//        attrs.put("uid", person.getUid());
//        attrs.put("cn", person.getCn());
//        attrs.put("sn", person.getSn());
//        //使用ldap
//        ldapTemplate.bind("uid=", person.getUid(), attrs);

        try {
            //使用dao
            Person savePerson = personRepository.save(person);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    /**
     * 使用template删除
     */
    @RequestMapping("/unbindPerson")
    public boolean unbindPerson(String uid) {
        try {
            ldapTemplate.unbind("uid=" + uid);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 使用dao删除
     */
    @RequestMapping("/deletePerson")
    public boolean deletePerson(@RequestBody Person person){
        try {
            personRepository.delete(person);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    @RequestMapping("/updatePerson")
    public boolean updatePerson(@RequestBody Person person) {
        try {
            //使用ldap
            ldapTemplate.modifyAttributes("uid=" + person.getUid().trim(), new ModificationItem[]{
                    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("cn", person.getCn().trim())),
                    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("displayName", person.getDisplayName().trim())),
                    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", person.getSn().trim())),
                    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("telephoneNumber", person.getMobile().trim())),
                    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("title", person.getTitle().trim()))
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * LDAP用户认证
     */
    @RequestMapping("/authenticate")
    public boolean authenticate(String loginName, String password) {
        EqualsFilter filter = new EqualsFilter("displayName", loginName);
        return ldapTemplate.authenticate("", filter.toString(), password);
    }

    /**
     * 修改密码
     */
    @RequestMapping("/resetPwd")
    public void resetPwd(String loginName, String newPassword) throws Exception {
        // 1. 查找AD用户
        LdapQuery query =  LdapQueryBuilder.query().where("displayName").is(loginName);
        Person person = ldapTemplate.findOne(query, Person.class);

        // 2. 创建密码
        String newQuotedPassword = "\"" + newPassword + "\"";
        byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");

        // 3. 修改密码
        ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", newUnicodePassword));
        ldapTemplate.modifyAttributes(person.getUid(), new ModificationItem[]{item});
    }
}
