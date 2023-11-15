package org.nb.pethome.service.impl;

import org.nb.pethome.entity.Department;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.mapper.DepartmentMapper;
import org.nb.pethome.mapper.EmployeeMapper;
import org.nb.pethome.net.param.LoginParam;
import org.nb.pethome.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@Service
public class EmployeeService implements IEmployeeService {

    private DepartmentMapper departmentMapper;

    private EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeService(DepartmentMapper departmentMapper,EmployeeMapper employeeMapper){
        this.departmentMapper = departmentMapper;
        this.employeeMapper=employeeMapper;
    }

    /*添加员工*/
    @Transactional
    @Override
    public boolean add(Employee employee) {
        //定义一个变量rows来接收数据
        int rows=employeeMapper.add(employee);
        //如果为0，则添加失败
        if (rows==0){
            return false;
        }else {
            //rows=1添加成功，通过员工的部门id找到相对应的部门
            Department department=this.departmentMapper.find(employee.getDid());
            //设置一下员工的部门即把这个数据赋值给员工
            employee.setDepartment(department);
            return true;
        }
    }

/*根据id删除员工*/
    @Transactional
    @Override
    public void remove(Long id) {
        employeeMapper.remove(id);
    }


    /*修改员工*/
    @Transactional
    @Override
    public void update(Employee employee) {
        employeeMapper.update(employee);
    }

    /**/
    @Override
    public Employee findIncumbency(Long id) {
        return employeeMapper.findIncumbency(id);
    }

    /*查询所有员工*/
    @Override
    public List<Employee> findAll() {
        return employeeMapper.findAll();
    }

    /*通过id查询员工*/
    @Override
    public Employee findById(Long id) {
        return employeeMapper.findById(id);
    }

    /*根据名字和密码查询员工*/
    @Override
    public Employee login(LoginParam loginParam) {
        return employeeMapper.login(loginParam);
    }

    /*根据电话和密码查询员工*/
    @Override
    public Employee select(String phone, String password) {
        return employeeMapper.select(phone,password);
    }


}
