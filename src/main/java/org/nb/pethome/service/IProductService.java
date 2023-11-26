package org.nb.pethome.service;

import org.apache.ibatis.annotations.Param;
import org.nb.pethome.entity.Product;
import org.nb.pethome.entity.Shop;

import java.util.List;

public interface IProductService {

    int add(Product product);
    int update(Product product);
    int delete(Long id);
    int shelves(Long id);
    int takenOff(Long id);
    List<Product> List(@Param("offset") int offset, @Param("pageSize") int pageSize);
    int count();
    Product getProductById(Long id);
    int buyProduct(Long id);
}
