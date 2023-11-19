package org.nb.pethome.mapper;

import org.apache.ibatis.annotations.*;
import org.nb.pethome.entity.Pet;
import org.nb.pethome.entity.PetProduct;
import org.nb.pethome.net.param.SeekingTheLordParam;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PetProductMapper {
    /*增加宠物商品*/
    @Insert("insert into petproduct(petName,petSex,petBirth,isInoculation,petSellPrice,isShelves," +
            "isSell,sellTime,endTime,user_id,seekingthelord_id)" +
            "values(#{petName},#{petSex},#{petBirth},#{isInoculation},#{petSellPrice}," +
            "#{isShelves},#{isSell},,#{sellTime},#{endTime},#{user_id},#{seekingthelord_id})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int add(PetProduct petProduct);

    /*通过寻主id去查询信息*/
    @Select("select * from petproduct where seekingthelord_id=#{seekingthelord_id}")
    SeekingTheLordParam getSeekingTheLord(long seekingthelord_id);

    /*查询宠物商品*/
    @Select("select * from petproduct where admin_id=#{admin_id}")
    List<PetProduct> getPetProduct(long id);

    //上架宠物
    @Update("update petproduct set state=1 ,sellTime=#{sellTime} where id=#{id}")
    int petShelves(long id,long sellTime);

    //下架宠物
    @Update("update petproduct set state=2 ,endTime=#{endTime} where id=#{id}")
    int petNotShelves(long id,long endTime);

    //出售宠物
    @Update("update petproduct set isSell=1 where id=#{id}")
    int petSell(long id);

    /*查询宠物商品通过id*/
    @Select("select * from petproduct where id=#{id}")
    PetProduct getPetProductById(long id);

}
