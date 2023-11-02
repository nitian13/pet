package org.nb.pethome.mapper;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.nb.pethome.entity.Department;
import org.nb.pethome.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;




@RunWith(SpringRunner.class)
@SpringBootTest
public class DepartmentMapperTest {
    @Autowired
    private DepartmentMapper departmentMapper;

    @Test
    public void testAddDepartment(){
        Department department = new Department();
        department.setSn("001");
        department.setName("新时代企业");
        Employee employee = new Employee();
        employee.setId(1l);
        department.setParent(new Department());
        departmentMapper.add(department);
        System.out.println(department);
    }

    @Test
    public void testAddOtherDepartment(){
        Department department = new Department();
        for(int i=0;i<3;i++){
            department.setSn("00"+(i+4));
            department.setName("开发"+(i+1)+"部");
            Department root = new Department();
            root.setId(11l);
            department.setParent(root);
            departmentMapper.add(department);
        }
    }

    @Test
    public void testDeleteDepartment(){
        departmentMapper.remove(14l);
    }

    @Test
    public void testSelect(){
        Department department = departmentMapper.find(10l);
        System.out.println(department);
    }

    @Test
    public void testUpdateDepartment(){
        Department department = departmentMapper.find(10l);
        department.setName("开发1部");
        departmentMapper.update(department);

    }

//    @Test
//    public void testSelectAll() throws JsonProcessingException {
//        List<Department> departments = departmentMapper.findAll();
//        System.out.println(departments);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = objectMapper.writeValueAsString(departments);
//        System.out.println(json);
//    }

    @Test
    public void testFindCount(){
        System.out.println(departmentMapper.queryCount());
    }

//    @Test
//    public void findDepartmentsByPage(){
//        DepartmentQuery departmentQuery = new DepartmentQuery();
//        departmentQuery.start = 0;
//        departmentQuery.pageSize = 3;
//        System.out.println(departmentMapper.findDepartmentsByPage(departmentQuery));
//    }


}

