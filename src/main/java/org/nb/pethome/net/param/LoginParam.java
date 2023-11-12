package org.nb.pethome.net.param;

import lombok.Data;

@Data
public class LoginParam {

    String phone;
    String password;
    String username;
    private int type;
}
