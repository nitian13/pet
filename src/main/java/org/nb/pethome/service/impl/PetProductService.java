package org.nb.pethome.service.impl;


import org.nb.pethome.entity.PetProduct;
import org.nb.pethome.mapper.PetProductMapper;
import org.nb.pethome.net.param.SeekingTheLordParam;
import org.nb.pethome.service.IPetProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Service
public class PetProductService implements IPetProductService {

    private PetProductMapper petProductMapper;

    @Autowired
    public PetProductService(PetProductMapper petProductMapper){
        this.petProductMapper=petProductMapper;
    }

    @Override
    public int add(PetProduct petProduct) {
        return petProductMapper.add(petProduct);
    }

    @Override
    public SeekingTheLordParam getSeekingTheLord(long seekingthelord_id) {
        return petProductMapper.getSeekingTheLord(seekingthelord_id);
    }

    @Override
    public List<PetProduct> getPetProduct(long id) {
        return petProductMapper.getPetProduct(id);
    }

    @Override
    public int petShelves(long id ,long sellTime) {
        return petProductMapper.petShelves(id,sellTime);
    }

    @Override
    public int petNotShelves(long id,long endTime) {
        return petProductMapper.petNotShelves(id,endTime);
    }

    @Override
    public int petSell(long id) {
        return petProductMapper.petSell(id);
    }

    @Override
    public PetProduct getPetProductById(long id) {
        return petProductMapper.getPetProductById(id);
    }

}
