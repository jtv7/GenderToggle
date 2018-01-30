package com.jtv7.gendertogglelib;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Checkable;

/**
 * Created by jtv7 on 1/29/18.
 */

public class GenderToggle extends View implements Checkable {

    // Constants
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_HEIGHT = 60;

    private static final int MALE_COLOR = Color.argb(255, 0, 248, 252);
    private static final int FEMALE_COLOR = Color.argb(255, 251, 77, 122);
    private static final int SHADOW_INTENSITY = 10;
    private static final int BOWTIE_ANGLE = 135;
    private static final int KNOB_END = 17;
    private static final int POSITION_MULTIPLIER = 135;
    private static final String EXTRA_SUPER = "extra_super";
    private static final String EXTRA_CHECKED = "extra_checked";


    private int maleColor = MALE_COLOR;
    private int femaleColor = FEMALE_COLOR;

    private static boolean rotateGradient = false;
    private boolean glowEnabled = true;


    private static int mDuration = 600;
    private static int bowtieStartDelay = 600;

    private static float shadowIntensityAnimate = SHADOW_INTENSITY;
    private static float gradientIntensityAnimate = 0.25f;

    private float rotation = 0;
    private RectF rectF;

    private int knobColor = MALE_COLOR;

    // Animator
    private ValueAnimator knobMoveAnimator;
    private ValueAnimator bowtieMoveAnimator;
    private ValueAnimator gradientAnimator;
    private ValueAnimator gradientToggleAnimator;

    private float knobAnimatedPosition;
    private Checked currentChecked;

    private boolean isAnimating = false;

    private GenderCheckedChangeListener listener;


    public GenderToggle(Context context) {
        super(context);
        init(null);
    }

