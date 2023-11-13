package org.nb.pethome.mapper;


import org.apache.ibatis.annotations.*;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Pet;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PetMapper {

    /*增加*/
    @Insert("insert into t_pet(type,description)" +
            "values(#{type},#{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int add(Pet pet);


    /*查询宠物*/
    @Select("select * from t_pet")
    List<Pet> list();

    /*通过id查询宠物*/
    @Select("select * from t_pet where id=#{id}")
    Pet findById(long id);

    /*根据id删除宠物*/
    @Delete("delete from t_pet where id=#{id}")
    void deleteById(int id);

}
