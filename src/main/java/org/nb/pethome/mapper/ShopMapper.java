package org.nb.pethome.mapper;

import org.apache.ibatis.annotations.*;
import org.nb.pethome.entity.Shop;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ShopMapper {

    /**
     * 创建部门
     *
     * @param shop
     */
    @Insert("insert into t_shop(name,tel,registerTime,state,address,logo,admin_id) values(#{name},#{tel},#{registerTime},#{state},#{address},#{logo},#{admin.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int add(Shop shop);

    @Select("select * from t_shop")
    List<Shop> list();



    @Delete("delete from t_shop where id=#{id}")
    void remove(Long id);

    /**
     * 审核成功
     * @param id
     */
    @Update("update t_shop set state=1 where id=#{id}")
    void successfulAudit(Long id);
    /**
     * 审核失败
     * @param id
     */
    @Update("update t_shop set state=2 where id=#{id}")
    void auditFailure(Long id);

    @Update("update t_shop set name=#{name},state=#{state},tel=#{tel},address=#{address} where id=#{id}")
    void update(Shop shop);

    @Select("SELECT * FROM t_shop LIMIT #{offset}, #{pageSize}")
    List<Shop> paginationList(@Param("offset") int offset, @Param("pageSize") int pageSize);


    @Select("SELECT COUNT(*) FROM t_shop")
    int count();

    @Select("select * from t_shop where id=#{id}")
    Shop  findById(Long id);

    @Select("select * from t_shop where address=#{address}")
    Shop findByAddress(String address);
}
