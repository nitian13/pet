package org.nb.pethome.service;

import org.nb.pethome.entity.Employee;
import org.nb.pethome.net.param.LoginParam;

import java.util.List;

public interface IEmployeeService {
    //添加员工
    boolean add(Employee employee);

    //根据id删除员工
    void remove(Long id);

    //修改员工
    void update(Employee employee);

    Employee findIncumbency(Long id);

    //查询所有员工
    List<Employee> findAll();

    //通过id查询员工
    Employee findById(Long id);

    /*根据名字和密码查询员工*/
    Employee login(LoginParam loginParam);

    /*根据电话和密码查询员工*/
    Employee select(String phone,String password);
}
