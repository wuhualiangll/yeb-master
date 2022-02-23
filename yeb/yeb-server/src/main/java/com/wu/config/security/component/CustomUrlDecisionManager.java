package com.wu.config.security.component;


import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 权限控制
 * 判断用户角色
 * @author cxj
 */
@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> collection)
            throws AccessDeniedException, InsufficientAuthenticationException {
        for (ConfigAttribute configAttribute : collection) {
            //当前的url所需的角色。就是从SecurityConfig获取的。这个不是配置类那个SecurityConfig
            String needRole = configAttribute.getAttribute();
            /**
             * 如果不是ROLE_LOGIN这个角色，去匹配你自己的角色，因为这个角色是自己
             * 设置进去的，如果角色是ROLE_LOGIN,则进去,发现没有Token则尚未登陆，请登录
             * 否则返回，只有登陆的功能
             */
           //判断角色是否登陆即可访问的角色，此角色在CustomFilter中设置的
            if ("ROLE_LOGIN".equals(needRole)){
               if (authentication instanceof AnonymousAuthenticationToken) {
                   throw  new AccessDeniedException("尚未登陆，请登录");
               }else {
                   return;
               }
            }
            //判断用户角色是否为url所需的角色
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
               if (authority.getAuthority().equals(needRole)){
                    return;
                }
            }
        }
     throw  new AccessDeniedException("权限不足，请联系管理员");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
