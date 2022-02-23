package com.wu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wu.pojo.MenuRole;
import com.wu.pojo.RespBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuhualiang
 * @since 2021-10-12
 */
public interface IMenuRoleService extends IService<MenuRole> {

    /**
     * 更新角色菜单
     * @param rid
     * @param mids
     * @return
     */
    RespBean updateMenuRole(Integer rid, Integer[] mids);
}
