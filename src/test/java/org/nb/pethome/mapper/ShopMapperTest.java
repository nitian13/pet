package org.nb.pethome.mapper;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopMapperTest {

    @Autowired
    private ShopMapper shopMapper;

    @Test
    public void addTest() {
        Shop shop=new Shop();
        shop.setAddress("123456");
        shop.setState(0);
        shop.setName("ikun");
        shop.setTel("15527320160");
        shop.setRegisterTime(System.currentTimeMillis());
        Employee employee=new Employee();
        employee.setId(0l);
        shop.setAdmin(employee);
        shopMapper.add(shop);
        System.out.println(shop);
    }
    @Test
    public void updateTest() {
        Shop shop=shopMapper.findById(31l);
        shop.setAddress("666");
        shop.setState(0);
        shopMapper.update(shop);
        System.out.println(shop);
    }
}
