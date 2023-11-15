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

    /*查询所有商铺*/
    @Select("select * from t_shop")
    List<Shop> list();



    /*通过id删除商铺*/
    @Delete("delete from t_shop where id=#{id}")
    void remove(Long id);

    /**
     * 审核成功，修改为state=1，state=1表示已通过审核
     * @param id
     */
    @Update("update t_shop set state=1 where id=#{id}")
    void successfulAudit(Long id);

    /**
     * 审核失败，state=2为审核失败
     * @param id
     */
    @Update("update t_shop set state=2 where id=#{id}")
    void auditFailure(Long id);

    /*修改商铺信息*/
    @Update("update t_shop set name=#{name},state=#{state},tel=#{tel},address=#{address} where id=#{id}")
    void update(Shop shop);

    /*分页查询商铺*/
    @Select("SELECT * FROM t_shop LIMIT #{offset}, #{pageSize}")
    List<Shop> paginationList(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /*查询商铺的数量*/
    @Select("SELECT COUNT(*) FROM t_shop")
    int count();

    /*通过id查询商铺*/
    @Select("select * from t_shop where id=#{id}")
    Shop  findById(Long id);

    /*通过地址查询商铺*/
    @Select("select * from t_shop where address=#{address}")
    Shop findByAddress(String address);
}
