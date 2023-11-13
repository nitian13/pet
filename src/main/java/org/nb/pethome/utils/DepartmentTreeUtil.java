package org.nb.pethome.utils;

import org.nb.pethome.entity.Department;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DepartmentTreeUtil {

    /**
     * list 转 tree
     *
     * @param nodes
     * @return
     */
    public static List<Department> listToTree(List<Department> nodes) {
        Map<Long, List<Department>> nodeMap = nodes.stream().filter(node -> node.getParentId() != 0)
                .collect(Collectors.groupingBy(node -> node.getParent().getId()));

        nodes.forEach(node -> node.setChildren(nodeMap.get(node.getId())));

        List<Department> treeNode = nodes.stream().filter(node -> node.getParentId() == 0).collect(Collectors.toList());
        return treeNode;
    }
}
