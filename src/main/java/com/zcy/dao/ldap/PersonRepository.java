package com.zcy.dao.ldap;

import com.zcy.entity.ldap.Person;
import org.springframework.data.ldap.repository.LdapRepository;

/**
 * @author zhouchunyang
 * @Date: Created in 16:57 2021/9/1
 * @Description:
 */
public interface PersonRepository extends LdapRepository<Person> {
}
