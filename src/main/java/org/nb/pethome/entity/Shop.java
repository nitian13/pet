package org.nb.pethome.entity;

import lombok.Data;

@Data
public class Shop {

    private Long id;
    private String name;
    private String tel;
    private long registerTime;
    private int state=0;//state为0代表未审核
    private String address;
    private  String logo;
    private Employee admin;

}
