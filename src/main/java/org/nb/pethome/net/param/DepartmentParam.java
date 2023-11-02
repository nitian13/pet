package org.nb.pethome.net.param;

import lombok.Data;


@Data
public class DepartmentParam {


    private String sn;
    private String name;
    private String dirPath;
    private  int state;

    private long parentId;

}
