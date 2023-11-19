package org.nb.pethome.service.impl;

import org.nb.pethome.entity.Employee;
import org.nb.pethome.net.param.SeekingTheLordParam;
import org.nb.pethome.mapper.SeekingTheLordMapper;
import org.nb.pethome.service.ISeekingTheLordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeekingTheLordService implements ISeekingTheLordService {

    private SeekingTheLordMapper seekingTheLordMapper;

    @Autowired
    public SeekingTheLordService(SeekingTheLordMapper seekingTheLordMapper){
        this.seekingTheLordMapper=seekingTheLordMapper;
    }
    @Override
    public int add(SeekingTheLordParam seekingTheLordParam) {
        return seekingTheLordMapper.add(seekingTheLordParam);
    }


    @Override
    public List<SeekingTheLordParam> getPetListByState(int state) {
        return seekingTheLordMapper.getPetListByState(state);
    }


    @Override
    public List<SeekingTheLordParam> getAuditList(long user_id) {
        return seekingTheLordMapper.getAuditList(user_id);
    }

    @Override
    public List<SeekingTheLordParam> getAuditListByShop(long shop_id) {
        return seekingTheLordMapper.getAuditListByShop(shop_id);
    }

    @Override
    public int auditSeekingTheLord(long id) {
        return seekingTheLordMapper.auditSeekingTheLord(id);
    }
}
