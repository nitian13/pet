package org.nb.pethome.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.nb.pethome.entity.Users;
import org.nb.pethome.net.param.RegisterParam;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UsersMapper {

    @Insert("insert into t_user(username,phone,password,state,age,registerTime)  " +
            "values(#{username},#{phone},#{password},#{state},#{age},#{registerTime})")
    int add(Users users);

    @Select("select * from t_user where phone=#{phone} and password=#{password}")
    Users getUser(@Param("phone") String phone, @Param("password") String password);

    @Select("select * from t_user where phone=#{phone} and password=#{password} and role=1")
    Users getAdmin(String phone, String password);

    @Select("select * from t_user where phone=#{phone}")
    Users selectPhone(String phone);
}
