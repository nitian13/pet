package org.nb.pethome.entity;

import lombok.Data;

@Data
public class SeekingTheLord {

    private Long id;
    //宠物名字
    private String name;
    //宠物性别
    private int sex;//0是母的 1是公的
    //地址
    private String address;
    private Long createTime;
    private double price;
    //宠物出生日期
    private String birth;
    //0是没接种 1是已接种
    private int isInoculation;
    //状态，0是上架了 1是没上架
    private int state;
    //用户id
    private Long user_id;
    //店铺id
    private Long shop_id;
    //管理员id
    private Long admin_id;
}
