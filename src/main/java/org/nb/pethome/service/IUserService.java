package org.nb.pethome.service;

import org.nb.pethome.net.NetResult;

public interface IUserService {

    /**
     * 发送二维码
     */
    NetResult sendRegisterCode(String phone);
}
