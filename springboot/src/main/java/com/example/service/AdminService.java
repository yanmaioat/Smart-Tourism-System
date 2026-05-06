package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AdminService 是管理员相关的业务逻辑处理类。
 * 提供了增删改查、登录、修改密码以及分页等功能。
 */
@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;// 注入管理员数据访问接口
    /**
     * 添加一个管理员账户，校验用户名是否已存在，设置默认密码和角色等。
     * @param admin 要添加的管理员对象
     */
    public void add(Admin admin) {
        // 检查数据库中是否已有相同用户名
        Admin dbAdmin = adminMapper.selectByUsername(admin.getUsername());
        if (ObjectUtil.isNotNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);// 抛出自定义异常
        }
        // 如果未设置密码，则使用默认密码
        if (ObjectUtil.isEmpty(admin.getPassword())) {
            admin.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        // 如果未设置姓名，默认使用用户名
        if (ObjectUtil.isEmpty(admin.getName())) {
            admin.setName(admin.getUsername());
        }
        // 设置角色为管理员
        admin.setRole(RoleEnum.ADMIN.name());
        // 插入到数据库中
        adminMapper.insert(admin);
    }
    /**
     * 根据 ID 更新管理员信息。
     * @param admin 要更新的管理员对象
     */
    public void updateById(Admin admin) {
        adminMapper.updateById(admin);
    }
    /**
     * 根据管理员 ID 删除记录。
     * @param id 管理员 ID
     */
    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }
    /**
     * 批量删除管理员。
     * @param ids 要删除的管理员 ID 列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            adminMapper.deleteById(id);// 循环调用删除单条记录的方法
        }
    }
    /**
     * 根据 ID 查询管理员信息。
     * @param id 管理员 ID
     * @return 查询到的管理员对象
     */
    public Admin selectById(Integer id) {
        return adminMapper.selectById(id);
    }// 调用 Mapper 查询
    /**
     * 查询所有管理员，支持条件筛选（如用户名模糊匹配等）。
     * @param admin 查询条件封装对象
     * @return 符合条件的管理员列表
     */
    public List<Admin> selectAll(Admin admin) {
        return adminMapper.selectAll(admin);
    }// 调用 Mapper 查询
    /**
     * 分页查询管理员，返回 PageInfo 对象，包含分页信息。
     * @param admin 查询条件
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @return PageInfo<Admin> 分页结果对象
     */
    public PageInfo<Admin> selectPage(Admin admin, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);// 开启分页
        List<Admin> list = adminMapper.selectAll(admin);// 查询当前页的数据
        return PageInfo.of(list); // 返回封装后的分页信息
    }

    /**
     * 管理员登录验证并生成 Token。
     * @param account 登录账号对象（包含用户名和密码）
     * @return 登录成功的管理员对象（含 Token）
     */
    public Admin login(Account account) {
        // 根据用户名查询数据库中的管理员
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        // 用户不存在
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        // 密码错误
        if (!dbAdmin.getPassword().equals(account.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // // 生成 Token（通常包含用户ID+角色+加密签名）
        String token = TokenUtils.createToken(dbAdmin.getId() + "-" + dbAdmin.getRole(), dbAdmin.getPassword());
        // 将 Token 设置进管理员对象中
        dbAdmin.setToken(token);
        return dbAdmin;
    }

    /**
     * 修改管理员密码，需验证原密码是否正确。
     * @param account 原始账号信息（包含旧密码和新密码）
     */
    public void updatePassword(Account account) {
        // 查询当前用户名是否存在
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        // 验证输入的原密码是否正确
        if (!account.getPassword().equals(dbAdmin.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        // 设置新密码
        dbAdmin.setPassword(account.getNewPassword());
        // 更新数据库中的密码
        adminMapper.updateById(dbAdmin);
    }

}
