package org.nb.pethome.net.param;

import lombok.Data;

@Data
public class SeekingTheLordParam {

    private Long id;
    //宠物名字
    private String name;
    //宠物性别，0为公，1为母
    private int sex;
    //地址
    private String address;
    private Long createTime;
    private double price;
    //宠物出生日期
    private Long birth;
    //0是没接种 1是已接种
    private int isInoculation;
    //状态，0是上未审核 1审核了
    private int state;
    //用户发布寻主任务的用户id
    private Long user_id;
    //店铺id
    private Long shop_id;
    //管理员id
    private Long admin_id;

    //宠物类别id
    private long pet_id;

}
