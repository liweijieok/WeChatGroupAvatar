package com.github.liweijie.wechatgroupavatar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.github.liweijie.wechatgroupavatar.callback.OnWeChatGroupLoaded;
import com.github.liweijie.wechatgroupavatar.callback.WeChatBitmapLoader;
import com.github.liweijie.wechatgroupavatar.domain.GroupAvatar;
import com.github.liweijie.wechatgroupavatar.domain.GroupRequestParam;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Project Name: WeChatGroupAvatarHelper
 *
 * @author vj
 * @date : 2019-05-08 10:28
 * email:liweijieok@qq.com
 * desc:
 * lastModify:
 */
@SuppressLint("ALL")
public class WeChatGroupAvatarHelper {

    private static final int ONE_COLUMN = 1;
    private static final int TWO_COLUMN = 4;

    private WeChatBitmapLoader loader;
    private WeakReference<Context> context;

    private WeChatGroupAvatarHelper() {

    }

    private static final class WeChatGroupAvatarHelperHolder {
        private static final WeChatGroupAvatarHelper INSTANCE = new WeChatGroupAvatarHelper();
    }

    public static WeChatGroupAvatarHelper getInstance() {
        return WeChatGroupAvatarHelperHolder.INSTANCE;
    }

    public void config(Context context, WeChatBitmapLoader loader) {
        this.loader = loader;
        this.context = new WeakReference<>(context);
    }

    public void asyncGetGroupAvatar(List<String> urls, int size, int gap, final OnWeChatGroupLoaded loaded) {
        asyncGetGroupAvatar(urls, size, gap, Color.parseColor("#EDEDED"), null, loaded);
    }

    public void asyncGetGroupAvatar(List<String> urls, int size, int gap, int backgroundColor, int placeHolder, final OnWeChatGroupLoaded loaded) {
        Bitmap bitmap = null;
        if (context != null && context.get() != null) {
            bitmap = BitmapFactory.decodeResource(context.get().getResources(), placeHolder);
        }
        asyncGetGroupAvatar(urls, size, gap, backgroundColor, bitmap, loaded);
    }

    /**
     * 异步获取，回调在主线程
     *
     * @param urls
     * @param size
     * @param gap
     * @param backgroundColor
     * @param placeHolder
     * @param loaded
     */
    public void asyncGetGroupAvatar(List<String> urls, int size, int gap, int backgroundColor, Bitmap placeHolder, final OnWeChatGroupLoaded loaded) {
        GroupRequestParam param = new GroupRequestParam(urls, size, gap, backgroundColor, placeHolder);
        AsyncTask<GroupRequestParam, Void, GroupAvatar> task = new AsyncTask<GroupRequestParam, Void, GroupAvatar>() {
            @Override
            protected GroupAvatar doInBackground(GroupRequestParam... params) {
                if (params == null || params.length != 1) {
                    return null;
                }
                GroupRequestParam p = params[0];
                return getGroupAvatar(p.getUrls(), p.getSize(), p.getGap(), p.getBackgroundColor(), p.getPlaceHolder());
            }

            @Override
            protected void onPostExecute(GroupAvatar avatar) {
                super.onPostExecute(avatar);
                if (loaded == null) {
                    return;
                }
                if (avatar == null) {
                    loaded.onError();
                    return;
                }
                loaded.onLoaded(avatar);
            }
        };
        task.execute(param);
    }

    public GroupAvatar getGroupAvatar(List<String> urls, int size, int gap) {
        return getGroupAvatar(urls, size, gap, Color.parseColor("#EDEDED"), null);
    }

    public GroupAvatar getGroupAvatar(List<String> urls, int size, int gap, int backgroundColor) {
        return getGroupAvatar(urls, size, gap, backgroundColor, null);
    }

    public GroupAvatar getGroupAvatar(List<String> urls, int size, int gap, int backgroundColor, int placeHolder) {
        Bitmap bitmap = null;
        if (context != null && context.get() != null) {
            bitmap = BitmapFactory.decodeResource(context.get().getResources(), placeHolder);
        }
        return getGroupAvatar(urls, size, gap, backgroundColor, bitmap);
    }


