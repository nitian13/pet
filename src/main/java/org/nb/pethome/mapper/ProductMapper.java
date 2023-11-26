package org.nb.pethome.mapper;

import org.apache.ibatis.annotations.*;
import org.nb.pethome.entity.Department;
import org.nb.pethome.entity.Product;
import org.nb.pethome.entity.Shop;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProductMapper {


    /*增加服务商品*/
    @Insert("insert into t_product(name,resources,salePrice,offSaleTime,onSaleTime,state,costPrice,createTime,saleCount)" +
            "values(#{name},#{resources},#{salePrice},#{offSaleTime},#{onSaleTime},#{state},#{costPrice},#{createTime},#{saleCount})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(Product product);

    //修改服务商品
    @Update("update t_product set " +
            "name=#{name},salePrice=#{salePrice},state=#{state}," +
            "costPrice=#{costPrice},saleCount=#{saleCount} " +
            "where id=#{id}")
    int update(Product product);

    //删除商品
    @Delete("delete from t_product where id=#{id}")
    int delete(Long id);


    //上架
    @Update("update t_product set state=1 where id=#{id}")
    int shelves(Long id);

    //下架
    @Update("update t_product set state=0 where id=#{id}")
    int takenOff(Long id);

    /*分页查询商品*/
    @Select("SELECT * FROM t_product LIMIT #{offset}, #{pageSize}")
    List<Product> List(@Param("offset") int offset, @Param("pageSize") int pageSize);

    //查询所有商品的数量
    @Select("SELECT COUNT(*) FROM t_product")
    int count();

    //通过id去找商品
    @Select("select*from t_product where id=#{id}")
    Product getProductById(Long id);

    //购买商品
    @Update("update t_product set saleCount=#{saleCount} where id=#{id}")
    int buyProduct(Long id);


}
