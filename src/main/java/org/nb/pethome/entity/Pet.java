package org.nb.pethome.entity;

import lombok.Data;

@Data
public class Pet {

    //id
    private Long id;
    //类别
    private String type;
    //描述
    private String description;

    public Pet(String type, String description){
        this.type = type;
        this.description = description;
    }
}
