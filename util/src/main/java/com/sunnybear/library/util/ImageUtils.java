package com.sunnybear.library.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import net.bither.util.NativeUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片处理工具
 * Created by guchenkai on 2015/12/8.
 */
public final class ImageUtils {
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 0;//相机拍照

    /**
     * 启动相机(返回值会为空,使用数据库中转)
     *
     * @param context   activity
     * @param savePath  相片保存路径文件夹
     * @param photoName 相片名
     */
    public static File startCamera(Activity context, String savePath, String photoName) {
        File file = null;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (FileUtils.createMkdirs(savePath)) {
            file = new File(savePath, photoName);
            Uri mOutputFileUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
            context.startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
        } else {
            ToastUtils.showToastShort(context, "创建相片保存路径报错");
        }
        return file;
    }

    /**
     * 根据uri获取图片路径
     *
     * @param context    context
     * @param contentUri 图片Uri路径
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String result = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            result = cursor.getString(columnIndex);
        }
        cursor.close();
        return result;
    }

    /**
     * 将小图片x轴循环填充进imageView中
     *
     * @param imageView imageView
     * @param bitmap    目标图片
     */
    public static void fillXInImageView(Context context, ImageView imageView, Bitmap bitmap) {
        int screenWidth = DensityUtil.getScreenW(context);//屏幕宽度
        imageView.setImageBitmap(createRepeater(screenWidth, bitmap));
    }


