package org.nb.pethome.mapper;

import org.apache.ibatis.annotations.*;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.net.param.LoginParam;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EmployeeMapper {

    /*增加*/
    @Insert("insert into t_employee(username,email,phone,password,age,state,did)" +
            "values(#{username},#{email},#{phone},#{password},#{age},#{state},#{did})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int add(Employee employee);

    /*删除*/
    @Delete("delete from t_employee where id=#{id}")
    void remove(Long id);

    /*修改*/
    @Update("update t_employee set " +
            " username=#{username}, email=#{email},phone=#{phone},password=#{password},age=#{age}" +
            " where id=#{id}")
    void update(Employee employee);


    /*查询所有*/
    @Select("select * from t_employee")
    List<Employee> findAll();

    /**
     * 找在职人员的
     */
    @Select("select * from t_employee where id=#{id} and state=0")
    Employee findIncumbency(Long id);


    /*根据id查询员工*/
    @Select("select * from t_employee where id=#{id}")
    Employee findById(Long id);

    /*根据名字和密码查询员工*/
    @Select("select * from t_employee where username=#{username} and password=#{password}")
    Employee login(LoginParam loginParam);

    /*根据电话和密码查询员工*/
    @Select("select * from t_employee where phone=#{phone} and password=#{password}")
    Employee select(String phone,String password);


    @Insert("insert into t_employee(username,email,phone,password,age,state)" +
            "values(#{username},#{email},#{phone},#{password},#{age},#{state})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int insert(Employee e);

}
