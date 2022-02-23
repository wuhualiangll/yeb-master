package com.wu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wu.config.security.component.AdminUtils;
import com.wu.mapper.MenuMapper;
import com.wu.pojo.Admin;
import com.wu.pojo.Menu;
import com.wu.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wuhualiang
 * @since 2021-10-12
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 根据用户id 查询菜单列表
     * @return
     */
    @Override
    public List<Menu> getMenuByAdminId() {
        Integer adminId= AdminUtils.getCurrentAdmin().getId();
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        //从redis中取
        List<Menu> menus = (List<Menu>) ops.get("men_" + adminId);
        //如果数据空，去数据库中取
        if (CollectionUtils.isEmpty(menus)){
            menus= menuMapper.getMenuByAdminId(adminId);
            //当进去
            ops.set("men_" + adminId,menus);

        }
           return menus;
    }

    /**
     * 根据用户角色查询菜单列表
     * @return
     */
    @Override
    public List<Menu> getMenusWithRole() {
        return menuMapper.getMenusWithRole();
    }


    /**
     * 查询所有菜单
     * @return
     */
    @Override
    public List<Menu> getAllMenus() {
        return menuMapper.getAllMenus();
    }
}
