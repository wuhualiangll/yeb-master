package com.wu.config.security;

import com.wu.config.security.component.*;
import com.wu.pojo.Admin;
import com.wu.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Security配置类
 * @author cxj
 */
 @Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private IAdminService adminService;
  @Autowired
  private RestAuthorizationEntryPoint restAuthorizationEntryPoint;
  @Autowired
  private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
  @Autowired
  private CustomFilter customFilter;
  @Autowired
  private CustomUrlDecisionManager customUrlDecisionManager;

 /**
  * 为了启用自己定义的UserDetailsService
  * @param auth
  * @throws Exception
  */
 @Override
 protected void configure(AuthenticationManagerBuilder auth) throws Exception {
     //把自己重写的userDetailsService放进来
      auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
 }

    @Override
    public void configure(WebSecurity web) throws Exception {
       web.ignoring().antMatchers(
               "/login",
               "/logout",
               "/css/**",
               "/js/**",
               "/index.html",
               "/favicon.ico",
               "/doc.html",
               "/webjars/**",
               "/swagger-resources/**",
               "/v2/api-docs/**",
               "/captcha",
               "/ws/**"
       );
    }

    @Override
 protected void configure(HttpSecurity http) throws Exception {
  //使用jwt.不需要csrf
    http.csrf()
            .disable()
            //基于token，不需要session
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()

            //所有请求都要求认证
            .anyRequest()
            .authenticated()
            //动态权限
            .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>()
             {
                 @Override
                 public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                     object.setAccessDecisionManager(customUrlDecisionManager);
                     object.setSecurityMetadataSource(customFilter);
                     return object;
                 }
             })
            .and()
            //禁用缓存
            .headers()
            .cacheControl();
    //添加jwt,登陆授权过滤器
    http.addFilterBefore(jwtAuthencationTokenFilter(), UsernamePasswordAuthenticationFilter.class );
    //添加自定义未授权的未登录结果返回；
    http.exceptionHandling()
          .accessDeniedHandler(restfulAccessDeniedHandler)
           .authenticationEntryPoint(restAuthorizationEntryPoint);

 }

 /**
  * 重写
  * 自己定义UserDetailsService
  * @return
  */
 @Override
         @Bean
         public UserDetailsService userDetailsService(){
         return  username ->{
          Admin admin = adminService.getAdminByUsreName(username);
          if (null!=admin){
              //把角色放进去
           admin.setRoles(adminService.getRoles(admin.getId()));
           return  admin;
          }
        throw new UsernameNotFoundException("用户名和密码不正确");
         };
         }

         @Bean
           public PasswordEncoder passwordEncoder(){
            return  new BCryptPasswordEncoder();
         }
          @Bean
          public JwtAuthencationTokenFilter jwtAuthencationTokenFilter(){
          return new JwtAuthencationTokenFilter();
    }
}
