package org.nb.pethome.service;

import org.nb.pethome.entity.Employee;

import java.util.List;

public interface IEmployeeService {

    boolean add(Employee employee);

    void remove(Long id);

    void update(Employee employee);

    Employee findIncumbency(Long id);

    List<Employee> findAll();

    Employee findById(Long id);
}
