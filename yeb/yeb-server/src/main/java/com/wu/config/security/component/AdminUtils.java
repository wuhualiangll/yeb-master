package com.wu.config.security.component;


import com.wu.pojo.Admin;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 操作员的工具类
 * @date:2021/9/12 16:24
 * @author:yang
 */
public class AdminUtils {
    public static Admin getCurrentAdmin() {
        return ((Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
