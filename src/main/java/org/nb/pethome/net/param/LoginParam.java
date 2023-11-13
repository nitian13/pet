package org.nb.pethome.net.param;

import lombok.Data;

@Data
public class LoginParam {

    String phone;
    String password;
    String username;
    private String code;
    //type 1管理员 type 0用户
    private int type;
}
