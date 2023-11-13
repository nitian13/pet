package org.nb.pethome.mapper;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.nb.pethome.entity.Users;
import org.nb.pethome.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UsersMapper userMapper;

    @Test
    public void testAdd(){

//        Users users=new Users();
//        users.setUsername("aa");
//        users.setPhone("15527320160");
//        users.setPassword(MD5Util.MD5Encode("123456","utf-8"));
//        users.setAge(18);
//        users.setState(1);
//        users.setRegisterTime(System.currentTimeMillis());
//        userMapper.add(users);
    }

}
