package org.nb.pethome.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.nb.pethome.entity.SeekingTheLord;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SeekingTheLordMapper {

    @Insert("insert into user_msg(name,sex,address,createTime,price,birth,isInoculation,state,user_id,shop_id,admin_id)" +
            "values(#{name},#{sex},#{address},#{createTime},#{price},#{birth},#{isInoculation},#{state},#{user_id},#{shop_id},#{admin_id})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(SeekingTheLord seekingTheLord);
}