    public GenderToggle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GenderToggle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attr) {
        if (attr != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attr, R.styleable.GenderToggle);
            currentChecked = Checked.values()[ta.getInt(R.styleable.GenderToggle_gt_default_selection, 0)];
            glowEnabled = ta.getBoolean(R.styleable.GenderToggle_gt_glow_enabled, true);
            maleColor = ta.getColor(R.styleable.GenderToggle_gt_male_color, MALE_COLOR);
            femaleColor = ta.getColor(R.styleable.GenderToggle_gt_female_color, FEMALE_COLOR);
            ta.recycle();
        } else {
            currentChecked = Checked.MALE;
        }

        rectF = new RectF();

        rotateGradient = currentChecked == Checked.MALE;

        ensureCorrectValues();

        setClickable(true);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rectF.set(0, 0, getWidth(), getHeight());

        rotateGradient = currentChecked == Checked.MALE;

        float animate = Math.max(knobAnimatedPosition * POSITION_MULTIPLIER, KNOB_END);

        GenderToggleUtil.drawToggle(canvas, rectF, GenderToggleUtil.ResizingBehavior.AspectFit, knobColor, animate, rotation, shadowIntensityAnimate, gradientIntensityAnimate, rotateGradient, glowEnabled);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getSize(widthMeasureSpec, dp2px(DEFAULT_WIDTH));
        int height = getSize(heightMeasureSpec, dp2px(DEFAULT_HEIGHT));

        setMeasuredDimension(width, height);
    }

    private int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getSize(int measureSpec, int fallbackSize) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                return Math.min(size, fallbackSize);
            case MeasureSpec.EXACTLY:
                return size;
            case MeasureSpec.UNSPECIFIED:
                return fallbackSize;
            default:
                return size;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable(EXTRA_SUPER, super.onSaveInstanceState());
        state.putInt(EXTRA_CHECKED, currentChecked.ordinal());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcel) {
        Bundle state = (Bundle) parcel;
        super.onRestoreInstanceState(state.getParcelable(EXTRA_SUPER));
        currentChecked = Checked.values()[state.getInt(EXTRA_CHECKED, 0)];
        ensureCorrectValues();
    }

    private ValueAnimator getKnobMoveAnimator(boolean checkState) {
        if (checkState) {
            return ValueAnimator.ofFloat(0, 1.0f);
        } else {
            return ValueAnimator.ofFloat(1.0f, 0);
        }
    }

    private ValueAnimator getGradientAnimator(boolean checkState) {
        if (checkState) {
            return ValueAnimator.ofFloat(-1.0f, 1.0f);
        } else {
            return ValueAnimator.ofFloat(1.0f, -1.0f);
        }
    }

    private ValueAnimator getToggleGradientAnimator() {
        return ValueAnimator.ofFloat(0, 0.25f);
    }

    private void setAnimatedState(Checked checked) {

        if (checked == Checked.MALE) {
            knobMoveAnimator = getKnobMoveAnimator(true);
            gradientAnimator = getGradientAnimator(true);
            gradientToggleAnimator = getToggleGradientAnimator();
            knobMoveAnimator.setInterpolator(new AccelerateInterpolator());
            bowtieStartDelay = 600;
            mDuration = 600;
        } else {
            knobMoveAnimator = getKnobMoveAnimator(false);
            gradientAnimator = getGradientAnimator(false);
            gradientToggleAnimator = getToggleGradientAnimator();
            knobMoveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            bowtieStartDelay = 0;
            mDuration = 400;
        }

        bowtieMoveAnimator = knobMoveAnimator.clone();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(knobMoveAnimator, bowtieMoveAnimator, gradientAnimator, gradientToggleAnimator);

        knobMoveAnimator.setDuration(mDuration);
        knobMoveAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        knobMoveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                knobAnimatedPosition = (float) animation.getAnimatedValue();
                knobColor = translateColor(knobAnimatedPosition, maleColor, femaleColor);

                rotation = Math.max(0, rotation);

                invalidate();
            }
        });

        bowtieMoveAnimator.setDuration(200);
        bowtieMoveAnimator.setStartDelay(bowtieStartDelay);
        bowtieMoveAnimator.setInterpolator(new OvershootInterpolator());
        bowtieMoveAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        bowtieMoveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float bowTieAnimatedValue = (float) animation.getAnimatedValue();
                rotation = (BOWTIE_ANGLE * bowTieAnimatedValue);

                invalidate();
            }
        });


        ///Gradient animation
        gradientAnimator.setDuration(400);
        gradientAnimator.setStartDelay(mDuration);
        gradientAnimator.setInterpolator(new LinearInterpolator());
        gradientAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        gradientAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float d = (float) animation.getAnimatedValue();
                shadowIntensityAnimate = (SHADOW_INTENSITY * d);
                invalidate();
            }
        });

        ///Toggle gradient animation
        gradientToggleAnimator.setDuration(1500);
        gradientToggleAnimator.setStartDelay(mDuration);
        gradientToggleAnimator.setInterpolator(new LinearInterpolator());
        gradientToggleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        gradientToggleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                gradientIntensityAnimate = (float) animation.getAnimatedValue();

                invalidate();
            }
        });


        animatorSet.start();
    }

    private int translateColor(float percent, int startColor, int endColor) {
        return (Integer) new ArgbEvaluator().evaluate(percent, startColor, endColor);
    }

    public enum Checked {
        MALE {
            @Override
            public Checked toggle() {
                return FEMALE;
            }
        },
        FEMALE {
            @Override
            public Checked toggle() {
                return MALE;
            }
        };

        public abstract Checked toggle();
    }

    @Override
    public void setChecked(boolean checked) {
        if (isAnimating) {
            return;
        }

        if (currentChecked == Checked.FEMALE) {
            currentChecked = Checked.MALE;
        } else {
            currentChecked = Checked.FEMALE;
        }

        if (currentChecked == Checked.MALE) {
            setAnimatedState(Checked.FEMALE);
        } else {
            setAnimatedState(Checked.MALE);
        }
        if (listener != null) {
            listener.onCheckChanged(currentChecked);
        }
    }

    private void ensureCorrectValues() {
        if (currentChecked == Checked.MALE) {
            bowtieStartDelay = 600;
            mDuration = 600;
            shadowIntensityAnimate = -SHADOW_INTENSITY;
            knobColor = maleColor;
            rotation = 0;
            knobAnimatedPosition = 0;
        } else {
            bowtieStartDelay = 0;
            mDuration = 400;
            shadowIntensityAnimate = SHADOW_INTENSITY;
            knobColor = femaleColor;
            rotation = BOWTIE_ANGLE;
            knobAnimatedPosition = 1;
        }
    }

    public interface GenderCheckedChangeListener {
        void onCheckChanged(Checked current);
    }

    @Override
    public boolean performClick() {
        if (currentChecked == Checked.FEMALE) {
            setChecked(false);
        } else {
            setChecked(true);
        }
        return super.performClick();
    }

    @Override
    public boolean isChecked() {
        return currentChecked == Checked.FEMALE;
    }

    @Override
    public void toggle() {
        performClick();
    }

    public Checked getChecked() {
        return currentChecked;
    }

    // Sets the checked value without animation
    public void setChecked(Checked checked) {
        this.currentChecked = checked;
        ensureCorrectValues();
    }

    public void setCheckedChangeListener(GenderCheckedChangeListener listener) {
        this.listener = listener;
    }

    public void setGlowEnabled(boolean glowEnabled) {
        this.glowEnabled = glowEnabled;
        ensureCorrectValues();
    }

    public boolean getGlowEnabled() {
        return this.glowEnabled;
    }

    public int getFemaleColor() {
        return femaleColor;
    }

    public void setFemaleColor(int femaleColor) {
        this.femaleColor = femaleColor;
        ensureCorrectValues();
    }

    public int getMaleColor() {
        return maleColor;
    }

    public void setMaleColor(int maleColor) {
        this.maleColor = maleColor;
        ensureCorrectValues();
    }

    // Glow will NOT work if this is enabled!
    public void useHardwareAcceleration() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        glowEnabled = false;
    }
}