    /**
     * 需要再子线程中调用
     *
     * @param urls
     * @param size
     * @param gap
     * @param backgroundColor
     * @param placeHolder
     * @return
     */
    public GroupAvatar getGroupAvatar(List<String> urls, int size, int gap, int backgroundColor, Bitmap placeHolder) {
        if (urls == null || urls.isEmpty()) {
            return new GroupAvatar(placeHolder);
        }
        if (loader == null) {
            Log.e("getGroupAvatar>>>", "Please setup WeChatBitmapLoader before generate group avatar，call the config()");
            return new GroupAvatar(placeHolder);
        }
        int length = urls.size();
        //计算单个大小
        int itemSize;
        if (length == ONE_COLUMN) {
            itemSize = size - 2 * gap;
        } else if (length <= TWO_COLUMN) {
            itemSize = (size - 3 * gap) / 2;
        } else {
            itemSize = (size - 4 * gap) / 3;
        }

        List<Bitmap> bitmaps = new ArrayList<>();
        List<String> effectUrls = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            String path = urls.get(i);
            Bitmap bitmap = null;
            if (TextUtils.isEmpty(path)) {
                if (placeHolder != null) {
                    bitmaps.add(placeHolder);
                }
                continue;
            }
            try {
                bitmap = loader.loadBitmap(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                //有效的url
                effectUrls.add(path);
            }
            //使用默认头像
            if (bitmap == null) {
                bitmap = placeHolder;
            }
            if (bitmap != null) {
                bitmaps.add(bitmap);
            }
        }

        scaleBitmap(bitmaps, itemSize);
        Bitmap target = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawColor(backgroundColor);
        drawAvatars(size, gap, length, itemSize, bitmaps, canvas);
        recycleBitmap(bitmaps);
        return new GroupAvatar(target, effectUrls);
    }

    /**
     * drawAvatar
     *
     * @param size
     * @param gap
     * @param length
     * @param itemSize
     * @param bitmaps
     * @param canvas
     */
    private void drawAvatars(int size, int gap, int length, int itemSize, List<Bitmap> bitmaps, Canvas canvas) {
        if (length == 1) {
            Bitmap first = bitmaps.get(0);
            canvas.drawBitmap(first, gap, gap, null);
        } else if (length == 2) {
            Bitmap first = bitmaps.get(0);
            Bitmap second = bitmaps.get(1);
            int top = (size - itemSize) / 2;
            canvas.drawBitmap(first, gap, top, null);
            canvas.drawBitmap(second, 2 * gap + itemSize, top, null);
        } else if (length == 3) {
            Bitmap first = bitmaps.get(0);
            Bitmap second = bitmaps.get(1);
            Bitmap third = bitmaps.get(2);
            int left = (size - itemSize) / 2;
            canvas.drawBitmap(first, left, gap, null);
            canvas.drawBitmap(second, gap, 2 * gap + itemSize, null);
            canvas.drawBitmap(third, 2 * gap + itemSize, 2 * gap + itemSize, null);
        } else if (length == 4) {
            Bitmap first = bitmaps.get(0);
            Bitmap second = bitmaps.get(1);
            Bitmap third = bitmaps.get(2);
            Bitmap four = bitmaps.get(3);
            canvas.drawBitmap(first, gap, gap, null);
            canvas.drawBitmap(second, 2 * gap + itemSize, gap, null);
            canvas.drawBitmap(third, gap, 2 * gap + itemSize, null);
            canvas.drawBitmap(four, gap * 2 + itemSize, 2 * gap + itemSize, null);
        } else if (length == 5) {
            Bitmap first = bitmaps.get(0);
            Bitmap second = bitmaps.get(1);
            Bitmap third = bitmaps.get(2);
            Bitmap four = bitmaps.get(3);
            Bitmap five = bitmaps.get(4);
            int left = (itemSize + gap) / 2;
            int top = (itemSize + gap) / 2;
            canvas.drawBitmap(first, left + gap, gap + top, null);
            canvas.drawBitmap(second, left + 2 * gap + itemSize, gap + top, null);
            canvas.drawBitmap(third, gap, gap * 2 + itemSize + top, null);
            canvas.drawBitmap(four, 2 * gap + itemSize, gap * 2 + itemSize + top, null);
            canvas.drawBitmap(five, 3 * gap + 2 * itemSize, gap * 2 + itemSize + top, null);
        } else if (length == 6) {
            Bitmap first = bitmaps.get(0);
            Bitmap second = bitmaps.get(1);
            Bitmap third = bitmaps.get(2);
            Bitmap four = bitmaps.get(3);
            Bitmap five = bitmaps.get(4);
            Bitmap six = bitmaps.get(5);
            int top = (itemSize + gap) / 2;
            canvas.drawBitmap(first, gap, gap + top, null);
            canvas.drawBitmap(second, 2 * gap + itemSize, gap + top, null);
            canvas.drawBitmap(third, 3 * gap + itemSize * 2, gap + top, null);
            canvas.drawBitmap(four, gap, gap * 2 + itemSize + top, null);
            canvas.drawBitmap(five, gap * 2 + itemSize, gap * 2 + itemSize + top, null);
            canvas.drawBitmap(six, gap * 3 + 2 * itemSize, gap * 2 + itemSize + top, null);
        } else if (length == 7) {
            Bitmap first = bitmaps.get(0);
            Bitmap second = bitmaps.get(1);
            Bitmap third = bitmaps.get(2);
            Bitmap four = bitmaps.get(3);
            Bitmap five = bitmaps.get(4);
            Bitmap six = bitmaps.get(5);
            Bitmap seven = bitmaps.get(6);
            int left = (size - itemSize) / 2;
            canvas.drawBitmap(first, left, gap, null);
            canvas.drawBitmap(second, gap, gap * 2 + itemSize, null);
            canvas.drawBitmap(third, gap * 2 + itemSize, gap * 2 + itemSize, null);
            canvas.drawBitmap(four, gap * 3 + itemSize * 2, gap * 2 + itemSize, null);
            canvas.drawBitmap(five, gap, 3 * gap + 2 * itemSize, null);
            canvas.drawBitmap(six, gap * 2 + itemSize, 3 * gap + 2 * itemSize, null);
            canvas.drawBitmap(seven, gap * 3 + itemSize * 2, 3 * gap + 2 * itemSize, null);
        } else if (length == 8) {
            Bitmap first = bitmaps.get(0);
            Bitmap second = bitmaps.get(1);
            Bitmap third = bitmaps.get(2);
            Bitmap four = bitmaps.get(3);
            Bitmap five = bitmaps.get(4);
            Bitmap six = bitmaps.get(5);
            Bitmap seven = bitmaps.get(6);
            Bitmap eight = bitmaps.get(7);
            int left = (itemSize + gap) / 2;
            canvas.drawBitmap(first, left + gap, gap, null);
            canvas.drawBitmap(second, left + itemSize + 2 * gap, gap, null);
            canvas.drawBitmap(third, gap, gap * 2 + itemSize, null);
            canvas.drawBitmap(four, gap * 2 + itemSize, gap * 2 + itemSize, null);
            canvas.drawBitmap(five, gap * 3 + itemSize * 2, gap * 2 + itemSize, null);
            canvas.drawBitmap(six, gap, 3 * gap + 2 * itemSize, null);
            canvas.drawBitmap(seven, gap * 2 + itemSize, 3 * gap + 2 * itemSize, null);
            canvas.drawBitmap(eight, gap * 3 + itemSize * 2, 3 * gap + 2 * itemSize, null);
        } else if (length == 9) {
            Bitmap first = bitmaps.get(0);
            Bitmap second = bitmaps.get(1);
            Bitmap third = bitmaps.get(2);
            Bitmap four = bitmaps.get(3);
            Bitmap five = bitmaps.get(4);
            Bitmap six = bitmaps.get(5);
            Bitmap seven = bitmaps.get(6);
            Bitmap eight = bitmaps.get(7);
            Bitmap nine = bitmaps.get(8);
            canvas.drawBitmap(first, gap, gap, null);
            canvas.drawBitmap(second, 2 * gap + itemSize, gap, null);
            canvas.drawBitmap(third, 3 * gap + itemSize * 2, gap, null);
            canvas.drawBitmap(four, gap, gap * 2 + itemSize, null);
            canvas.drawBitmap(five, gap * 2 + itemSize, gap * 2 + itemSize, null);
            canvas.drawBitmap(six, gap * 3 + itemSize * 2, gap * 2 + itemSize, null);
            canvas.drawBitmap(seven, gap, 3 * gap + 2 * itemSize, null);
            canvas.drawBitmap(eight, gap * 2 + itemSize, 3 * gap + 2 * itemSize, null);
            canvas.drawBitmap(nine, gap * 3 + itemSize * 2, 3 * gap + 2 * itemSize, null);
        }
    }