    /**
     * 将图片在imageView中x轴循环填充需要的bitmap
     *
     * @param width 填充宽度
     * @param src   图片源
     * @return 新图片源
     */
    private static Bitmap createRepeater(int width, Bitmap src) {
        int count = (width + src.getWidth() - 1) / src.getWidth(); //计算出平铺填满所给width（宽度）最少需要的重复次数
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth() * count, src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int idx = 0; idx < count; ++idx) {
            canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
        }
        return bitmap;
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     *
     * @param filePath 图片路径
     * @param width    压缩后的宽
     * @param height   压缩后的高
     */
    public static Bitmap getSmallBitmap(String filePath, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 将bitmap输出到sdCard中
     *
     * @param outPath 输出路径
     */
    public static void bitmapOutSdCard(Bitmap bitmap, String outPath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 截取滚动屏画面保存到图片
     *
     * @param view     view源
     * @param savePath 保存路径
     */
    public static void cutOutScrollViewToImage(ScrollView view, String savePath, OnImageSaveCallback callback) {
        int mSrollViewHeight = 0;
        for (int i = 0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);
            if (child instanceof LinearLayout || child instanceof RelativeLayout) {
                child.setBackgroundResource(R.color.white);
                mSrollViewHeight += child.getHeight();
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), mSrollViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        File file = new File(savePath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Logger.d(isSuccess ? "保存图片成功" : "保存图片成功");
            if (callback != null) callback.onImageSave(isSuccess);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 截取滚动屏画面保存到图片
     *
     * @param view     view源
     * @param savePath 保存路径
     */
    public static void cutOutScrollViewToImage(ScrollView view, String savePath) {
        cutOutScrollViewToImage(view, savePath, null);
    }

    /**
     * 截取画面保存到图片
     *
     * @param view     view源
     * @param savePath 保存路径
     */
    public static void cutOutViewToImage(View view, String savePath, OnImageSaveCallback callback) {
        view.setBackgroundResource(R.color.white);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        File file = new File(savePath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Logger.d(isSuccess ? "保存图片成功" : "保存图片成功");
            if (callback != null) callback.onImageSave(isSuccess);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 截取图片保存成功的回调
     */
    public interface OnImageSaveCallback {

        void onImageSave(boolean isSuccess);
    }

    /**
     * 截取画面保存到图片
     *
     * @param view     view源
     * @param savePath 保存路径
     */
    public static void cutOutViewToImage(View view, String savePath) {
        cutOutViewToImage(view, savePath, null);
    }

    /**
     * 截取view画面保存至bitmap
     *
     * @param view view源
     */
    public static Bitmap cutOutViewToBitmap(View view) {
        return Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable drawable
     * @return bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    /**
     * file转bitmap
     *
     * @param file 图片文件
     * @return bitmap
     */
    public static Bitmap fileToBitmap(File file) {
        return BitmapFactory.decodeFile(file.getPath());
    }

    /**
     * file转bitmap
     *
     * @param filePath 图片文件路径
     * @return bitmap
     */
    public static Bitmap fileToBitmap(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null)
            returnBm = bm;
        if (bm != returnBm)
            bm.recycle();
        return returnBm;
    }

    /**
     * 获得压缩图片路径
     *
     * @param source 源图片
     */
    public static String getCompressPhotoPath(File source) {
        String fileName = source.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        int degree = 0;
        int orientation = getOrientation(source.getAbsolutePath());
                        /*设置手机照片的角度*/
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = -90;
                break;
        }
        NativeUtil.compressBitmap(ImageUtils.rotateBitmapByDegree(bitmap, degree), 10, fileName, false);
        return fileName;
    }

    /**
     * 获得相片拍摄的GPS坐标信息
     *
     * @param photoPath 相片文件路径
     */
    public static LatLng getPhotoLocation(String photoPath) {
        LatLng latLng = new LatLng();
        try {
            ExifInterface exif = new ExifInterface(photoPath);
            float lat = convertRationalLatLonToFloat(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
                    , exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
            latLng.setLat(lat);
            float lng = convertRationalLatLonToFloat(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
                    , exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));
            latLng.setLng(lng);
            return latLng;
        } catch (IOException e) {
            Logger.e(e);
            return null;
        }
    }

    /**
     * 获得相片拍摄的时间
     *
     * @param photoPath 相片文件路径
     */
    public static String getPhotoCreateTime(String photoPath) {
        String createDate = "";
        try {
            ExifInterface exif = new ExifInterface(photoPath);
            createDate = exif.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (IOException e) {
            e.printStackTrace();
            return createDate;
        }
        return createDate;
    }

    /**
     * 获得相片拍摄的GPS坐标信息
     *
     * @param photoFile 相片文件
     */
    public static LatLng getPhotoLocation(File photoFile) {
        return getPhotoLocation(photoFile.getAbsolutePath());
    }

    /**
     * 将GPS时分秒坐标转换为标准GPS坐标
     *
     * @param rational 时分秒坐标
     * @param ref      坐标参照方向
     */
    private static float convertRationalLatLonToFloat(String rational, String ref) {
        if (StringUtils.isEmpty(rational) || StringUtils.isEmpty(ref))
            return (float) 0.0;
        String[] parts = rational.split(",");
        String[] pair = null;

        pair = parts[0].split("/");
        double degrees = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());
        pair = parts[1].split("/");
        double minutes = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());
        pair = parts[2].split("/");
        double second = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());

        double result = degrees + (minutes / 60.0) + (second / 3600.0);
        if (ref.equals("S") || ref.equals("W"))
            return (float) -result;
        return (float) result;
    }

    /**
     * 图片添加拍摄时间和经纬度的水印
     *
     * @param photoPath         原照片路径
     * @param watermarkLocation 添加水印的位置
     * @return 处理是否成功
     */
    /*public static void addWatermark(final Context context, final String photoPath, final WatermarkLocation watermarkLocation) {
        LatLng photoLocation = getPhotoLocation(photoPath);
        final String createDate = DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        *//*水印文字*//*
        final String watermarkStr = "(" + photoLocation.getLng() + " , " + photoLocation.getLat() + ") " + createDate;
        *//*图片处理过程*//*
        Disposable disposable = Flowable.just(photoPath).subscribeOn(Schedulers.computation())
                *//*压缩图片*//*
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        int index = s.lastIndexOf(".");
                        final String temporaryFile = s.substring(0, index) + "$temp";
                        Bitmap bitmap = BitmapFactory.decodeFile(s);
                        if (bitmap == null)
                            throw new NullPointerException("照片解析错误");
                        int degree = 0;
                        int orientation = getOrientation(photoPath);
                        *//*设置手机照片的角度*//*
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                degree = 90;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                degree = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                degree = -90;
                                break;
                        }
                        NativeUtil.compressBitmap(rotateBitmapByDegree(bitmap, degree), 10, temporaryFile, false);
                        bitmap.recycle();
                        return temporaryFile;
                    }
                })
                *//*添加水印*//*
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        Bitmap bitmap1 = BitmapFactory.decodeFile(s);
                        Bitmap bitmap = bitmap1.copy(Bitmap.Config.ARGB_8888, true);
                        if (bitmap == null)
                            throw new NullPointerException("照片解析错误");
                        bitmap1.recycle();
                        int w = bitmap.getWidth();
                        int h = bitmap.getHeight();
                        //水印笔画
                        Paint paint = new Paint();
                        Rect bounds = new Rect();
                        paint.setColor(Color.RED);
                        paint.setTextSize(80);
                        paint.getTextBounds(watermarkStr, 0, watermarkStr.length(), bounds);
                        //水印的宽高
                        int ww = bounds.width();
                        int wh = bounds.height();
                        float x = 0, y = 0;//水印的初始位置
                        //画布
                        Canvas canvas = new Canvas(bitmap);
                        switch (watermarkLocation) {
                            case TOP_LEFT:
                                x = wh;
                                y = wh;
                                break;
                            case TOP_RIGHT:
                                x = w - ww - wh;
                                y = wh;
                                break;
                            case BOTTOM_LEFT:
                                x = wh;
                                y = h - wh;
                                break;
                            case BOTTOM_RIGHT:
                                x = w - ww - wh;
                                y = h - wh;
                                break;
                            case CENTER:
                                x = w / 2 - ww / 2;
                                y = h / 2 - wh / 2;
                                break;
                        }
                        canvas.drawText(watermarkStr, x, y, paint);
                        saveBitmap(bitmap, s);
                        bitmap.recycle();
                        return s;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        try {
                            ExifInterface exifInterface = new ExifInterface(photoPath);
                            exifInterface.setAttribute(ExifInterface.TAG_DATETIME, createDate);
                            exifInterface.saveAttributes();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        FileUtils.delete(photoPath);
                        new File(s).renameTo(new File(photoPath));
                        Logger.d("照片添加水印压缩成功");
                        AppUtils.gc(context);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e(throwable.getMessage());
                        AppUtils.gc(context);
                    }
                });
        if (disposable.isDisposed())
            disposable.dispose();
    }*/

    /**
     * 质量压缩
     *
     * @param image 压缩Bitmap
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 50;
        while (baos.toByteArray().length / 1024 > 100 && options >= 0) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
    }

    /**
     * 质量压缩
     *
     * @param photoPath      原图路径
     * @param thumbPhotoPath 缩略图路径
     * @return 缩略图路径
     */
    public static String compressImage(String photoPath, String thumbPhotoPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        Bitmap compress = compressImage(bitmap);
        FileUtils.createFile(new File(thumbPhotoPath));
        saveBitmap(compress, thumbPhotoPath);
        return thumbPhotoPath;
    }

    /**
     * 尺寸压缩
     *
     * @param imgPath 图片路径
     * @param pixelW
     * @param pixelH
     */
    public static Bitmap ratio(String imgPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * 尺寸压缩
     *
     * @param photoPath
     * @param thumbPhotoPath
     * @param pixelW
     * @param pixelH
     */
    public static String compressImage(String photoPath, String thumbPhotoPath, float pixelW, float pixelH) {
        Bitmap bitmap = ratio(photoPath, pixelW, pixelH);
        FileUtils.createFile(new File(thumbPhotoPath));
        saveBitmap(rotateBitmapByDegree(bitmap, 90), thumbPhotoPath);
        return thumbPhotoPath;
    }

    /**
     * 获取图片方向
     *
     * @param ImgPath 图片路径
     */
    public static int getOrientation(String ImgPath) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(ImgPath);
            return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 保存图片
     *
     * @param bitmap   源图片
     * @param savePath 保存路径
     */
    public static void saveBitmap(Bitmap bitmap, String savePath) {
        File f = new File(savePath);
        if (f.exists())
            f.delete();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public enum WatermarkLocation {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER
    }

    /**
     * 图片经纬度信息
     */
    public static class LatLng {
        private float lat;
        private float lng;

        public float getLat() {
            return lat;
        }

        public void setLat(float lat) {
            this.lat = lat;
        }

        public float getLng() {
            return lng;
        }

        public void setLng(float lng) {
            this.lng = lng;
        }

        @Override
        public String toString() {
            return "LatLng{" +
                    "lat='" + lat + '\'' +
                    ", lng='" + lng + '\'' +
                    '}';
        }
    }
}
