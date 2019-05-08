package com.github.liweijie.wechatgroupavatar.callback;

import com.github.liweijie.wechatgroupavatar.domain.GroupAvatar;

/**
 * Project Name: WeChatGroupAvatar
 *
 * @author vj
 * @date : 2019-05-08 11:01
 * email:liweijieok@qq.com
 * desc:
 * lastModify:
 */
public interface OnWeChatGroupLoaded {
    /**
     * 加载完成
     *
     * @param avatar
     */
    void onLoaded(GroupAvatar avatar);

    /**
     * onError
     */
    void onError();
}
