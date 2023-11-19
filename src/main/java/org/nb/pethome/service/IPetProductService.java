package org.nb.pethome.service;

import org.nb.pethome.entity.PetProduct;
import org.nb.pethome.net.param.SeekingTheLordParam;

import java.util.List;

public interface IPetProductService {

    int add(PetProduct petProduct);

    SeekingTheLordParam getSeekingTheLord(long seekingthelord_id);

    List<PetProduct> getPetProduct(long id);
    int petShelves(long id,long sellTime);
    int petNotShelves(long id,long endTime);
    int petSell(long id);
    PetProduct getPetProductById(long id);
}
