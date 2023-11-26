package org.nb.pethome.entity;

import lombok.Data;

@Data
public class Product {

    //产品id
    private Long id;

    //产品名字
    private String name;

    //资源
    private String resources;

    //售价
    private double salePrice;

    //售罄时间
    private long offSaleTime;

    //售中时间
    private long onSaleTime;

    //状态,0为未上架，1为上架
    private int state;

    //成本价格
    private double costPrice;

    //创建时间
    private long createTime;

    //销售数量
    private int saleCount;

}
