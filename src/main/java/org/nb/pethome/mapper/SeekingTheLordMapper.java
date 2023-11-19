package org.nb.pethome.mapper;

import org.apache.ibatis.annotations.*;
import org.nb.pethome.net.param.SeekingTheLordParam;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SeekingTheLordMapper {

    /*添加寻主信息*/
    @Insert("insert into user_msg(name,sex,address,createTime,price,birth,isInoculation,state,user_id,shop_id,admin_id,pet_id)" +
            "values(#{name},#{sex},#{address},#{createTime},#{price},#{birth},#{isInoculation},#{state},#{user_id},#{shop_id},#{admin_id},,#{pet_id})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(SeekingTheLordParam seekingTheLordParam);


    /*根据state的状态去查询寻主信息*/
    @Select("select * from user_msg where ")
    List<SeekingTheLordParam> getPetListByState(int state);

    /*通过用户的id去查询寻主信息*/
    @Select("select * from user_msg where user_id=#{user_id}")
    List<SeekingTheLordParam> getAuditList(long user_id);

    /*通过商铺的id去查询寻主信息*/
    @Select("select * from user_msg where shop_id=#{shop_id}")
    List<SeekingTheLordParam> getAuditListByShop(long shop_id);

    //审核寻主，state=1代表已审核
    @Update("update user_msg set state=1 where id=#{id}")
    int auditSeekingTheLord(long id);

    /*通过管理员的id去查询寻主信息*/
    @Select("select * from user_msg where admin_id=#{admin_id}")
    SeekingTheLordParam getSeekingTheLordByAdmin(long admin_id);

}
