package com.jtv7.gendertogglelib;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;

import java.util.Arrays;
import java.util.Stack;


/**
 * Created by jtv7 on Jan 29, 2018.
 *
 * @author jtv7
 */
public class GenderToggleUtil {
    private static class GlobalCache {
        static PorterDuffXfermode blendModeSourceIn = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        static PorterDuffXfermode blendModeDestinationOut = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }

    // Resizing Behavior
    public enum ResizingBehavior {
        AspectFit, //!< The content is proportionally resized to fit into the target rectangle.
        AspectFill, //!< The content is proportionally resized to completely fill the target rectangle.
        Stretch, //!< The content is stretched to match the entire target rectangle.
        Center, //!< The content is centered in the target rectangle, but it is NOT resized.
    }

    // Canvas Drawings
    // Tab

    private static class CacheForCanvas1 {
        private static Paint paint = new Paint();
        private static Paint shadowPaint = new Paint();
        private static Shadow switchShadow = new Shadow();
        private static Shadow knobBlur = new Shadow();
        private static RectF originalFrame = new RectF(0f, 0f, 250f, 125f);
        private static RectF resizedFrame = new RectF();
        private static RectF rectangleRect = new RectF();
        private static Path rectanglePath = new Path();
        private static ToggleLinearGradient rectanglePathGradient = new ToggleLinearGradient();
        private static RectF ovalRect = new RectF();
        private static Path ovalPath = new Path();
        private static RectF bezierRect = new RectF();
        private static Path bezierPath = new Path();
    }

    public static void drawToggle(Canvas canvas, RectF targetFrame, ResizingBehavior resizing, int color, float position, float rotation, float shadowIntensity, float gradientIntensity, boolean swap, boolean glowEnabled) {
        // General Declarations
        Stack<Matrix> currentTransformation = new Stack<Matrix>();
        currentTransformation.push(new Matrix());
        Paint paint = CacheForCanvas1.paint;

        // Local Colors
        int derivedColor = colorByApplyingShadow(color, 0.7f);

        // Local Gradients
        ToggleGradient gradient = new ToggleGradient(new int[]{derivedColor, colorByBlendingColors(derivedColor, 0.5f, color), color}, new float[]{0f, 0.5f - gradientIntensity, 1f});

        // Local Shadows
        Shadow switchShadow = CacheForCanvas1.switchShadow.get(Color.BLACK, shadowIntensity, 0f, 4f);
        Shadow knobBlur = CacheForCanvas1.knobBlur.get(color, 0f, 0f, 10f);

        // Resize to Target Frame
        canvas.save();
        currentTransformation.push(new Matrix(currentTransformation.peek()));
        RectF resizedFrame = CacheForCanvas1.resizedFrame;
        GenderToggleUtil.resizingBehaviorApply(resizing, CacheForCanvas1.originalFrame, targetFrame, resizedFrame);
        canvas.translate(resizedFrame.left, resizedFrame.top);
        canvas.scale(resizedFrame.width() / 250f, resizedFrame.height() / 125f);

        // Rectangle
        RectF rectangleRect = CacheForCanvas1.rectangleRect;
        rectangleRect.set(27f, 21f, 213f, 99f);
        Path rectanglePath = CacheForCanvas1.rectanglePath;
        rectanglePath.reset();
        rectanglePath.addRoundRect(rectangleRect, 50f, 50f, Path.Direction.CW);

        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);


        float x0 = 213f;
        float x1 = 27f;

