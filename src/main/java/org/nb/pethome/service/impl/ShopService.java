package org.nb.pethome.service.impl;


import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Shop;
import org.nb.pethome.mapper.EmployeeMapper;
import org.nb.pethome.mapper.ShopMapper;
import org.nb.pethome.service.IShopService;
import org.nb.pethome.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Service
public class ShopService implements IShopService {

    private ShopMapper shopMapper;
    private EmployeeMapper employeeMapper;

    @Autowired
    public ShopService(ShopMapper shopMapper,EmployeeMapper employeeMapper) {
        this.shopMapper = shopMapper;
        this.employeeMapper=employeeMapper;

    }


    @Override
    public int add(Shop shop) {
        return shopMapper.add(shop);
    }

    @Override
    public List<Shop> list() {
        return shopMapper.list();
    }

    @Override
    public void remove(Long id) {
        shopMapper.remove(id);
    }

    //通过审核
    @Override
    public void auditTrue(Long id) {
        //绑定管理员
        Employee employee = new Employee();
        //通过id查询商铺
        Shop shop=shopMapper.findById(id);
        //设置管理员名字
        employee.setUsername(shop.getName());
        //设置管理员电话
        employee.setPhone(shop.getTel());
        //设置加密密码
        employee.setPassword(MD5Util.MD5Encode("123456", "utf-8"));
        //添加管理员
        employeeMapper.insert(employee);
        //根据电话和密码去查询管理员
        Employee e=employeeMapper.select(employee.getPhone(),employee.getPassword());
        //绑定管理员
        shopMapper.addAdmin(e,id);
        //状态改为1审核通过
        shopMapper.auditTrue(id);
    }


    @Override
    public void auditFalse(Long id) {
        shopMapper.auditFalse(id);
    }

    @Override
    public void update(Shop shop) {
        shopMapper.update(shop);
    }

    @Override
    public List<Shop> paginationList(int offset, int pageSize) {
        return shopMapper.paginationList(offset,pageSize);
    }

    @Override
    public int count() {
        return shopMapper.count();
    }

    @Override
    public Shop findById(long id) {
        return shopMapper.findById(id);
    }

    @Override
    public Shop findByAddress(String address) {
        return shopMapper.findByAddress(address);
    }

    @Override
    public Shop findByAdmin(long id) {
        return shopMapper.findByAdmin(id);
    }
}
