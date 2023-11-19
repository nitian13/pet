package org.nb.pethome.entity;

import lombok.Data;

@Data
public class PetProduct {
    //宠物商品id
    private Long id;

    //宠物名字
    private String petName;

    //宠物姓别
    private int petSex;

    //宠物出生日期
    private Long petBirth;

    //是否接种
    private int isInoculation;

    //是否上架,0未上架，1已上架
    private int isShelves;

    //上架时间
    private Long sellTime;

    //卖出时间
    private Long endTime;

    //是否出售，0未出售，1已出售
    private int isSell;

    //成本价
    private double petPrice;

    //售价
    private double petSellPrice;

    //用户id
    private Long user_id;

    //商铺id
    private Long shop_id;

    //管理员id
    private Long admin_id;

    //宠物类别id
    private Long pet_id;

    //寻主id
    private Long seekingthelord_id;

}
