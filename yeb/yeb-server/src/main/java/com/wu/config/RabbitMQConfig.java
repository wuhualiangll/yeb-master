package com.wu.config;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wu.pojo.MailConstants;
import com.wu.pojo.MailLog;
import com.wu.service.IMailLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 发送邮件配置类
 */
@Configuration
public class RabbitMQConfig {
    //日志
    public static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConfig.class);
     @Autowired
     private CachingConnectionFactory factory;
     @Autowired
     private IMailLogService mailLogService;

    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        /**
         *  // data: 消息唯一标识     ack: 确认结果   cause: 失败原因
         */
        rabbitTemplate.setConfirmCallback((data,ack,cause)->{
            String msgId = data.getId();
            if (ack){
                LOGGER.info("消息发送成功",msgId);
                mailLogService.update(new UpdateWrapper<MailLog>().set("status",1).eq("msgId",msgId));
            }else {
                LOGGER.info("消息发送失败",msgId);
            }
        });
        /**
         * 消息发送失败回
         * msg: 消息主题
        * repCode:  响应码
         * repText: 响应描述
         * exchange: 交换机
         * routingkey: 路由键
         */
        rabbitTemplate.setReturnCallback((msg,repCode,repText,exchange,routingkey)->{
            LOGGER.error("消息发送queue失败", msg.getBody());
        });
        return  rabbitTemplate;
    }
    @Bean
    public Queue queue() {
        return new Queue(MailConstants.MAIL_QUEUE_NAME);
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(MailConstants.MAIL_EXCHANGE_NAME);
    }
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(directExchange()).with(MailConstants.MAIL_ROUTING_KEY_NAME);
    }
}
