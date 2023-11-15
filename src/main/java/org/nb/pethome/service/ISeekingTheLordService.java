package org.nb.pethome.service;

import org.apache.ibatis.annotations.Param;
import org.nb.pethome.entity.SeekingTheLord;

import java.util.List;

public interface ISeekingTheLordService {

    int add(SeekingTheLord seekingTheLord);

    int addTask(@Param("shop_id") long shop_id, @Param("admin_id")long admin_id,
                @Param("pet_id")long pet_id, @Param("user_id")long user_id, @Param("id")long id);

    List<SeekingTheLord> getPetListByState(int state);

    List<SeekingTheLord> getUserList(long user_id);
}
