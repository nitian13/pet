package org.nb.pethome.service.impl;

import org.nb.pethome.entity.Product;
import org.nb.pethome.entity.Shop;
import org.nb.pethome.mapper.ProductMapper;
import org.nb.pethome.service.IProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Service
public class ProductService implements IProductService {

    private ProductMapper productMapper;

    public ProductService(ProductMapper productMapper){
        this.productMapper=productMapper;
    }
    @Override
    public int add(Product product) {
        return productMapper.add(product);
    }

    @Override
    public int update(Product product) {
        return productMapper.update(product);
    }

    @Override
    public int delete(Long id) {
        return productMapper.delete(id);
    }

    @Override
    public int shelves(Long id) {
        return productMapper.shelves(id);
    }

    @Override
    public int takenOff(Long id) {
        return productMapper.takenOff(id);
    }

    /**
     * 获取分页列表
     */
    @Override
    public List<Product> List(int offset, int pageSize) {
        return productMapper.List(offset,pageSize);
    }

    /**
     * 获取列表总数量
     */
    @Override
    public int count() {
        return productMapper.count();
    }

    @Override
    public Product getProductById(Long id) {
        return productMapper.getProductById(id);
    }

    @Override
    public int buyProduct(Long id) {
        return productMapper.buyProduct(id);
    }
}
