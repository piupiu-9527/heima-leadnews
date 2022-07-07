package com.heima.wemedia.interceptor;

import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @description: 拦截器
* @ClassName WmTokenInterceptor
* @author Zle
* @date 2022-06-25 19:46
* @version 1.0
*/
@Slf4j
public class WmTokenInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //得到header中的信息
        String userId = request.getHeader("userId");

        //使用ThreadLocal保存登录用户信息
        if (userId != null) {
            //存入当前线程中
            WmUser wmUser = new WmUser();
            wmUser.setId(Integer.valueOf(userId));
            WmThreadLocalUtils.setUser(wmUser);//保存到当前线程：每一个请求都是一个线程
            log.info("wmTokenFilter设置用户信息到threadlocal中...");
        }
        return true;   //必须为true，否则拦截器不生效
    }

    /**
    * @description: 清理线程中的数据
     * 如果后续的Controller中方法执行出现异常，postHandle方法不再执行,因此将清理数据写在afterCompletion中
    * @author Zle
    * @date 2022-06-25  19:50
    * @version 1.0
    */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WmThreadLocalUtils.clear();
    }
}