        paint.setShader(CacheForCanvas1.rectanglePathGradient.get(gradient, swap ? x0 : x1, 60f, swap ? x1 : x0, 60f));
        canvas.drawPath(rectanglePath, paint);
        canvas.saveLayerAlpha(null, 255, Canvas.ALL_SAVE_FLAG);
        currentTransformation.push(new Matrix(currentTransformation.peek()));
        {

            paint.reset();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(switchShadow.color);
            canvas.drawPath(rectanglePath, paint);

            paint.reset();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(GlobalCache.blendModeDestinationOut);
            canvas.saveLayer(null, paint, Canvas.ALL_SAVE_FLAG);
            currentTransformation.push(new Matrix(currentTransformation.peek()));
            {
                Matrix invertedCurrentTransformation = new Matrix();
                currentTransformation.peek().invert(invertedCurrentTransformation);
                canvas.concat(invertedCurrentTransformation);
                canvas.translate(switchShadow.dx, switchShadow.dy);
                canvas.concat(currentTransformation.peek());


                paint.reset();
                paint.setFlags(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.WHITE);
                switchShadow.setBlurOfPaint(paint);
                canvas.drawPath(rectanglePath, paint);
            }
            currentTransformation.pop();
            canvas.restore();
        }
        currentTransformation.pop();
        canvas.restore();

        // Group
        {
            canvas.save();
            currentTransformation.push(new Matrix(currentTransformation.peek()));
            canvas.translate(position + 49f, 60f);
            currentTransformation.peek().postTranslate(position + 49f, 60f);
            canvas.rotate(-rotation);
            currentTransformation.peek().postRotate(-rotation);

            // Oval
            RectF ovalRect = CacheForCanvas1.ovalRect;
            ovalRect.set(-39f, -39f, 39f, 39f);
            Path ovalPath = CacheForCanvas1.ovalPath;
            ovalPath.reset();
            ovalPath.addOval(ovalRect, Path.Direction.CW);

            paint.reset();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);


            if (glowEnabled) {

                canvas.saveLayerAlpha(null, 255, Canvas.ALL_SAVE_FLAG);
                currentTransformation.push(new Matrix(currentTransformation.peek()));
                {
                    Matrix invertedCurrentTransformation = new Matrix();
                    currentTransformation.peek().invert(invertedCurrentTransformation);
                    canvas.concat(invertedCurrentTransformation);
                    canvas.translate(knobBlur.dx, knobBlur.dy);
                    canvas.concat(currentTransformation.peek());

                    Paint shadowPaint = CacheForCanvas1.shadowPaint;
                    shadowPaint.set(paint);
                    knobBlur.setBlurOfPaint(shadowPaint);
                    canvas.drawPath(ovalPath, shadowPaint);
                    shadowPaint.setXfermode(GlobalCache.blendModeSourceIn);
                    canvas.saveLayer(null, shadowPaint, Canvas.ALL_SAVE_FLAG);
                    currentTransformation.push(new Matrix(currentTransformation.peek()));
                    {
                        canvas.drawColor(knobBlur.color);
                    }
                    currentTransformation.pop();
                    canvas.restore();
                }
                currentTransformation.pop();
                canvas.restore();

            }

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            canvas.drawPath(ovalPath, paint);

