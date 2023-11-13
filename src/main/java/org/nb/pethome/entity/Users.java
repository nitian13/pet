package org.nb.pethome.entity;


import lombok.Data;

import java.io.Serializable;

@Data
public class Users implements Serializable {

    //用户id
    private Long id;
    //用户名
    private String username;
    //手机号
    private String phone;
    //密码
    private String password;
    //状态
    private int state;
    //年龄
    private int age;
    //注册时间
    private Long registerTime;
    //token
    private String token;
}
