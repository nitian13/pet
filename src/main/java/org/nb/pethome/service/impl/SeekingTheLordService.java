package org.nb.pethome.service.impl;

import org.nb.pethome.entity.SeekingTheLord;
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
    public int add(SeekingTheLord seekingTheLord) {
        return seekingTheLordMapper.add(seekingTheLord);
    }

    @Override
    public int addTask(long shop_id, long admin_id, long pet_id, long user_id, long id) {
        return seekingTheLordMapper.addTask(shop_id,admin_id,pet_id,user_id,id);
    }

    @Override
    public List<SeekingTheLord> getPetListByState(int state) {
        return seekingTheLordMapper.getPetListByState(state);
    }

    @Override
    public List<SeekingTheLord> getUserList(long user_id) {
        return seekingTheLordMapper.getUserList(user_id);
    }


}
