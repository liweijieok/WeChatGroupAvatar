# WeChatGroupAvatar
[ ![Download](https://api.bintray.com/packages/liweijieok/maven/WeChatGroupAvatar/images/download.svg?version=0.0.1) ](https://bintray.com/liweijieok/maven/WeChatGroupAvatar/0.0.1/link)

这是仿微信群主头像的，使用的方案是合成bitmap显示，而非九宫格里面包含九张图片，就是先把对应的群头像照片先合成为一张之后在显示。他的好处是：在显示群头像的时候，不会在一个头像中有多次加载图片，同时在刷新页面的时候，掉帧不会那么厉害。
本库同时提供给外部自己确定如何加载bitmap的方案，内部没有实现，所以群头像的组成可以是本地文件，网络文件或者是drawable等可以转为bitmap的均可。


## 使用

```
implementation 'com.github.bumptech.glide:glide:4.9.0'
```

在`WeChatGroupAvatarHelper`中提供了同步获取和异步获取的方法，在使用之前，需要先调用config()方法置顶加载bitmap的方式，比如使用glide

```

WeChatGroupAvatarHelper.getInstance().config(this, new WeChatBitmapLoader() {
            @Override
            public Bitmap loadBitmap(String url) {
                try {
                    return Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(url)
                            .submit(100, 100)
                            .get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        
```

加载方式

```

//同步加载
 public GroupAvatar getGroupAvatar(List<String> urls, int size, int gap, int backgroundColor, Bitmap placeHolder)
//异步加载
asyncGetGroupAvatar(List<String> urls, int size, int gap, int backgroundColor, Bitmap placeHolder, final OnWeChatGroupLoaded loaded)

参数说明：
GroupAvatar：合成之后的信息返回，包括了合成之后的bitmap以及有效参数该bitmap合成的url地址类别(就是成功加载出bitmap的，urls中假如是为空或者是图片加载失败的不会添加进入返回的effectUrls里面)。
urls：群头像地址
size：合成bitmap大小
gap：间隙宽度
backgroundColor：背景颜色
placeHolder：图片加载失败或者是url为空时候的默认图
OnWeChatGroupLoaded：异步加载的回调

```



## 效果：

![](https://github.com/liweijieok/WeChatGroupAvatar/blob/master/art/device-2019-05-08-113946.png)


![](https://github.com/liweijieok/WeChatGroupAvatar/blob/master/art/device-2019-05-08-114009.png)


## 题外话

未完待续
..
