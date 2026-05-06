package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户相关的业务层服务类
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;// 注入用户数据的mapper接口
    /**
     * 添加用户记录的方法
     *
     * @param user 要添加的用户对象
     */
    public void add(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());// 根据用户名查询用户是否存在
        if (ObjectUtil.isNotNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);// 如果用户存在，抛出异常
        }
        if (ObjectUtil.isEmpty(user.getPassword())) {
            user.setPassword(Constants.USER_DEFAULT_PASSWORD);// 如果密码为空，设置默认密码
        }
        if (ObjectUtil.isEmpty(user.getName())) {
            user.setName(user.getUsername()); // 如果姓名为空，使用用户名作为姓名
        }
        user.setRole(RoleEnum.USER.name()); // 设置用户角色为普通用户
        userMapper.insert(user);// 插入新的用户记录到数据库
    }
    /**
     * 根据ID更新用户记录的方法
     *
     * @param user 包含新值的用户对象
     */
    public void updateById(User user) {
        userMapper.updateById(user);
    } // 更新指定ID的用户记录
    /**
     * 根据ID删除用户记录的方法
     *
     * @param id 要删除的用户记录ID
     */
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    } // 删除指定ID的用户记录
    /**
     * 批量删除用户记录的方法
     *
     * @param ids 要删除的用户记录ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            userMapper.deleteById(id); // 循环删除每个指定ID的用户记录
        }
    }
    /**
     * 根据ID查询单条用户记录的方法
     *
     * @param id 要查询的用户记录ID
     * @return 查询到的用户对象
     */
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }// 返回指定ID的用户记录
    /**
     * 查询所有符合条件的用户记录的方法
     *
     * @param user 包含查询条件的对象
     * @return 符合条件的所有用户记录列表
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    } // 返回所有符合查询条件的用户记录
    /**
     * 分页查询用户记录的方法
     *
     * @param user   包含查询条件的对象
     * @param pageNum   当前页码
     * @param pageSize  每页显示数量
     * @return 分页后的用户记录信息
     */
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);// 开启分页插件，设置当前页码和每页大小
        List<User> list = userMapper.selectAll(user);// 查询符合条件的数据
        return PageInfo.of(list); // 将结果封装成PageInfo对象返回
    }

    /**
     * 用户登录的方法
     *
     * @param account 包含用户名和密码的对象
     * @return 登录成功的用户对象，并包含生成的token
     */
    public User login(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());// 根据用户名查询用户
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);// 如果用户不存在，抛出异常
        }
        if (!dbUser.getPassword().equals(account.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);  // 如果密码不匹配，抛出异常
        }
        // 生成token
        String token = TokenUtils.createToken(dbUser.getId() + "-" + dbUser.getRole(), dbUser.getPassword());
        dbUser.setToken(token);// 设置用户的token
        return dbUser; // 返回登录成功的用户对象
    }

    /**
     * 修改用户密码的方法
     *
     * @param account 包含用户名、旧密码和新密码的对象
     */
    public void updatePassword(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());// 根据用户名查询用户
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);// 如果用户不存在，抛出异常
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);// 如果旧密码不匹配，抛出异常
        }
        dbUser.setPassword(account.getNewPassword());// 设置新的密码
        userMapper.updateById(dbUser); // 更新用户记录到数据库
    }

}
