package com.wu.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wu.pojo.Employee;
import com.wu.pojo.RespBean;
import com.wu.pojo.RespPageBBean;
import com.wu.pojo.Salary;
import com.wu.service.IEmployeeService;
import com.wu.service.ISalaryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工工资账套
 */

@RestController
@RequestMapping("/salary/sobcfg")
public class SalarySobCfgController {

    @Autowired
    private ISalaryService salaryService;
    @Autowired
    private IEmployeeService employeeService;

    @ApiOperation(value = "获取所有工资账套")
    @GetMapping("/salaries")
    public List<Salary> getAllSalary(){
        return salaryService.list();
    }


    @ApiOperation(value = "获取所有员工工资账套")
    @GetMapping("/")
    public RespPageBBean getEmployeeWithSalary(@RequestParam(defaultValue = "1") Integer currentPage,
                                               @RequestParam(defaultValue = "10") Integer size ){

        return employeeService. getEmployeeWithSalary(currentPage,size);
    }

    @ApiOperation(value = "更新员工工资账套")
    @PutMapping("/")
    public RespBean updateEmployeeWithSalary(Integer eid,Integer sid){
       if (employeeService.update(new UpdateWrapper<Employee>().set("salaryId",sid).eq("id",eid))){
           return RespBean.success("更新成功");
       }
        return RespBean.success("更新失败");
    }


}


