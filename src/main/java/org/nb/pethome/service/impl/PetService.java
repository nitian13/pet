package org.nb.pethome.service.impl;

import org.nb.pethome.entity.Pet;
import org.nb.pethome.mapper.PetMapper;
import org.nb.pethome.service.IPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Service
public class PetService implements IPetService {

    private PetMapper petMapper;

    @Autowired
    public PetService(PetMapper petMapper){
        this.petMapper=petMapper;
    }
    @Override
    public int add(Pet pet) {
        return petMapper.add(pet);
    }

    @Override
    public List<Pet> list() {
        return petMapper.list();
    }

    @Override
    public Pet findById(long id) {
        return petMapper.findById(id);
    }

    @Override
    public void deleteById(int id) {
        petMapper.deleteById(id);
    }
}
