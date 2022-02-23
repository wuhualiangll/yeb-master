package com.wu.controller;


import com.wu.pojo.Admin;
import com.wu.pojo.RespBean;
import com.wu.pojo.Role;
import com.wu.service.IAdminService;
import com.wu.service.IRoleService;
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
@RequestMapping("/system/admin")
public class AdminController {
    @Autowired
    private IAdminService  iAdminService;
    @Autowired
    private IRoleService roleService;

    /**
     * 获取所有操作员
     * @param keywords
     * @return
     */
    @ApiOperation(value ="获取所有操作员" )
    @GetMapping("/")
    public List<Admin> getAllAdmins(String keywords){
        return  iAdminService.getAllAdmins(keywords);
    }

    /**
     * 更新操作员
     * @param admin
     * @return
     */
    @ApiOperation("更新操作员")
    @PutMapping("/")
    public RespBean updateAdmin(@RequestBody Admin admin){
        if (iAdminService.updateById(admin)){
            return RespBean.success("更新成功");
        }
        return RespBean.error("更新操作失败");
    }

    /**
     * 删除操作员
     * @param id
     * @return
     */
    @ApiOperation("删除操作员")
    @DeleteMapping("/{id}")
    public RespBean updateAdmin(@PathVariable Integer id){
        if (iAdminService.removeById(id)){
            return RespBean.success("删除成功");
        }
        return RespBean.error("删除操作失败");
    }


     @ApiOperation(value = "获取所有角色")
     @GetMapping("/roles")
     public List<Role> getAllRoles() {
         return roleService.list();
     }

     @ApiOperation(value = "更新操作员的角色")
     @PutMapping("/role")
     public RespBean updateAdminRole(Integer adminId, Integer[] rids) {
         return iAdminService.updateAdminRole(adminId, rids);
     }

}
