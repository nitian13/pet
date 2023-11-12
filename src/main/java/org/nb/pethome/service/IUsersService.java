package org.nb.pethome.service;

import org.apache.ibatis.annotations.Param;
import org.nb.pethome.entity.Users;
import org.nb.pethome.net.param.RegisterParam;

public interface IUsersService {

    int add(RegisterParam registerParam);

    Users getUser(@Param("phone") String phone, @Param("password") String password);

    Users getAdmin(String phone, String password);

    Users selectPhone(String phone);
    Users findById(long id);
}
