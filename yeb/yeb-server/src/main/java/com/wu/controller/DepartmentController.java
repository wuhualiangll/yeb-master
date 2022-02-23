package com.wu.controller;


import com.wu.pojo.Department;
import com.wu.pojo.RespBean;
import com.wu.service.IDepartmentService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2021-10-30
 */
@RestController
@RequestMapping("/system/basic/department")
public class DepartmentController {
     @Autowired
     private IDepartmentService iDepartmentService;

      @ApiOperation(value = "获取所有部门")
      @GetMapping("/")
     public List<Department> getAllDepartments(){
      return iDepartmentService.getAllDepartments();
     }

     @ApiOperation(value = "添加部门")
    @PostMapping("/")
    public RespBean addDep(@RequestBody Department department){
    return iDepartmentService.addDep(department);
    }

    @ApiOperation(value = "删除部门")
    @DeleteMapping("/{id}")
    public RespBean deleteDep(@PathVariable Integer id){
          return iDepartmentService.deleteDep(id);
    }
}
