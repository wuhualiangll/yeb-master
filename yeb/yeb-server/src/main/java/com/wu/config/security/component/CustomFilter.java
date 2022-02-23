package com.wu.config.security.component;

import com.wu.pojo.Menu;
import com.wu.pojo.Role;
import com.wu.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

/**
 * 权限控制
 * 根据请求的url分析请求所需的角色
 *
 */
@Component
public class CustomFilter implements FilterInvocationSecurityMetadataSource {
    @Autowired
    private IMenuService iMenuService;

  AntPathMatcher antPathMatcher=  new AntPathMatcher();
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
      //获取请求的URL
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        List<Menu> menus = iMenuService.getMenusWithRole();
        for (Menu menu : menus) {
            //判断请求的url和角色的是否匹配
            if (antPathMatcher.match(menu.getUrl(),requestUrl)){
                String[] str = menu.getRoles().stream().
                        map(Role::getName).toArray(String[]::new);
                //把url的全部角色放进去，一般有多个
                return SecurityConfig.createList(str);
            }
        }
        //没有匹配的url,默认登陆即可访问
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
