package org.nb.pethome.service.impl;

import org.nb.pethome.entity.Department;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.mapper.DepartmentMapper;
import org.nb.pethome.mapper.EmployeeMapper;
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

    @Transactional
    @Override
    public boolean add(Employee employee) {
        int rows=employeeMapper.add(employee);
        if (rows==0){
            return false;
        }else {
            Department department=this.departmentMapper.find(employee.getDid());
            employee.setDepartment(department);
            return true;
        }
    }


    @Transactional
    @Override
    public void remove(Long id) {
        employeeMapper.remove(id);
    }


    @Transactional
    @Override
    public void update(Employee employee) {
        employeeMapper.update(employee);
    }

    @Override
    public Employee findIncumbency(Long id) {
        return employeeMapper.findIncumbency(id);
    }



    @Override
    public List<Employee> findAll() {
        return employeeMapper.findAll();
    }

    @Override
    public Employee findById(Long id) {
        return employeeMapper.findById(id);
    }

    @Override
    public Employee login(Employee employee) {
        return employeeMapper.login(employee);
    }


    @Override
    public Employee select(String phone, String password) {
        return employeeMapper.select(phone,password);
    }


}