    /**
     * 缩放到至大小
     *
     * @param bitmaps    列表
     * @param targetSize 目标大小
     */
    private static void scaleBitmap(List<Bitmap> bitmaps, int targetSize) {
        int size = bitmaps.size();
        for (int i = 0; i < size; i++) {
            Bitmap item = bitmaps.get(i);
            int width = item.getWidth();
            int height = item.getHeight();
            //缩放倍数
            float ratio;
            //获取的原图片的大小，需要获取正方形
            int originSie;
            int offsetX = 0, offsetY = 0;
            if (width <= height) {
                originSie = width;
                offsetY = (height - width) / 2;
                ratio = 1.0f * targetSize / width;
            } else {
                originSie = height;
                offsetX = (width - height) / 2;
                ratio = 1.0f * targetSize / height;
            }
            Matrix matrix = new Matrix();
            matrix.preScale(ratio, ratio);
            Bitmap newBitmap = Bitmap.createBitmap(item, offsetX, offsetY, originSie, originSie, matrix, false);
            bitmaps.set(i, newBitmap);
        }
    }

    private static void recycleBitmap(List<Bitmap> bitmaps) {
        if (bitmaps != null && !bitmaps.isEmpty()) {
            for (int i = 0; i < bitmaps.size(); i++) {
                if (bitmaps.get(i) != null && !bitmaps.get(i).isRecycled()) {
                    bitmaps.get(i).recycle();
                }
            }
        }
    }
}
