package com.wu.mail;

import com.rabbitmq.client.Channel;
import com.wu.pojo.Employee;
import com.wu.pojo.MailConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;

/**
 * 消息接收者
 */
@Component
public class MailReceiver {
    public static final Logger LOGGER = LoggerFactory.getLogger(MailReceiver.class);
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;

    @RabbitListener(queues = MailConstants.MAIL_QUEUE_NAME)
    public void Handle(Message message, Channel channel) {
        Employee employee = (Employee) message.getPayload();
        MessageHeaders headers = message.getHeaders();
        //消息序号
        long tag = (long) (headers.get(AmqpHeaders.DELIVERY_TAG));
        String msgId = (String) headers.get("spring_returned_message_correlation");
        HashOperations hashOperations = redisTemplate.opsForHash();

        try {
            if (hashOperations.entries("mail_log").containsKey(msgId)){
                LOGGER.info("消息已经被消费========》",msgId);
                /**
                 * 手动确认信息
                 * tag:消息序号
                 * multiple：false 是否确认多条消息
                 */
                channel.basicAck(tag,false);
                return;
              }
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            // 发件人
            helper.setFrom(mailProperties.getUsername());
            // 收件人
            helper.setTo(employee.getEmail());
            // 主题
            helper.setSubject("入职欢迎邮件");
            // 发送日期
            helper.setSentDate(new Date());
            // 邮件内容
            Context context = new Context();
            context.setVariable("name", employee.getName());
            context.setVariable("posName", employee.getPosition().getName());
            context.setVariable("joblevelName", employee.getJoblevel().getName());
            context.setVariable("departmentName", employee.getDepartment().getName());
            String mail = templateEngine.process("mail", context);
            helper.setText(mail, true);
            //发送邮件
             javaMailSender.send(mimeMessage);
             LOGGER.info("发送邮件成功");
             //将消息id存入redis
             hashOperations.put("mail_log",msgId,"ok");
             //手动确认信息
            channel.basicAck(tag,false);
        } catch (Exception e) {
            try {
                /**
                 * 手动确认信息
                 * 三个参数分别：
                 * tag:消息序号
                 * multiple：false 是否确认多条消息
                 * requeue:是否回到队列
                 */
                channel.basicNack(tag,false,true);
            } catch (IOException ioException) {
                LOGGER.error("邮件发送失败");
            }
            LOGGER.error("邮件发送失败");
        }
    }

}
