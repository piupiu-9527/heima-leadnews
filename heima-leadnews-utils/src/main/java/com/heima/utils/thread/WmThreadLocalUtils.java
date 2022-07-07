package com.heima.utils.thread;

import com.heima.model.wemedia.pojos.WmUser;

/**
* @description:  ThreadLocal工具类
* @ClassName WmThreadLocalUtils
* @author Zle
* @date 2022-06-25 19:33
* @version 1.0
*/
public class WmThreadLocalUtils {
    public static final ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<WmUser>();

    /**
    * @description: 添加用户
    * @param: [wmUser]
    * @return: void
    * @author Zle
    * @date: 2022-06-25 19:44
    */
    public static void setUser(WmUser wmUser){
        WM_USER_THREAD_LOCAL.set(wmUser);
    }

    /**
    * @description: 获取用户
    * @param: []
    * @return: com.heima.model.wemedia.pojos.WmUser
    * @author Zle
    * @date: 2022-06-25 19:44
    */
    public static WmUser getUser(){
        return WM_USER_THREAD_LOCAL.get();
    }
    
    /**
    * @description: 清理用户
    * @param: []
    * @return: void
    * @author Zle
    * @date: 2022-06-25 20:25
    */
    public static void clear(){
        WM_USER_THREAD_LOCAL.remove();
    }
}
