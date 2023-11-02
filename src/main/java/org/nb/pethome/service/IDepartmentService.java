package org.nb.pethome.service;

import org.nb.pethome.common.DepartmentQuery;
import org.nb.pethome.entity.Department;

import java.util.List;


public interface IDepartmentService {
    void add(Department d);
    void remove(Long id);
    void update(Department d);
    Department find(Long id);
    List<Department> findAll();
    Long queryCount();
    List<Department> findDepartmentsByPage(DepartmentQuery query);

    List<Department> getDepartmentTreeData();
}
