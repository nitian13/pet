package org.nb.pethome.mapper;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.nb.pethome.entity.SeekingTheLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeekingTheLordMapperTest {

    @Autowired
    private SeekingTheLordMapper seekingTheLordMapper;

    @Test
    public void addTest(){
        SeekingTheLord seekingTheLord=new SeekingTheLord();
        seekingTheLord.setName("旺财");
        seekingTheLord.setSex("公");
        seekingTheLord.setAddress("大咖课");
        seekingTheLord.setBirth(System.currentTimeMillis());
        seekingTheLord.setCreateTime(System.currentTimeMillis());
        seekingTheLord.setIsInoculation(1);
        seekingTheLord.setPrice(99999999);
        seekingTheLord.setState(1);
        seekingTheLordMapper.add(seekingTheLord);
        System.out.println(seekingTheLord);
    }
}
