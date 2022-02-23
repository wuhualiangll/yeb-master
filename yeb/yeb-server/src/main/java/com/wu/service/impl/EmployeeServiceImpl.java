package com.wu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wu.mapper.EmployeeMapper;
import com.wu.mapper.MailLogMapper;
import com.wu.pojo.*;
import com.wu.service.IEmployeeService;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wuhualiang
 * @since 2021-10-12
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MailLogMapper mailLogMapper;

    /**
     * 员工分页
     * @param currentPage
     * @param size
     * @param employee
     * @param beginDateScope
     * @return
     */
    @Override
    public RespPageBBean getEmployeePage(Integer currentPage, Integer size, Employee employee, LocalDate[] beginDateScope) {
        //开启分页
        Page<Employee> page=new Page<>(currentPage,size);
        IPage<Employee> employeePage = employeeMapper.getEmployeePage(page, employee, beginDateScope);
        RespPageBBean respPageBBean = new RespPageBBean(employeePage.getTotal(),employeePage.getRecords());
        return respPageBBean;
    }

    /**
     * 获取工号
     * @return
     */
    @Override
    public RespBean maxWorkID() {
        List<Map<String, Object>> maps = employeeMapper.selectMaps(new QueryWrapper<Employee>().select("max(workID)"));
        //获得是object 所以转toString
         return RespBean.success("",String.format("%08d", Integer.parseInt(maps.get(0).get("max(workID)").toString())+1));
    }
    /**
     * 添加员工
     * @param employee
     * @return
     */
    @Override
    public RespBean addEmp(Employee employee) {
        //处理合同期限，包留两位小数
        LocalDate beginContract = employee.getBeginContract();
        LocalDate endContract = employee.getEndContract();
        long days = beginContract.until(endContract, ChronoUnit.DAYS);
        DecimalFormat decimalFormat = new DecimalFormat("##.00");
        employee.setContractTerm(Double.parseDouble(decimalFormat.format(days/365.00)));
        if (1==employeeMapper.insert(employee)){
            //根据上面插入员工的id查询，获得插入的员工，因为只有一个所以get0
            Employee emp = employeeMapper.getEmployee(employee.getId()).get(0);
            String msgId = UUID.randomUUID().toString();
             MailLog mailLog = new MailLog();
             mailLog.setMsgId(msgId);
             mailLog.setEid(employee.getId());
             mailLog.setCount(0);
             mailLog.setStatus(0);
             mailLog.setExchange(MailConstants.MAIL_EXCHANGE_NAME);
             mailLog.setRouteKey(MailConstants.MAIL_ROUTING_KEY_NAME);
             mailLog.setTryTime(LocalDateTime.now().plusMinutes(MailConstants.MSG_TIMEOUT));
             mailLog.setCreateTime(LocalDateTime.now());
             mailLog.setUpdateTime(LocalDateTime.now());
             mailLogMapper.insert(mailLog);
            //发送消息
              rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME,MailConstants.MAIL_ROUTING_KEY_NAME
                    ,emp,new CorrelationData(msgId));
          return RespBean.success("添 加成功");
        }else {
            return RespBean.error("添加失败");
        }

    }
    /**
     * 查询员工
     * @param id
     * @return
     */
    @Override
    public List<Employee> getEmployee(Integer id) {
        return employeeMapper.getEmployee(id);
    }

    /**
     * 获取员工的工资账套
     * @param currentPage
     * @param size
     * @return
     */
    @Override
    public RespPageBBean getEmployeeWithSalary(Integer currentPage, Integer size) {
        //开启分页
        Page<Employee> page = new Page<>(currentPage, size);
        IPage<Employee>  employeeIPage=   employeeMapper.getEmployeeWithSalary(page);
        RespPageBBean respPageBBean = new RespPageBBean(employeeIPage.getTotal(), employeeIPage.getRecords());
        return respPageBBean;
    }
}
