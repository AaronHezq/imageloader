# imageloader
图片加载框架

使用前先在主Activity中初始化
ImageLoader.getInstance(context).build();

调用 第一个参数ImageView,第二个参数图片网络地址，第三个参数默认图片id
ImageLoader.getInstance(context).display(ImageView, imageUrl);
ImageLoader.getInstance(context).display(ImageView, imageUrl,R.drawable.default_image_id);

需要权限
android.permission.INTERNET
android.permission.WRITE_EXTERNAL_STORAGE

