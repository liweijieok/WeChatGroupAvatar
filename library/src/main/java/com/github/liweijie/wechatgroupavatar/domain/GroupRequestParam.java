package com.github.liweijie.wechatgroupavatar.domain;

import android.graphics.Bitmap;


import java.io.Serializable;
import java.util.List;

/**
 * Project Name: WeChatGroupAvatar
 *
 * @author vj
 * @date : 2019-05-08 11:08
 * email:liweijieok@qq.com
 * desc:
 * lastModify:
 */
public class GroupRequestParam implements Serializable {
    private List<String> urls;
    private int size;
    private int gap;
    private int backgroundColor;
    private Bitmap placeHolder;

    public GroupRequestParam() {
    }

    public GroupRequestParam(List<String> urls, int size, int gap, int backgroundColor, Bitmap placeHolder) {
        this.urls = urls;
        this.size = size;
        this.gap = gap;
        this.backgroundColor = backgroundColor;
        this.placeHolder = placeHolder;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Bitmap getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(Bitmap placeHolder) {
        this.placeHolder = placeHolder;
    }

}
