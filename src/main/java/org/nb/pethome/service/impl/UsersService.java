package org.nb.pethome.service.impl;

import org.nb.pethome.entity.Users;
import org.nb.pethome.mapper.UsersMapper;
import org.nb.pethome.net.param.RegisterParam;
import org.nb.pethome.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService implements IUsersService {

    private UsersMapper usersMapper;

    @Autowired
    public UsersService(UsersMapper usersMapper){
        this.usersMapper = usersMapper;
    }

    @Override
    public int add(RegisterParam registerParam) {
        return usersMapper.add(registerParam);
    }

    @Override
    public Users getUser(String phone, String password) {
        return usersMapper.getUser(phone,password);
    }

    @Override
    public Users getAdmin(String phone, String password) {
        return usersMapper.getAdmin(phone,password);
    }

    @Override
    public Users selectPhone(String phone) {
        return usersMapper.selectPhone(phone);
    }

    @Override
    public Users findById(long id) {
        return usersMapper.findById(id);
    }
}
