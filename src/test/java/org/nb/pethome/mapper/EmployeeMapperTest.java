package org.nb.pethome.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nb.pethome.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

}
