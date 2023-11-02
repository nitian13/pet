package org.nb.pethome.service.impl;


import org.nb.pethome.common.DepartmentQuery;
import org.nb.pethome.entity.Department;
import org.nb.pethome.mapper.DepartmentMapper;
import org.nb.pethome.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@Service
public class DepartmentService implements IDepartmentService {


    private DepartmentMapper departmentMapper;

    @Autowired
    public DepartmentService(DepartmentMapper departmentMapper){
        this.departmentMapper = departmentMapper;
    }

    @Transactional
    @Override
    public void add(Department d) {
        departmentMapper.add(d);
    }

    @Transactional
    @Override
    public void remove(Long id) {
        departmentMapper.remove(id);
    }

    @Transactional
    @Override
    public void update(Department d) {
        departmentMapper.update(d);
    }

    @Transactional
    @Override
    public Department find(Long id) {
        return departmentMapper.find(id);
    }

    @Transactional
    @Override
    public List<Department> findAll() {
        return departmentMapper.findAll();
    }

    @Transactional
    @Override
    public Long queryCount() {
        return departmentMapper.queryCount();
    }

    @Transactional
    @Override
    public List<Department> findDepartmentsByPage(DepartmentQuery query) {
        return departmentMapper.findDepartmentsByPage(query);
    }

    @Override
    public List<Department> getDepartmentTreeData() {
        List<Department>departments = departmentMapper.findAll();
        return buildTree(departments);
    }


    public List<Department> buildTree(List<Department> nodes){
        //将这些非顶级节点的数按Did进行分组 这个是pid为key 第一步过Pid=@前节点 第二步进行分组
        Map<Long,List<Department>> nodeMap = nodes.stream().filter(node->node.getParentId()!=0)
                .collect(Collectors.groupingBy(node->node.getParent().getId()));
        //循环设置对应的子节点《id = pid) 上一步以pid 为Key 所以就真接循环获取
        nodes.forEach(node->node.setChildren(nodeMap.get(node.getId())));

        //过跨第一层不是Pid为零的数据 也就是没有根节点的数据
        List<Department>treeNode = nodes.stream().filter(node->node.getParentId()==0).collect(Collectors.toList());
        return treeNode;
    }
}
