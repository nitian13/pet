package org.nb.pethome.service.impl;

import org.nb.pethome.entity.SeekingTheLord;
import org.nb.pethome.mapper.SeekingTheLordMapper;
import org.nb.pethome.service.ISeekingTheLordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
