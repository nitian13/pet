package org.nb.pethome.service;

import org.nb.pethome.net.NetResult;
import org.nb.pethome.net.param.LoginParam;
import org.nb.pethome.net.param.RegisterParam;


public interface IUserService {

    /**
     * 发送二维码
     */
    NetResult sendRegisterCode(String phone);


    NetResult adminLogin(LoginParam loginParam);

    NetResult login(LoginParam loginParam) throws Exception;

    NetResult register(RegisterParam registerParam);
}
