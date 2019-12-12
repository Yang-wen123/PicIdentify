package com.example.left.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CircleDrawable extends Drawable {
    private Bitmap bitmap;
    private BitmapShader bitmapShader;
    private Paint paint;
    // 圆心
    private float cx, cy;
    // 半径
    private float radius;
    public CircleDrawable(Drawable drawable, Context context, int size) {
        size = dip2px(context, size);
        drawable = zoomDrawable(drawable, dip2px(context, size), dip2px(context, size));
        this.bitmap = drawableToBitmap(drawable);
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);
        cx = size / 2;
        cy = size / 2;
        radius = size / 2;
    }
    public CircleDrawable(Bitmap bitmap, Context context, int size) {
        size = dip2px(context, size);
        bitmap = zoomBitmap(bitmap, dip2px(context, size), dip2px(context, size));
        this.bitmap = bitmap;
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);
        cx = size / 2;
        cy = size / 2;
        radius = size / 2;
    }
    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawCircle(cx, cy, radius, paint);
    }
    /**
     * * 缩放Drawable
     * */
    private Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(null, newbmp);
    }
    private Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap oldbmp = bitmap;
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return newbmp;
    }
    /**
     *  * Drawable转Bitmap
     *  * */
    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
    /**
     *  * dp to px
     *  * */
    private int dip2px(Context context, float dipValue) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, resources.getDisplayMetrics());
    }
    public static int dip2px1(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    private int dip2px2(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }
    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}

