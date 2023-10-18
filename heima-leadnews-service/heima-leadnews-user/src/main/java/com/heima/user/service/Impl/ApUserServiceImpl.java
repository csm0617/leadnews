package com.heima.user.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.dtos.LoginDto;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;

@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser>implements ApUserService {

    /**
     * app端登录功能
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult login(LoginDto dto) {
        //1.正常登录 用户名和密码
        if (StringUtils.isNotBlank(dto.getPhone())&&StringUtils.isNotBlank(dto.getPassword())) {
            //1.1根据手机号查询用户信息
            ApUser dbUser = this.getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone,dto.getPhone()));
            if (dbUser==null){
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户信息不存在");
            }
            //1.2对比密码
            String salt = dbUser.getSalt();
            String password = dto.getPassword();
            String pswd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!pswd.equals(dbUser.getPassword())){
                return ResponseResult.setAppHttpCodeEnum(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            //1.3返回数据jwt user
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            HashMap<String, Object> map = new HashMap<>();
            map.put("token",token);
            //用户数据脱敏
            dbUser.setSalt("");
            dbUser.setPassword("");
            map.put("user",dbUser);
            return ResponseResult.okResult(map);

        }else {
            //游客登录
            HashMap<String, Object> map = new HashMap<>();
            map.put("token",AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }
    }
}
