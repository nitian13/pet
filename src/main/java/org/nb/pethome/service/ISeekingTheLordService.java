package org.nb.pethome.service;

import org.apache.ibatis.annotations.Param;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.net.param.SeekingTheLordParam;

import java.util.List;

public interface ISeekingTheLordService {

    int add(SeekingTheLordParam seekingTheLordParam);

    List<SeekingTheLordParam> getPetListByState(int state);

    List<SeekingTheLordParam> getAuditList(long user_id);

    List<SeekingTheLordParam> getAuditListByShop(long shop_id);

    int auditSeekingTheLord(long id);
}
