package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
* @description:  App端登录业务实现类
* @ClassName ApUserServiceImpl
* @author Zle
* @date 2022-06-22 19:16
* @version 1.0
*/
@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    
    /**
    * @description: 实现用户登录
    * @param: [dto]
    * @return: com.heima.model.common.dtos.ResponseResult
    * @author Zle
    * @date: 2022-06-22 19:20
    */
    @Override
    public ResponseResult login(LoginDto dto) {
        //1.正常登录 用户名和密码
        if (StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())){
            //1.1 根据手机号查询用户信息
            ApUser user = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
            if (user == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户信息不存在");
            }
            //1.2 比对密码
            String salt = user.getSalt();
            String password = dto.getPassword();
            //计算数据库存储密码=MD5(用户密码+盐)
            String pasw = DigestUtils.md5DigestAsHex((password + salt).getBytes());

            if (!pasw.equals(user.getPassword())){
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            //1.3 返回数据  jwt  user
            String token = AppJwtUtil.getToken(user.getId().longValue());
            Map<String,Object> map= new HashMap<>();
            map.put("token",token);
            user.setSalt("");
            user.setPassword("");
            //将用户信息存储到map中，一块返回给前端
            map.put("user",user);
            return ResponseResult.okResult(map);
        }else {
            Map<String,Object> map= new HashMap<>();
            map.put("token",AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }
    }
}
