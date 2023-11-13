package org.nb.pethome.service;

import org.nb.pethome.entity.Pet;

import java.util.List;

public interface IPetService {
    int add(Pet pet);
    List<Pet> list();
    Pet findById(long id);
    void deleteById(int id);
}
