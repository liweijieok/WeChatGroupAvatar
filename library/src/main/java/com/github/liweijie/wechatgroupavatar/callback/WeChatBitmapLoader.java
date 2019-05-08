package com.github.liweijie.wechatgroupavatar.callback;

import android.graphics.Bitmap;

/**
 * Project Name: GroupAvatar
 *
 * @author vj
 * @date : 2019-05-08 10:38
 * email:liweijieok@qq.com
 * desc:
 * lastModify:
 */
public interface WeChatBitmapLoader {
    /**
     * loadBitmap
     *
     * @param url
     * @return
     */
    Bitmap loadBitmap(String url);
}
