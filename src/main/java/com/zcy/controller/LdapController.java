package com.zcy.controller;

import com.zcy.dao.ldap.PersonRepository;
import com.zcy.entity.ldap.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
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
    public Person findOne(@RequestBody Person person){
        ContainerCriteria containerCriteria = LdapQueryBuilder.query().where("uid").is(person.getUid());
        Person onePerson = ldapTemplate.findOne(containerCriteria, Person.class);
        System.out.println(onePerson.toString());
        return onePerson;
    }


    @RequestMapping("/findOneByMobile")
    public Person findOneByMobile(@RequestBody Person person){
        ContainerCriteria containerCriteria = LdapQueryBuilder.query().where("mobile").is(person.getMobile());
        Person one = ldapTemplate.findOne(containerCriteria, Person.class);
        return one;
    }

    /**
     * 绑定人员要把所有必填属性哦度加上
     *
     */
    @RequestMapping("/addPerson")
    public boolean addPerson(@RequestBody Person person){
        //首先是objectClass属性
        BasicAttribute ocattr = new BasicAttribute("objectClass");
        ocattr.add("top");
        ocattr.add("person");
        ocattr.add("uidObject");
        ocattr.add("inetOrgPerson");
        ocattr.add("organizationalPerson");
        // 用户属性
        Attributes attrs = new BasicAttributes();
        attrs.put(ocattr);
        attrs.put("",person.getUid());
        attrs.put("",person.getCn());
        attrs.put("",person.getSn());
        try{
            ldapTemplate.bind("uid=",person.getUid(),attrs);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }

    @RequestMapping("/deletePerson")
    public boolean deletePerson(String uid){
        try{
            ldapTemplate.unbind("uid="+uid);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @RequestMapping("/updatePerson")
    public boolean updatePerson(@RequestBody Person person){
        try {
            ldapTemplate.modifyAttributes("uid=" + person.getUid().trim(), new ModificationItem[] {
                    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("cn", person.getCn().trim())),
                    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("displayName", person.getDisplayName().trim())),
                    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", person.getSn().trim())),
                    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("telephoneNumber", person.getMobile().trim())),
                    new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("title", person.getTitle().trim()))
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    @RequestMapping("/searchAll")
    public List<Person> searchAll(){
        List<Person> personList = personRepository.findAll();
        return personList;
    }
}
