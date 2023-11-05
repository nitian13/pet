package org.nb.pethome.controller;

import org.nb.pethome.entity.Department;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.service.IDepartmentService;
import org.nb.pethome.service.IEmployeeService;
import org.nb.pethome.utils.MD5Util;
import org.nb.pethome.utils.ResultGenerator;
import org.nb.pethome.utils.StringUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private IDepartmentService iDepartmentService;
    private IEmployeeService iEmployeeService;


    public EmployeeController(IDepartmentService iDepartmentService,IEmployeeService iEmployeeService){
        this.iDepartmentService = iDepartmentService;
        this.iEmployeeService=iEmployeeService;
    }

    @PostMapping("/add")
    public NetResult add(@RequestBody Employee employee) {
        if (StringUtil.isEmpty(employee.getPhone())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,"手机号不能为空");
        }
        if (StringUtil.isEmpty(employee.getUsername())) {
            return ResultGenerator.genErrorResult(NetCode.USERNAME_INVALID, "用户名不能为空");
        }
        if (StringUtil.isEmpty(employee.getEmail())) {
            return ResultGenerator.genErrorResult(NetCode.EMAIL_INVALID, "邮箱不能为空");
        }
        if (StringUtil.isEmpty(employee.getPassword())) {
            employee.setPassword(MD5Util.MD5Encode("123456", "utf-8"));
        }
        Department department = iDepartmentService.find(employee.getDid());
        if (department == null) {
            return ResultGenerator.genErrorResult(NetCode.DEPARTMENT_ID_INVALID, "非法的部门id");
        }
        boolean result = iEmployeeService.add(employee);
        if (!result) {
            return ResultGenerator.genFailResult("添加员工失败");
        }
        return ResultGenerator.genSuccessResult(employee);
    }

    @PostMapping("/delete")
    public NetResult delete(Long id){
        try {
            iEmployeeService.remove(id);
            return ResultGenerator.genSuccessResult(id);
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.REMOVE_DEPARTMENT_ERROR,"删除员工失败！"+e.getMessage());
        }
    }

    @PostMapping("/update")
    public NetResult update(@RequestBody Employee employee) {
        System.out.println(employee);
        try {
            if (StringUtil.isEmpty(employee.getPhone())) {
                return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID,"手机号不能为空");
            }
            if (StringUtil.isEmpty(employee.getUsername())) {
                return ResultGenerator.genErrorResult(NetCode.USERNAME_INVALID, "用户名不能为空");
            }
            if (StringUtil.isEmpty(employee.getEmail())) {
                return ResultGenerator.genErrorResult(NetCode.EMAIL_INVALID, "邮箱不能为空");
            }
            if (StringUtil.isEmpty(employee.getPassword())) {
                employee.setPassword(MD5Util.MD5Encode("123456", "utf-8"));
            }
            Department department = iDepartmentService.find(employee.getDid());
            if (department == null) {
                return ResultGenerator.genErrorResult(NetCode.DEPARTMENT_ID_INVALID, "非法的部门id");
            }
            iEmployeeService.update(employee);
            return ResultGenerator.genSuccessResult(employee);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.UPDATE_DEPARTMENT_ERROR, "修改员工失败！" + e.getMessage());
        }
    }

    @GetMapping("/find")
    public NetResult find(Long id){
        Employee employee = iEmployeeService.findById(id);
        return  ResultGenerator.genSuccessResult(employee);
    }

    @GetMapping("/list")
    public NetResult findAll(){
        List<Employee> employees = iEmployeeService.findAll();
        return ResultGenerator.genSuccessResult(employees);
    }

}
