package org.nb.pethome.mapper;

import org.apache.ibatis.annotations.*;
import org.nb.pethome.entity.SeekingTheLord;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SeekingTheLordMapper {

    @Insert("insert into user_msg(name,sex,address,createTime,price,birth,isInoculation,state,user_id,shop_id,admin_id,pet_id)" +
            "values(#{name},#{sex},#{address},#{createTime},#{price},#{birth},#{isInoculation},#{state},#{user_id},#{shop_id},#{admin_id},#{pet_id})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(SeekingTheLord seekingTheLord);

    @Update("update user_msg set shop_id=#{shop_id},admin_id=#{admin_id},pet_id=#{pet_id},user_id=#{user_id} " +
            "where id=#{id}")
    int addTask(@Param("shop_id") long shop_id, @Param("admin_id")long admin_id,
                @Param("pet_id")long pet_id, @Param("user_id")long user_id, @Param("id")long id);
}
