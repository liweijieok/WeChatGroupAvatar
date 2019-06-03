package com.github.liweijie.wechatgroupavatar.demo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.liweijie.wechatgroupavatar.WeChatGroupAvatarHelper;
import com.github.liweijie.wechatgroupavatar.callback.OnWeChatGroupLoaded;
import com.github.liweijie.wechatgroupavatar.callback.WeChatBitmapLoader;
import com.github.liweijie.wechatgroupavatar.domain.GroupAvatar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author liweijie
 */
public class MainActivity extends AppCompatActivity {
    private ImageView iv1, iv2, iv3, iv4, iv5, iv6, iv7, iv8, iv9,iv10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv1 = findViewById(R.id.avatar1);
        iv2 = findViewById(R.id.avatar2);
        iv3 = findViewById(R.id.avatar3);
        iv4 = findViewById(R.id.avatar4);
        iv5 = findViewById(R.id.avatar5);
        iv6 = findViewById(R.id.avatar6);
        iv7 = findViewById(R.id.avatar7);
        iv8 = findViewById(R.id.avatar8);
        iv9 = findViewById(R.id.avatar9);
        iv10 = findViewById(R.id.avatar10);
        WeChatGroupAvatarHelper.getInstance().config(this, new WeChatBitmapLoader() {
            @Override
            public Bitmap loadBitmap(String url) {
                try {
                    //这里只是加载了100*100的头像，可能会比较模糊
                    return Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(url)
                            .submit()
                            .get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        loadImage();
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });
    }

    private void loadImage() {
        final List<String> urls = new ArrayList<>();
        final int size = dp2px(100);
        final int gap = dp2px(4);
        urls.add("https://ss1.baidu.com/-4o3dSag_xI4khGko9WTAnF6hhy/image/h%3D300/sign=a9e671b9a551f3dedcb2bf64a4eff0ec/4610b912c8fcc3cef70d70409845d688d53f20f7.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1557295797809&di=23dffb083e8aadfaa903ebb9ae2a92a2&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201605%2F19%2F20160519095412_tcCZw.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1557295797809&di=bbd31770b51e159feea9230e117a9b88&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F0ca311627999e23502462147a63f723574a2897e13529-2bAh96_fw658");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1557295797809&di=ee5b44c6834a45800f021afa997c7d2c&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201509%2F02%2F20150902204354_Kn83E.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1557295797804&di=fd137d633c9225f9392fdecfda79701e&imgtype=0&src=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201510%2F24%2F20151024183653_PEudR.thumb.700_0.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1557295861685&di=dcf3c308298bb68803cdc279377ad9b6&imgtype=0&src=http%3A%2F%2Fimg17.3lian.com%2Fd%2Ffile%2F201702%2F23%2F6e12cb5e536cbaaa63d4f841f8bcb1dc.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1557295861685&di=d43e0b7216bd3e6c3ca1150280ff86ae&imgtype=0&src=http%3A%2F%2Fpic.rmb.bdstatic.com%2Ff54083119edfb83c4cfe9ce2eeebc076.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1557295861684&di=65ca3b4f8c5417779ccf03be0e07acfd&imgtype=0&src=http%3A%2F%2Fimg.ph.126.net%2FtUmAKo2mJ5kbaxbvxdVmbA%3D%3D%2F2507379092555461040.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559586457253&di=4869eccea2be98be73973356b9df5712&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201509%2F10%2F20150910124631_fTrPX.jpeg");
        final int placeHolder = R.mipmap.ic_launcher;
        final int backgroundColor = Color.parseColor("#EDEDED");
        WeChatGroupAvatarHelper.getInstance().asyncGetGroupAvatar(urls.subList(0, 1), size, gap, backgroundColor, placeHolder, new OnWeChatGroupLoaded() {
            @Override
            public void onLoaded(GroupAvatar avatar) {
                iv1.setImageBitmap(avatar.getBitmap());
            }

            @Override
            public void onError() {

            }
        });
        WeChatGroupAvatarHelper.getInstance().asyncGetGroupAvatar(urls.subList(0, 2), size, gap, backgroundColor, placeHolder, new OnWeChatGroupLoaded() {
            @Override
            public void onLoaded(GroupAvatar avatar) {
                iv2.setImageBitmap(avatar.getBitmap());
            }

            @Override
            public void onError() {

            }
        });
        WeChatGroupAvatarHelper.getInstance().asyncGetGroupAvatar(urls.subList(0, 3), size, gap, backgroundColor, placeHolder, new OnWeChatGroupLoaded() {
            @Override
            public void onLoaded(GroupAvatar avatar) {
                iv3.setImageBitmap(avatar.getBitmap());
            }

            @Override
            public void onError() {

            }
        });

        //下面是同步获取的例子
        ThreadExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final GroupAvatar avatar = WeChatGroupAvatarHelper.getInstance().getGroupAvatar(urls.subList(0, 4), size, gap, backgroundColor, placeHolder);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv4.setImageBitmap(avatar.getBitmap());
                    }
                });
            }
        });

        ThreadExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final GroupAvatar avatar = WeChatGroupAvatarHelper.getInstance().getGroupAvatar(urls.subList(0, 5), size, gap, backgroundColor, placeHolder);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv5.setImageBitmap(avatar.getBitmap());
                    }
                });
            }
        });
        ThreadExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final GroupAvatar avatar = WeChatGroupAvatarHelper.getInstance().getGroupAvatar(urls.subList(0, 6), size, gap, backgroundColor, placeHolder);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv6.setImageBitmap(avatar.getBitmap());
                    }
                });
            }
        });
        ThreadExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final GroupAvatar avatar = WeChatGroupAvatarHelper.getInstance().getGroupAvatar(urls.subList(0, 7), size, gap, backgroundColor, placeHolder);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv7.setImageBitmap(avatar.getBitmap());
                    }
                });
            }
        });
        ThreadExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final GroupAvatar avatar = WeChatGroupAvatarHelper.getInstance().getGroupAvatar(urls.subList(0, 8), size, gap, backgroundColor, placeHolder);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv8.setImageBitmap(avatar.getBitmap());
                    }
                });
            }
        });

        ThreadExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final GroupAvatar avatar = WeChatGroupAvatarHelper.getInstance().getGroupAvatar(urls.subList(0, 9), size, gap, backgroundColor,placeHolder);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv9.setImageBitmap(avatar.getBitmap());
                    }
                });
            }
        });
        //这里的最后一个没有placeholder，而且第九张图为空，所以最终结果是八张图
        final List<String> testEmptyPlaceHolder = new ArrayList<>();
        testEmptyPlaceHolder.addAll(urls.subList(0, 8));
        testEmptyPlaceHolder.add("");
        ThreadExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final GroupAvatar avatar = WeChatGroupAvatarHelper.getInstance().getGroupAvatar(testEmptyPlaceHolder.subList(0, 9), size, gap, backgroundColor);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv10.setImageBitmap(avatar.getBitmap());
                    }
                });
            }
        });
    }


    public int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