            // Bezier
            RectF bezierRect = CacheForCanvas1.bezierRect;
            bezierRect.set(-16f, 45f, 16f, 62f);
            Path bezierPath = CacheForCanvas1.bezierPath;
            bezierPath.reset();
            bezierPath.moveTo(-14.31f, 45.06f);
            bezierPath.cubicTo(-14.83f, 45.28f, -15.33f, 46.57f, -15.64f, 48.48f);
            bezierPath.cubicTo(-15.88f, 49.94f, -15.95f, 50.79f, -15.99f, 52.73f);
            bezierPath.cubicTo(-16.04f, 55.2f, -15.88f, 57.41f, -15.5f, 59.13f);
            bezierPath.cubicTo(-15.2f, 60.53f, -14.89f, 61.3f, -14.47f, 61.67f);
            bezierPath.cubicTo(-14.26f, 61.85f, -14.24f, 61.85f, -13.57f, 61.81f);
            bezierPath.cubicTo(-12.75f, 61.77f, -11.94f, 61.59f, -10.96f, 61.25f);
            bezierPath.cubicTo(-8.72f, 60.48f, -5.74f, 58.86f, -3.57f, 57.26f);
            bezierPath.lineTo(-2.7f, 56.61f);
            bezierPath.lineTo(-2.27f, 56.82f);
            bezierPath.lineTo(-1.85f, 57.04f);
            bezierPath.lineTo(-0.01f, 57.04f);
            bezierPath.lineTo(1.82f, 57.04f);
            bezierPath.lineTo(2.21f, 56.86f);
            bezierPath.lineTo(2.6f, 56.66f);
            bezierPath.lineTo(3.12f, 57.08f);
            bezierPath.cubicTo(5.28f, 58.78f, 8.69f, 60.65f, 11.11f, 61.45f);
            bezierPath.cubicTo(12.07f, 61.77f, 12.76f, 61.92f, 13.58f, 61.96f);
            bezierPath.lineTo(14.27f, 62f);
            bezierPath.lineTo(14.47f, 61.8f);
            bezierPath.cubicTo(15.19f, 61.1f, 15.71f, 58.98f, 15.93f, 55.87f);
            bezierPath.cubicTo(16.23f, 51.46f, 15.55f, 46.39f, 14.51f, 45.36f);
            bezierPath.cubicTo(14.32f, 45.17f, 14.31f, 45.17f, 13.51f, 45.17f);
            bezierPath.cubicTo(12.56f, 45.18f, 11.95f, 45.31f, 10.49f, 45.82f);
            bezierPath.cubicTo(9.33f, 46.24f, 8.4f, 46.64f, 7.07f, 47.33f);
            bezierPath.cubicTo(5.48f, 48.16f, 3.6f, 49.33f, 2.72f, 50.04f);
            bezierPath.lineTo(2.47f, 50.24f);
            bezierPath.lineTo(2.15f, 50.08f);
            bezierPath.cubicTo(1.84f, 49.94f, 1.75f, 49.93f, -0.04f, 49.93f);
            bezierPath.cubicTo(-1.73f, 49.93f, -1.93f, 49.94f, -2.14f, 50.05f);
            bezierPath.cubicTo(-2.36f, 50.19f, -2.36f, 50.19f, -2.6f, 50f);
            bezierPath.cubicTo(-5.68f, 47.67f, -9.96f, 45.57f, -12.6f, 45.1f);
            bezierPath.cubicTo(-13.2f, 44.99f, -14.1f, 44.97f, -14.31f, 45.06f);
            bezierPath.close();

