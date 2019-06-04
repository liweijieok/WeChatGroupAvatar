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
    private Bitmap defaultBitmap;

    private WeChatGroupAvatarHelper() {

    }

    private static final class WeChatGroupAvatarHelperHolder {
        private static final WeChatGroupAvatarHelper INSTANCE = new WeChatGroupAvatarHelper();
    }

    public static WeChatGroupAvatarHelper getInstance() {
        return WeChatGroupAvatarHelperHolder.INSTANCE;
    }

    public void config(Context context, WeChatBitmapLoader loader) {
        this.config(context, loader, null);
    }

    public void config(Context context, WeChatBitmapLoader loader, Bitmap defaultBitmap) {
        this.loader = loader;
        this.context = new WeakReference<>(context);
        this.defaultBitmap = defaultBitmap;
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
            return new GroupAvatar(defaultBitmap);
        }
        if (loader == null) {
            Log.e("getGroupAvatar>>>", "Please setup WeChatBitmapLoader before generate group avatar，call the config()");
            return new GroupAvatar(placeHolder);
        }
        int length = urls.size();
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
        length = bitmaps.size();
        if (length == 0) {
            return new GroupAvatar(defaultBitmap);
        }
        //计算单个大小
        int itemSize;
        if (length == ONE_COLUMN) {
            itemSize = size / 2;
        } else if (length <= TWO_COLUMN) {
            itemSize = (size - 3 * gap) / 2;
        } else {
            itemSize = (size - 4 * gap) / 3;
        }
        scaleBitmap(bitmaps, itemSize);
        Bitmap target = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawColor(backgroundColor);
        drawAvatars(size, gap, itemSize, bitmaps, canvas);
        recycleBitmap(bitmaps);
        return new GroupAvatar(target, effectUrls);
    }

    /**
     * drawAvatar
     *
     * @param size
     * @param gap
     * @param itemSize
     * @param bitmaps
     * @param canvas
     */
    private void drawAvatars(int size, int gap, int itemSize, List<Bitmap> bitmaps, Canvas canvas) {
        int length = bitmaps.size();
        int initTop = gap, firstLeft = gap;
        if (length == 1) {
            initTop = size >> 2;
        } else if (length == 2) {
            initTop = (size - itemSize) / 2;
        } else if (length >= 5 && length <= 6) {
            initTop = (itemSize + gap) / 2 + gap;
        }
        if (length == 1) {
            firstLeft = size >> 2;
        } else if (length == 3 || length == 7) {
            firstLeft = (size - itemSize) / 2;
        } else if (length == 5 || length == 8) {
            firstLeft = (itemSize + gap) / 2;
        }

        for (int i = 0; i < length; i++) {
            Bitmap item = bitmaps.get(i);
            int top, left;
            if (length <= 2) {
                top = initTop;
            } else if (length <= 6) {
                if ((length == 3 && i < 1)
                        || ((length == 4 || length == 5) && i < 2)
                        || (length == 6 && i < 3)) {
                    top = initTop;
                } else {
                    top = initTop + gap + itemSize;
                }
            } else {
                if ((length == 7 && i < 1) || (length == 8 && i < 2) || (length == 9 && i < 3)) {
                    top = initTop;
                } else if ((length == 7 && i < 4) || (length == 8 && i < 5) || (length == 9 && i < 6)) {
                    top = gap + itemSize + initTop;
                } else {
                    top = 2 * gap + 2 * itemSize + initTop;
                }
            }

            if ((length == 1 || length == 3 || length == 7 || length == 5 || length == 8) && i == 0) {
                //特殊位置第一列
                left = firstLeft;
            } else if ((length == 5 || length == 8) && i == 1) {
                //特殊位置第二列
                left = firstLeft + gap + itemSize;
            } else if ((length == 1)
                    //正常位置第一列
                    || (length == 2 && i < 1)
                    || (length == 3 && i == 1)
                    || (length == 4 && (i % 2 == 0))
                    || (length == 5 && i == 2)
                    || ((length == 6 || length == 9) && (i % 3 == 0))
                    || (length == 7 && (i - 1) % 3 == 0)
                    || (length == 8 && (i - 2) % 3 == 0)) {
                left = gap;
            } else if ((length <= 4)
                    || (length == 5 && i == 3)
                    || ((length == 6 || length == 9) && (i - 1) % 3 == 0)
                    || (length == 7 && (i - 2) % 3 == 0)
                    || (length == 8 && i % 3 == 0)) {
                //正常位置第二列
                left = 2 * gap + itemSize;
            } else {
                //正常位置第三列
                left = 3 * gap + 2 * itemSize;
            }

            canvas.drawBitmap(item, left, top, null);
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
