package com.github.liweijie.wechatgroupavatar.domain;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Project Name: WeChatGroupAvatarHelper
 *
 * @author vj
 * @date : 2019-05-08 10:30
 * email:liweijieok@qq.com
 * desc:
 * lastModify:
 */
public class GroupAvatar implements Serializable {
    /**
     * target
     */
    private Bitmap bitmap;
    /**
     * 构成bitmap的有效的连接
     */
    private List<String> effectUrls;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public GroupAvatar() {
    }

    public GroupAvatar(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public GroupAvatar(Bitmap bitmap, List<String> effectUrls) {
        this.bitmap = bitmap;
        this.effectUrls = effectUrls;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public List<String> getEffectUrls() {
        return effectUrls;
    }

    public void setEffectUrls(List<String> effectUrls) {
        this.effectUrls = effectUrls;
    }
}
