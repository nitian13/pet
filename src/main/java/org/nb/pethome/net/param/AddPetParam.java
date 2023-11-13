package org.nb.pethome.net.param;

import lombok.Data;
import org.nb.pethome.entity.SeekingTheLord;

@Data
public class AddPetParam {

    public SeekingTheLord seekingTheLord;
    public int user_id;
}
