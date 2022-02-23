package com.wu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wu.pojo.Employee;
import com.wu.pojo.RespBean;
import com.wu.pojo.RespPageBBean;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuhualiang
 * @since 2021-10-12
 */
public interface IEmployeeService extends IService<Employee> {

    /**
     * 员工分页
     * @param currentPage
     * @param size
     * @param employee
     * @param beginDateScope
     * @return
     */
    RespPageBBean getEmployeePage(Integer currentPage, Integer size, Employee employee, LocalDate[] beginDateScope);

    /**
     * 获取工号
     * @return
     */
    RespBean maxWorkID();

    /**
     * 添加员工
     * @param employee
     * @return
     */
    RespBean addEmp(Employee employee);

    /**
     * 查询员工
     * @param id
     * @return
     */
    List<Employee> getEmployee(Integer id);

    /**
     * 获取员工的工资账套
     * @param currentPage
     * @param size
     * @return
     */
    RespPageBBean getEmployeeWithSalary(Integer currentPage, Integer size);
}
