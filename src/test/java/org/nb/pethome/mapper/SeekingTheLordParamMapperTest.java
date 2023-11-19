package org.nb.pethome.mapper;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.nb.pethome.net.param.SeekingTheLordParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeekingTheLordParamMapperTest {

    @Autowired
    private SeekingTheLordMapper seekingTheLordMapper;

    @Test
    public void addTest(){
        SeekingTheLordParam seekingTheLordParam =new SeekingTheLordParam();
        seekingTheLordParam.setName("旺财");
        seekingTheLordParam.setSex(0);
        seekingTheLordParam.setAddress("大咖课");
        seekingTheLordParam.setBirth(System.currentTimeMillis());
        seekingTheLordParam.setCreateTime(System.currentTimeMillis());
        seekingTheLordParam.setIsInoculation(1);
        seekingTheLordParam.setPrice(99999999);
        seekingTheLordParam.setState(1);
        seekingTheLordMapper.add(seekingTheLordParam);
        System.out.println(seekingTheLordParam);
    }
}
