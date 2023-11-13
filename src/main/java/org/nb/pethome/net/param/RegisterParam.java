package org.nb.pethome.net.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterParam implements Serializable {

    private String code;
    private String username;
    private String phone;
    private String password;
    private int state;
    private int age;
    private Long registerTime;
}
