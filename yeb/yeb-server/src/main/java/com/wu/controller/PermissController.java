package com.wu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wu.pojo.Menu;
import com.wu.pojo.MenuRole;
import com.wu.pojo.RespBean;
import com.wu.pojo.Role;
import com.wu.service.IMenuRoleService;
import com.wu.service.IMenuService;
import com.wu.service.IRoleService;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限组
 * @author cxj
 */
@RestController
@RequestMapping("/system/basic/permiss")
public class PermissController {
    @Autowired
    private IRoleService iRoleService;

    @Autowired
    private IMenuService iMenuService;

    @Autowired
    private IMenuRoleService iMenuRoleService;

    @ApiOperation(value = "获取所有角色")
    @GetMapping("/")
    public List<Role> getAllRoles(){
        return iRoleService.list();
    }

    @ApiOperation(value = "添加角色")
    @PostMapping("/role")
    public RespBean addRoles(@RequestBody Role role){
        if (!role.getName().startsWith("ROLE_")){
            role.setName("ROLE_"+role.getName());
        }
      if (iRoleService.save(role)){
          return RespBean.success("添加成功");
      }
      return  RespBean.error("添加失败");
    }

    @ApiOperation(value ="删除角色")
    @DeleteMapping("/role/{rid}")
    public RespBean deleteRole(@PathVariable Integer rid){
        if (iRoleService.removeById(rid)){
            return RespBean.success("删除成功");
        }
        return  RespBean.error("删除失败！");
    }

    @ApiOperation(value ="查询所有菜单")
    @GetMapping("/menus")
    public List<Menu> getAllMenus(){
        return iMenuService.getAllMenus();
    }

     @ApiOperation(value = "根据角色id查询菜单id")
     @GetMapping("/mid/{rid}")
     public  List<Integer> getMidByRid(@PathVariable Integer rid){
        return iMenuRoleService.list(new QueryWrapper<MenuRole>().eq("rid",rid)).stream().map(MenuRole::getMid)
                .collect(Collectors.toList());
       }

    @ApiOperation(value = "更细角色菜单")
    @PutMapping("/")
    public RespBean updateMenuRole(Integer rid,Integer[] mids){
        return iMenuRoleService.updateMenuRole(rid,mids);

    }



}
