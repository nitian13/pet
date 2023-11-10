package org.nb.pethome.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nb.pethome.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeMapperTest {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Test
    public void updateTest() {
        Employee e = employeeMapper.findById(328l);
        e.setUsername("wu");
        employeeMapper.update(e);
        System.out.println(e);
    }

    @Test
    public void addTest() {
        Employee employee = new Employee();
        employee.setAge(12);
        employee.setEmail("123@qq.com");
        employee.setUsername("wjw");
        employee.setPhone("15527320160");
        employee.setPassword("123456");
        employee.setDid(1l);
        employee.setState(0);
        employeeMapper.add(employee);
        System.out.println(employee);
    }

    @Test
    public void listTest() {
        List<Employee> list =employeeMapper.findAll();

        System.out.println(list);
    }

}
