package org.nb.pethome.mapper;

import org.apache.ibatis.annotations.*;
import org.nb.pethome.common.DepartmentQuery;
import org.nb.pethome.entity.Department;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface DepartmentMapper {
    /**
     * 创建部门
     * @param d Department
     */
    @Insert("insert into t_department(sn,name,manager_id,parent_id,state)" +
            "values(#{sn},#{name},#{manager.id},#{parent.id},#{state})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    void add(Department d);

    /**
     * 删除部门
     * @param id
     */
    @Delete("delete from t_department where id=#{id}")
    void remove(Long id);

    /**
     * 修改部门
     * @param d
     */
    @Update("update t_department set " +
            "sn=#{sn},name=#{name},manager_id=#{manager.id}," +
            "parent_id=#{parent.id},state=#{state} " +
            "where id=#{id}")
    void update(Department d);

    /**
     * 查询对应id部门
     * @param id
     * @return
     */
    @Select("select * from t_department where id=#{id}")
    @Results({
            @Result(
                    property = "id",
                    column = "id"
            ),
            @Result(
                    property = "parent",
                    column = "parent_id",
                    javaType = Department.class,
                    one = @One(select = "org.nb.pethome.mapper.DepartmentMapper.findParentDepartment")
            ),
            @Result(
                    property = "children",
                    column = "id",
                    javaType = List.class,
                    many = @Many(select = "org.nb.pethome.mapper.DepartmentMapper.findSubDepartments")
            )
    })
    Department find(Long id);

    /**
     * 查询所有部门
     * @return
     */
    @Select("select * from t_department")
    @Results({
            @Result(
                    property = "id",
                    column = "id"
            ),
            @Result(
                    property = "parentId",
                    column = "parent_id"
            ),
            @Result(
                    property = "parent",
                    column = "parent_id",
                    javaType = Department.class,
                    one = @One(select = "org.nb.pethome.mapper.DepartmentMapper.findParentDepartment")
            ),
            @Result(
                    property = "children",
                    column = "id",
                    javaType = List.class,
                    many = @Many(select = "org.nb.pethome.mapper.DepartmentMapper.findSubDepartments")
            )
    })
    List<Department> findAll();

    /**
     * 查询id的所有的子部门
     * @param id
     * @return
     */
    @Results({//@Results批注用于定义查询的结果映射
            @Result(
                    property = "id", //“id”属性映射到结果集中的“id”列
                    column = "id"
            ),
            @Result(
                    property = "parent", //“parent”属性映射到结果集中的“parent_id”列
                    column = "parent_id",
                    javaType = Department.class,//映射类型
                    one = @One(select = "org.nb.pethome.mapper.DepartmentMapper.findParentDepartment")
                    //one 属性指定这是一对一映射
            ),

    })
    @Select("select * from t_department where parent_id=#{id}")
    List<Department> findSubDepartments(Long id);


    /**
     * 查询id = parent_ id的数据，用来查询id的 parent的组织
     * @param parent_id
     * @return
     */
    @Select("select * from t_department where id=#{parent_id}")
    @Results({
            @Result(
                    property = "id",
                    column = "id"
            ),
            @Result(
                    property = "parent",
                    column = "parent_id",
                    javaType = Department.class,
                    one = @One(select = "org.nb.pethome.mapper.DepartmentMapper.findParentDepartment")
            )
    })
    Department findParentDepartment(Long parent_id);


    /*查询部门的数量*/
    @Select("select count(*) from t_department")
    Long queryCount();

    /**
     * 分页查询
     * @param query
     * @return
     */
    @Select("select " +
            "* from " +
            "t_department " +
            "limit #{start},#{pageSize}")
    @Results({
            @Result(
                    property = "id",
                    column = "id"
            ),
            @Result(
                    property = "parent",
                    column = "parent_id",
                    javaType = Department.class,
                    one = @One(select = "org.nb.pethome.mapper.DepartmentMapper.findParentDepartment")
            ),
            @Result(
                    property = "children",
                    column = "id",
                    javaType = List.class,
                    many = @Many(select = "org.nb.pethome.mapper.DepartmentMapper.findSubDepartments")
            )
    })
    List<Department> findDepartmentsByPage(DepartmentQuery query);


}























