package com.wu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wu.pojo.AdminRole;
import com.wu.pojo.RespBean;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wuhualiang
 * @since 2021-10-12
 */
public interface AdminRoleMapper extends BaseMapper<AdminRole> {

    /**
     * 更新操作员下的角色
     * @param adminId
     * @param rids
     * @return
     */
      Integer addAdminRole(@Param("adminId") Integer adminId, @Param("rids") Integer[] rids);
}
