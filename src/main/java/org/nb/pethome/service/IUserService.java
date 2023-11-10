package org.nb.pethome.service;

import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Users;
import org.nb.pethome.net.NetResult;


public interface IUserService {

    /**
     * 发送二维码
     */
    NetResult sendRegisterCode(String phone);


    NetResult adminLogin(Employee employee);

    NetResult login(Employee employee);

    NetResult register(Users users);
}
