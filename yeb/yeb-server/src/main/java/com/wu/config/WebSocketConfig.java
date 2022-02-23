package com.wu.config;

import com.wu.config.security.component.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * websocket配置类
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;



    /**
     * 添加这个Endpoint，这样在网页可以通过websocket 连接上服务
     * 也就是我们配置websocket的服务地址，并且可以指定是否使用socketJs
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         * 1.将ws/ep路径注册为stomp的端点，用户连接了这个端点就可以进行websocket通讯了，支持withSockJS
         * 2.setAllowedOrigins("*")  ,允许跨域
         * 3.withSockJS();支持withSockJS访问
         */

        registry.addEndpoint("/ws/ep").setAllowedOriginPatterns("*").withSockJS();
    }

    /**
     * 输入通道参数配置，防止被JWT拦截器拦截
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
           registration.interceptors(new ChannelInterceptor() {
               @Override
               public Message<?> preSend(Message<?> message, MessageChannel channel) {
                  StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,StompHeaderAccessor.class);
                 //判断是否为连接，如果是，需要获取token ,并且设置用户对象
                  if (StompCommand.CONNECT.equals(accessor.getCommand())){
                      String token = accessor.getFirstNativeHeader("Auth-Token");
                      if (!StringUtils.isEmpty(token)){
                          //拿到令牌
                          String authToken = token.substring(tokenHead.length());
                          String username = jwtTokenUtil.getUsernameFormToken(authToken);
                          if (!username.isEmpty()){
                              //登陆
                              UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                              //验证token，是否有效，重新设置用户对象
                              if (jwtTokenUtil.validateToken(authToken,userDetails)){
                                  UsernamePasswordAuthenticationToken authenticationToken
                                          = new UsernamePasswordAuthenticationToken(userDetails, null,
                                          userDetails.getAuthorities());
                                  //getContext()获取springsecurity上下文，
                                  SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                                  accessor.setUser(authenticationToken);
                              }
                          }

                      }
                  }
                   return message;
               }
           });
    }

    /**
     * 配置消息代理 。后端相当于中转站
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
           //配置代理域，可以配置多个，配置代理的目的地前缀为/queue，可以在配配置域上向客户端推送消息
        registry.enableSimpleBroker("/queue");
    }
}
