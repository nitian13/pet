package org.nb.pethome.entity;
import io.swagger.annotations.ApiModel;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;
@ApiModel(value = "部门对象",description = "新增部门对象")
@Data
public class Department {
    /*主键*/
    private Long id;
    /*部门编号*/
    private String sn;
    /*部门名称*/
    private String name;
    /*暂时不用*/
    private String dirPath;
    /* 部门 状态0正常，-1 停用*/
    private int state;
    /*部门经理 和员工关联*/
    private Employee manager;
    /*开发部门*/
    private Department parent;

    private long parentId;
    /* 开发部门的子部门 */
    private List<Department> children = new ArrayList<>();
}
