package org.nb.pethome.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nb.pethome.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    public void addTest(){
        Product product=new Product();
        product.setName("宠物洗澡1");
        product.setResources("/group1");
        product.setSalePrice(998);
        product.setCostPrice(100);
        product.setCreateTime(System.currentTimeMillis());
        product.setOnSaleTime(System.currentTimeMillis());
        product.setSaleCount(10);
        product.setState(1);
        productMapper.add(product);
        System.out.println(product);
    }
}
