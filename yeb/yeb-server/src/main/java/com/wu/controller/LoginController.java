package com.wu.controller;

import com.sun.deploy.net.HttpResponse;
import com.wu.pojo.Admin;
import com.wu.pojo.AdminLoginparam;
import com.wu.pojo.RespBean;
import com.wu.service.IAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Api(tags = "LoginController")
@RestController
public class LoginController {
    @Autowired
    private IAdminService iAdminService;
    @ApiOperation(value = "登陆之后返回token")
    @PostMapping("/login")
    public RespBean login(@RequestBody AdminLoginparam adminLoginparam, HttpServletRequest request){
        return  iAdminService.login(adminLoginparam.getUsername(),adminLoginparam.getPassword(),adminLoginparam.getCode(),request);
    }

    @ApiOperation(value = "获取当前用户登陆的消息")
    @GetMapping("/admin/info")
    public Admin getAdmininfo(Principal principal){
        System.out.println("进入获取信息");
        if (null == principal) {

            return  null;
        }
       String username = principal.getName();
     Admin admin=   iAdminService.getAdminByUsreName(username);
     admin.setPassword(null);
     //通过用户id查询角色
     admin.setRoles(iAdminService.getRoles(admin.getId()));
     return  admin;

    }




        @ApiOperation(value = "注销登录")
        @PostMapping("/logout")
        public RespBean logout(){
            System.out.println("注销成功");
          return  RespBean.success("注销成功");
        }


}
