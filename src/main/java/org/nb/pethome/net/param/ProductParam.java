package org.nb.pethome.net.param;

import lombok.Data;
import org.nb.pethome.entity.Product;

import java.util.List;

@Data
public class ProductParam {

    private int total;
    private List<Product> productList;
}
