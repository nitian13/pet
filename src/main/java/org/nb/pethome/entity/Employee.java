package org.nb.pethome.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Employee implements Serializable {
    /*主键*/
    private Long id;
    /*员工编号*/
    private long did;
    /*员工名称*/
    private String username;
    /*员工邮箱*/
    private String email;
    /*员工手机号码*/
    private String phone;
    /*员工密码*/
    private String password;
    /*员工年龄*/
    private int age;
    /* 部门 状态0正常，-1 停用*/
    private int state;

    private Department department;

    private String token;
}



