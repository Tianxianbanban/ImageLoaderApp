package cy.review.imageloaderapp.loader;

import java.io.FileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageResizer {
    private static final String TAG = "ImageResizer";

    public ImageResizer() {

    }


    /**
     * 如何获取采样率?四个步骤加载出的图片就是最终缩放后的图片
     * @param res 图片资源
     * @param resId 资源id
     * @param reqWidth View的宽
     * @param reqHeight View的宽
     * @return 缩放后的图片
     */
    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        //BitmapFactory.Options中的inJustDecodeBounds参数设置为true并且加载图片
        //设置为ture的时候，BitmapFactory只会解析图片的原始宽高信息，并不会真正地加载图片，是一个轻量级的操作
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //从BitmapFactory.Options中取出图片的原始宽高信息，分别对应outWidth和outHeight参数
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        //根据采样率的规则并且结合目标View的所需大小计算出采样率inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        //将BitmapFactory.Options的inJustDecodeBounds参数设置为false，然后重新加载图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    /**
     *
     * @param fd 文件描述符
     * @param reqWidth View的宽
     * @param reqHeight View的高
     * @return 缩放后的图片
     */
    public Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    /**
     * 根据采样率的规则并且结合目标View的所需大小计算出采样率inSampleSize
     * @param options 使用BitmapFactory.Options按照一定的采样率来加载缩小后的图片。
     * @param reqWidth View的宽
     * @param reqHeight View的高
     * @return 合适的采样率
     */
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(TAG, "origin, w= " + width + " h=" + height);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        Log.d(TAG, "sampleSize:" + inSampleSize);
        return inSampleSize;
    }
}