            paint.reset();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);

            if (glowEnabled) {

                canvas.saveLayerAlpha(null, 255, Canvas.ALL_SAVE_FLAG);
                currentTransformation.push(new Matrix(currentTransformation.peek()));
                {
                    Matrix invertedCurrentTransformation = new Matrix();
                    currentTransformation.peek().invert(invertedCurrentTransformation);
                    canvas.concat(invertedCurrentTransformation);
                    canvas.translate(knobBlur.dx, knobBlur.dy);
                    canvas.concat(currentTransformation.peek());

                    Paint shadowPaint = CacheForCanvas1.shadowPaint;
                    shadowPaint.set(paint);
                    knobBlur.setBlurOfPaint(shadowPaint);
                    canvas.drawPath(bezierPath, shadowPaint);
                    shadowPaint.setXfermode(GlobalCache.blendModeSourceIn);
                    canvas.saveLayer(null, shadowPaint, Canvas.ALL_SAVE_FLAG);
                    currentTransformation.push(new Matrix(currentTransformation.peek()));
                    {
                        canvas.drawColor(knobBlur.color);
                    }
                    currentTransformation.pop();
                    canvas.restore();
                }
                currentTransformation.pop();
                canvas.restore();

            }

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            canvas.drawPath(bezierPath, paint);

            currentTransformation.pop();
            canvas.restore();
        }

        currentTransformation.pop();
        canvas.restore();
    }

    public static int colorByChangingAlpha(int color, int newAlpha) {
        return Color.argb(newAlpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int colorByBlendingColors(int c1, float ratio, int c2) {
        return Color.argb((int) ((1f - ratio) * Color.alpha(c1) + ratio * Color.alpha(c2)),
                (int) ((1f - ratio) * Color.red(c1) + ratio * Color.red(c2)),
                (int) ((1f - ratio) * Color.green(c1) + ratio * Color.green(c2)),
                (int) ((1f - ratio) * Color.blue(c1) + ratio * Color.blue(c2)));
    }

    public static int colorByApplyingShadow(int color, float ratio) {
        return colorByBlendingColors(color, ratio, colorByChangingAlpha(Color.BLACK, Color.alpha(color)));
    }

    // Resizing Behavior
    public static void resizingBehaviorApply(ResizingBehavior behavior, RectF rect, RectF target, RectF result) {
        if (rect.equals(target) || target == null) {
            result.set(rect);
            return;
        }

        if (behavior == ResizingBehavior.Stretch) {
            result.set(target);
            return;
        }

        float xRatio = Math.abs(target.width() / rect.width());
        float yRatio = Math.abs(target.height() / rect.height());
        float scale = 0f;

        switch (behavior) {
            case AspectFit: {
                scale = Math.min(xRatio, yRatio);
                break;
            }
            case AspectFill: {
                scale = Math.max(xRatio, yRatio);
                break;
            }
            case Center: {
                scale = 1f;
                break;
            }
        }

        float newWidth = Math.abs(rect.width() * scale);
        float newHeight = Math.abs(rect.height() * scale);
        result.set(target.centerX() - newWidth / 2,
                target.centerY() - newHeight / 2,
                target.centerX() + newWidth / 2,
                target.centerY() + newHeight / 2);
    }

}

class ToggleGradient {
    private int[] colors;
    private float[] positions;

    public ToggleGradient(int[] colors, float[] positions) {
        if (positions == null) {
            int steps = colors.length;
            positions = new float[steps];
            for (int i = 0; i < steps; i++)
                positions[i] = (float) i / (steps - 1);
        }

        this.colors = colors;
        this.positions = positions;
    }

    public LinearGradient linearGradient(float x0, float y0, float x1, float y1) {
        return new LinearGradient(x0, y0, x1, y1, this.colors, this.positions, Shader.TileMode.CLAMP);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ToggleGradient))
            return false;
        ToggleGradient other = (ToggleGradient) obj;
        return Arrays.equals(this.colors, other.colors) && Arrays.equals(this.positions, other.positions);
    }
}


class ToggleLinearGradient {
    private LinearGradient shader;
    private ToggleGradient toggleGradient;
    private float x0, y0, x1, y1;

    LinearGradient get(ToggleGradient toggleGradient, float x0, float y0, float x1, float y1) {
        if (this.shader == null || this.x0 != x0 || this.y0 != y0 || this.x1 != x1 || this.y1 != y1 || !this.toggleGradient.equals(toggleGradient)) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            this.toggleGradient = toggleGradient;
            this.shader = toggleGradient.linearGradient(x0, y0, x1, y1);
        }
        return this.shader;
    }
}

class Shadow {
    int color;
    float dx, dy;
    private float radius;
    private BlurMaskFilter blurMaskFilter;

    Shadow() {

    }

    Shadow get(int color, float dx, float dy, float radius) {
        this.color = color;
        this.dx = dx;
        this.dy = dy;

        if (this.radius != radius) {
            this.blurMaskFilter = null;
            this.radius = radius;
        }

        return this;
    }

    void setBlurOfPaint(Paint paint) {
        if (this.radius <= 0)
            return;

        if (this.blurMaskFilter == null)
            this.blurMaskFilter = new BlurMaskFilter(this.radius, BlurMaskFilter.Blur.NORMAL);

        paint.setMaskFilter(this.blurMaskFilter);
    }
}
