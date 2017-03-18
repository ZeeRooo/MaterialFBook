package com.github.clans.fab;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import me.zeerooo.materialfb.R;

public class FloatingActionButton extends ImageButton {

    public static final int SIZE_NORMAL = 0;

    int mFabSize;
    boolean mShowShadow;
    int mShadowColor;
    int mShadowRadius = Util.dpToPx(getContext(), 4f);
    int mShadowXOffset = Util.dpToPx(getContext(), 1f);
    int mShadowYOffset = Util.dpToPx(getContext(), 3f);

    private static final Xfermode PORTER_DUFF_CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private static final long PAUSE_GROWING_TIME = 200;
    private static final double BAR_SPIN_CYCLE_TIME = 500;
    private static final int BAR_MAX_LENGTH = 270;

    private int mColorNormal;
    private int mColorPressed;
    private int mColorDisabled;
    private int mColorRipple;
    private Drawable mIcon;
    private int mIconSize = Util.dpToPx(getContext(), 24f);
    private Animation mShowAnimation;
    private Animation mHideAnimation;
    private String mLabelText;
    private OnClickListener mClickListener;
    private Drawable mBackgroundDrawable;
    private boolean mUsingElevation;
    private boolean mUsingElevationCompat;

    private float mOriginalX = -1;
    private float mOriginalY = -1;
    private boolean mButtonPositionSaved;
    private long mPausedTimeWithoutGrowing = 0;
    private double mTimeStartGrowing;
    private boolean mBarGrowingFromFront = true;
    private int mBarLength = 16;
    private float mBarExtraLength;
    private float mCurrentProgress;

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, defStyleAttr, 0);
        mColorNormal = attr.getColor(R.styleable.FloatingActionButton_fab_colorNormal, 0xFFDA4336);
        mColorPressed = attr.getColor(R.styleable.FloatingActionButton_fab_colorPressed, 0xFFE75043);
        mColorDisabled = attr.getColor(R.styleable.FloatingActionButton_fab_colorDisabled, 0xFFAAAAAA);
        mColorRipple = attr.getColor(R.styleable.FloatingActionButton_fab_colorRipple, 0x99FFFFFF);
        mShowShadow = attr.getBoolean(R.styleable.FloatingActionButton_fab_showShadow, true);
        mShadowColor = attr.getColor(R.styleable.FloatingActionButton_fab_shadowColor, 0x66000000);
        mShadowRadius = attr.getDimensionPixelSize(R.styleable.FloatingActionButton_fab_shadowRadius, mShadowRadius);
        mShadowXOffset = attr.getDimensionPixelSize(R.styleable.FloatingActionButton_fab_shadowXOffset, mShadowXOffset);
        mShadowYOffset = attr.getDimensionPixelSize(R.styleable.FloatingActionButton_fab_shadowYOffset, mShadowYOffset);
        mFabSize = attr.getInt(R.styleable.FloatingActionButton_fab_size, SIZE_NORMAL);
        mLabelText = attr.getString(R.styleable.FloatingActionButton_fab_label);

        if (attr.hasValue(R.styleable.FloatingActionButton_fab_elevationCompat)) {
            float elevation = attr.getDimensionPixelOffset(R.styleable.FloatingActionButton_fab_elevationCompat, 0);
            if (isInEditMode()) {
                setElevation(elevation);
            } else {
                setElevationCompat(elevation);
            }
        }

        initShowAnimation(attr);
        initHideAnimation(attr);
        attr.recycle();

        setClickable(true);
    }

    private void initShowAnimation(TypedArray attr) {
        int resourceId = attr.getResourceId(R.styleable.FloatingActionButton_fab_showAnimation, R.anim.fab_scale_up);
        mShowAnimation = AnimationUtils.loadAnimation(getContext(), resourceId);
    }

    private void initHideAnimation(TypedArray attr) {
        int resourceId = attr.getResourceId(R.styleable.FloatingActionButton_fab_hideAnimation, R.anim.fab_scale_down);
        mHideAnimation = AnimationUtils.loadAnimation(getContext(), resourceId);
    }

    private int getCircleSize() {
        return getResources().getDimensionPixelSize(mFabSize == SIZE_NORMAL
                ? R.dimen.fab_size_normal : R.dimen.fab_size_mini);
    }

    private int calculateMeasuredWidth() {
        int width = getCircleSize() + calculateShadowWidth();
        return width;
    }

    private int calculateMeasuredHeight() {
        int height = getCircleSize() + calculateShadowHeight();
        return height;
    }

    int calculateShadowWidth() {
        return hasShadow() ? getShadowX() * 2 : 0;
    }

    int calculateShadowHeight() {
        return hasShadow() ? getShadowY() * 2 : 0;
    }

    private int getShadowX() {
        return mShadowRadius + Math.abs(mShadowXOffset);
    }

    private int getShadowY() {
        return mShadowRadius + Math.abs(mShadowYOffset);
    }

    private float calculateCenterX() {
        return (float) (getMeasuredWidth() / 2);
    }

    private float calculateCenterY() {
        return (float) (getMeasuredHeight() / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(calculateMeasuredWidth(), calculateMeasuredHeight());
    }

    private void updateProgressLength(long deltaTimeInMillis) {
        if (mPausedTimeWithoutGrowing >= PAUSE_GROWING_TIME) {
            mTimeStartGrowing += deltaTimeInMillis;

            if (mTimeStartGrowing > BAR_SPIN_CYCLE_TIME) {
                mTimeStartGrowing -= BAR_SPIN_CYCLE_TIME;
                mPausedTimeWithoutGrowing = 0;
                mBarGrowingFromFront = !mBarGrowingFromFront;
            }

            float distance = (float) Math.cos((mTimeStartGrowing / BAR_SPIN_CYCLE_TIME + 1) * Math.PI) / 2 + 0.5f;
            float length = BAR_MAX_LENGTH - mBarLength;

            if (mBarGrowingFromFront) {
                mBarExtraLength = distance * length;
            } else {
                float newLength = length * (1 - distance);
                mCurrentProgress += (mBarExtraLength - newLength);
                mBarExtraLength = newLength;
            }
        } else {
            mPausedTimeWithoutGrowing += deltaTimeInMillis;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        saveButtonOriginalPosition();

        super.onSizeChanged(w, h, oldw, oldh);

        updateBackground();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof ViewGroup.MarginLayoutParams && mUsingElevationCompat) {
            ((ViewGroup.MarginLayoutParams) params).leftMargin += getShadowX();
            ((ViewGroup.MarginLayoutParams) params).topMargin += getShadowY();
            ((ViewGroup.MarginLayoutParams) params).rightMargin += getShadowX();
            ((ViewGroup.MarginLayoutParams) params).bottomMargin += getShadowY();
        }
        super.setLayoutParams(params);
    }

    void updateBackground() {
        LayerDrawable layerDrawable;
        if (hasShadow()) {
            layerDrawable = new LayerDrawable(new Drawable[]{
                    new Shadow(),
                    createFillDrawable(),
                    getIconDrawable()
            });
        } else {
            layerDrawable = new LayerDrawable(new Drawable[]{
                    createFillDrawable(),
                    getIconDrawable()
            });
        }

        int iconSize = -1;
        if (getIconDrawable() != null) {
            iconSize = Math.max(getIconDrawable().getIntrinsicWidth(), getIconDrawable().getIntrinsicHeight());
        }
        int iconOffset = (getCircleSize() - (iconSize > 0 ? iconSize : mIconSize)) / 2;
        int circleInsetHorizontal = hasShadow() ? mShadowRadius + Math.abs(mShadowXOffset) : 0;
        int circleInsetVertical = hasShadow() ? mShadowRadius + Math.abs(mShadowYOffset) : 0;

        layerDrawable.setLayerInset(
                hasShadow() ? 2 : 1,
                circleInsetHorizontal + iconOffset,
                circleInsetVertical + iconOffset,
                circleInsetHorizontal + iconOffset,
                circleInsetVertical + iconOffset
        );

        setBackgroundCompat(layerDrawable);
    }

    protected Drawable getIconDrawable() {
        if (mIcon != null) {
            return mIcon;
        } else {
            return new ColorDrawable(Color.TRANSPARENT);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Drawable createFillDrawable() {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_enabled}, createCircleDrawable(mColorDisabled));
        drawable.addState(new int[]{android.R.attr.state_pressed}, createCircleDrawable(mColorPressed));
        drawable.addState(new int[]{}, createCircleDrawable(mColorNormal));

        if (Util.hasLollipop()) {
            RippleDrawable ripple = new RippleDrawable(new ColorStateList(new int[][]{{}},
                    new int[]{mColorRipple}), drawable, null);
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
            setClipToOutline(true);
            mBackgroundDrawable = ripple;
            return ripple;
        }

        mBackgroundDrawable = drawable;
        return drawable;
    }

    private Drawable createCircleDrawable(int color) {
        CircleDrawable shapeDrawable = new CircleDrawable(new OvalShape());
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setBackgroundCompat(Drawable drawable) {
        if (Util.hasJellyBean()) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

    private void saveButtonOriginalPosition() {
        if (!mButtonPositionSaved) {
            if (mOriginalX == -1) {
                mOriginalX = getX();
            }

            if (mOriginalY == -1) {
                mOriginalY = getY();
            }

            mButtonPositionSaved = true;
        }
    }

    void playShowAnimation() {
        mHideAnimation.cancel();
        startAnimation(mShowAnimation);
    }

    void playHideAnimation() {
        mShowAnimation.cancel();
        startAnimation(mHideAnimation);
    }

    OnClickListener getOnClickListener() {
        return mClickListener;
    }

    Label getLabelView() {
        return (Label) getTag(R.id.fab_label);
    }

    void setColors(int colorNormal, int colorPressed, int colorRipple) {
        mColorNormal = colorNormal;
        mColorPressed = colorPressed;
        mColorRipple = colorRipple;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void onActionDown() {
        if (mBackgroundDrawable instanceof StateListDrawable) {
            StateListDrawable drawable = (StateListDrawable) mBackgroundDrawable;
            drawable.setState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed});
        } else if (Util.hasLollipop()) {
            RippleDrawable ripple = (RippleDrawable) mBackgroundDrawable;
            ripple.setState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed});
            ripple.setHotspot(calculateCenterX(), calculateCenterY());
            ripple.setVisible(true, true);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void onActionUp() {
        if (mBackgroundDrawable instanceof StateListDrawable) {
            StateListDrawable drawable = (StateListDrawable) mBackgroundDrawable;
            drawable.setState(new int[]{android.R.attr.state_enabled});
        } else if (Util.hasLollipop()) {
            RippleDrawable ripple = (RippleDrawable) mBackgroundDrawable;
            ripple.setState(new int[]{android.R.attr.state_enabled});
            ripple.setHotspot(calculateCenterX(), calculateCenterY());
            ripple.setVisible(true, true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mClickListener != null && isEnabled()) {
            Label label = (Label) getTag(R.id.fab_label);
            if (label == null) return super.onTouchEvent(event);

            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    if (label != null) {
                        label.onActionUp();
                    }
                    onActionUp();
                    break;

                case MotionEvent.ACTION_CANCEL:
                    if (label != null) {
                        label.onActionUp();
                    }
                    onActionUp();
                    break;
            }
            mGestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            Label label = (Label) getTag(R.id.fab_label);
            if (label != null) {
                label.onActionDown();
            }
            onActionDown();
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Label label = (Label) getTag(R.id.fab_label);
            if (label != null) {
                label.onActionUp();
            }
            onActionUp();
            return super.onSingleTapUp(e);
        }
    });

    private class CircleDrawable extends ShapeDrawable {

        private int circleInsetHorizontal;
        private int circleInsetVertical;

        private CircleDrawable(Shape s) {
            super(s);
            circleInsetHorizontal = hasShadow() ? mShadowRadius + Math.abs(mShadowXOffset) : 0;
            circleInsetVertical = hasShadow() ? mShadowRadius + Math.abs(mShadowYOffset) : 0;

        }

        @Override
        public void draw(Canvas canvas) {
            setBounds(circleInsetHorizontal, circleInsetVertical, calculateMeasuredWidth()
                    - circleInsetHorizontal, calculateMeasuredHeight() - circleInsetVertical);
            super.draw(canvas);
        }
    }

    private class Shadow extends Drawable {

        private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint mErase = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float mRadius;

        private Shadow() {
            this.init();
        }

        private void init() {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mColorNormal);

            mErase.setXfermode(PORTER_DUFF_CLEAR);

            if (!isInEditMode()) {
                mPaint.setShadowLayer(mShadowRadius, mShadowXOffset, mShadowYOffset, mShadowColor);
            }

            mRadius = getCircleSize() / 2;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(calculateCenterX(), calculateCenterY(), mRadius, mPaint);
            canvas.drawCircle(calculateCenterX(), calculateCenterY(), mRadius, mErase);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }
    }

    /* ===== API methods ===== */

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (mIcon != drawable) {
            mIcon = drawable;
            updateBackground();
        }
    }

    @Override
    public void setImageResource(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        if (mIcon != drawable) {
            mIcon = drawable;
            updateBackground();
        }
    }

    @Override
    public void setOnClickListener(final OnClickListener l) {
        super.setOnClickListener(l);
        mClickListener = l;
        View label = (View) getTag(R.id.fab_label);
        if (label != null) {
            label.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onClick(FloatingActionButton.this);
                    }
                }
            });
        }
    }

    public void setColorNormal(int color) {
        if (mColorNormal != color) {
            mColorNormal = color;
            updateBackground();
        }
    }

    public void setColorPressed(int color) {
        if (color != mColorPressed) {
            mColorPressed = color;
            updateBackground();
        }
    }

    public void setColorRipple(int color) {
        if (color != mColorRipple) {
            mColorRipple = color;
            updateBackground();
        }
    }

    public boolean hasShadow() {
        return !mUsingElevation && mShowShadow;
    }

    public int getShadowRadius() {
        return mShadowRadius;
    }

    public int getShadowXOffset() {
        return mShadowXOffset;
    }

    public int getShadowYOffset() {
        return mShadowYOffset;
    }

    public int getShadowColor() {
        return mShadowColor;
    }

    /**
     * Checks whether <b>FloatingActionButton</b> is hidden
     *
     * @return true if <b>FloatingActionButton</b> is hidden, false otherwise
     */
    public boolean isHidden() {
        return getVisibility() == INVISIBLE;
    }

    /**
     * Makes the <b>FloatingActionButton</b> to appear and sets its visibility to {@link #VISIBLE}
     *
     * @param animate if true - plays "show animation"
     */
    public void show(boolean animate) {
        if (isHidden()) {
            if (animate) {
                playShowAnimation();
            }
            super.setVisibility(VISIBLE);
        }
    }

    /**
     * Makes the <b>FloatingActionButton</b> to disappear and sets its visibility to {@link #INVISIBLE}
     *
     * @param animate if true - plays "hide animation"
     */
    public void hide(boolean animate) {
        if (!isHidden()) {
            if (animate) {
                playHideAnimation();
            }
            super.setVisibility(INVISIBLE);
        }
    }

    public void setLabelText(String text) {
        mLabelText = text;
        TextView labelView = getLabelView();
        if (labelView != null) {
            labelView.setText(text);
        }
    }

    public String getLabelText() {
        return mLabelText;
    }

    public void setShowAnimation(Animation showAnimation) {
        mShowAnimation = showAnimation;
    }

    public void setHideAnimation(Animation hideAnimation) {
        mHideAnimation = hideAnimation;
    }

    @Override
    public void setElevation(float elevation) {
        if (Util.hasLollipop() && elevation > 0) {
            super.setElevation(elevation);
            if (!isInEditMode()) {
                mUsingElevation = true;
                mShowShadow = false;
            }
            updateBackground();
        }
    }

    /**
     * Sets the shadow color and radius to mimic the native elevation.
     *
     * <p><b>API 21+</b>: Sets the native elevation of this view, in pixels. Updates margins to
     * make the view hold its position in layout across different platform versions.</p>
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setElevationCompat(float elevation) {
        mShadowColor = 0x26000000;
        mShadowRadius = Math.round(elevation / 2);
        mShadowXOffset = 0;
        mShadowYOffset = Math.round(mFabSize == SIZE_NORMAL ? elevation : elevation / 2);

        if (Util.hasLollipop()) {
            super.setElevation(elevation);
            mUsingElevationCompat = true;
            mShowShadow = false;
            updateBackground();

            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams != null) {
                setLayoutParams(layoutParams);
            }
        } else {
            mShowShadow = true;
            updateBackground();
        }
    }
}
