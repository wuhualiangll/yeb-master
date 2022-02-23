package com.wu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wu.pojo.Department;
import com.wu.pojo.RespBean;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wuhualiang
 * @since 2021-10-12
 */
public interface DepartmentMapper extends BaseMapper<Department> {
    /**
     * 获取所有部门
     * @return
     */
    List<Department> getAllDepartments(Integer parentId );

    /**
     * 添加部门
     * @param department
     */
    void addDep(Department department);

    /**
     * 删除部门
     * @param department
     * @return
     */
    RespBean deleteDep(Department department);
}
