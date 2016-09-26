package cn.lb.viewlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MultiIconDisplayView extends View {
    public static final int GRAVITY_LEFT = 0;
    public static final int GRAVITY_CENTER = 1;
    public static final int GRAVITY_CENTER_VERTICAL_STYLE1 = 2;
    private static final boolean DEBUG = true;

    //默认个数 当未设置个数和item尺寸的时候有效
    private static final int SF_DEF_ITEM_NUM = 6;

    public MultiIconDisplayView(Context context) {
        this(context, null);
    }

    public MultiIconDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MultiIconDisplayView);
            int vaSize = ta.getDimensionPixelSize(R.styleable.MultiIconDisplayView_VarietySize, 0);
            int hSpan = ta.getDimensionPixelSize(R.styleable.MultiIconDisplayView_HorizontalSpan, 0);
            int vSpan = ta.getDimensionPixelSize(R.styleable.MultiIconDisplayView_VerticalSpan, 0);
            int vaSpan = ta.getDimensionPixelSize(R.styleable.MultiIconDisplayView_VarietySpan, 0);
            int itemSize = ta.getDimensionPixelSize(R.styleable.MultiIconDisplayView_ItemSize, 0);
            int itemCount = ta.getInt(R.styleable.MultiIconDisplayView_ItemCount, 0);

            mGravity = ta.getInt(R.styleable.MultiIconDisplayView_Gravity, 0);

            mItemSize = itemSize;
            if (itemCount != 0) {
                mItemCount = itemCount;
            }

            if (vaSize != 0) {
                mVarietySize = vaSize;
            }

            if (hSpan !=0) {
                mHorizontalSpan = hSpan;
            } else {
                float hSpanScale = ta.getFloat(R.styleable.MultiIconDisplayView_HorizontalSpanScale, 0F);
                if (isCorrectPercentScale(hSpanScale)) {
                    mHorizontalSpanScale = hSpanScale / 100F;
                }
            }

            if (vSpan != 0) {
                mVerticalSpan = vSpan;
            } else {
                float vSpanScale = ta.getFloat(R.styleable.MultiIconDisplayView_VerticalSpanScale, 0F);
                if (isCorrectPercentScale(vSpanScale)) {
                    mVerticalSpanScale = vSpanScale / 100F;
                }
            }

            if (vaSpan != 0) {
                mVarietySpan = vaSpan;
            } else {
                float vaSpanScale = ta.getFloat(R.styleable.MultiIconDisplayView_VarietySpanScale, 0F);
                if (isCorrectPercentScale(vaSpanScale)) {
                    mVarietySpanScale = vaSpanScale / 100F;
                }
            }

            ta.recycle();
        }

        if (DEBUG) {
            GradientDrawable debugDrawable = new GradientDrawable();
            debugDrawable.setColor(0xFF00A080);
            debugDrawable.setShape(GradientDrawable.RECTANGLE);

            Drawable drawable = debugDrawable;

            List<Drawable> list = new ArrayList<>();
            List<Drawable> list1 = new ArrayList<>();
            for (int i = 0; i < 14; i++) {
                list.add(drawable);
            }
            for (int i = 0; i < 14; i++) {
                list1.add(drawable);
            }
            List<List<Drawable>> all = new ArrayList<>();
            all.add(list);
            //all.add(list1);

            setItems(all);
        }
    }

    private boolean isCorrectPercentScale(float v) {
        return v > 0F && v < 100F;
    }

    private Paint mLinePaint;
    private int mOritation = LinearLayout.VERTICAL;

    //比例设置 0-1F
    private float mHorizontalSpanScale = 0F;
    private float mVerticalSpanScale = 0F;
    private float mVarietySpanScale = 0F;

    //水平方向的间距
    private int mHorizontalSpan = 10;
    //竖直方向的间距
    private int mVerticalSpan = 10;
    //分类的间距
    private int mVarietySpan = 40;
    //分类线宽度
    private int mVarietySize = 2;
    //分类线颜色
    private int mVarietyColor = 0x80FFFFFF;

    //边界有效
    private boolean mHorizontalBoundValid = true;
    private boolean mVerticalSpanBoundValid = true;

    //布局方向
    private int mGravity;

    /**
     * @param gravity {@link #GRAVITY_CENTER}
     */
    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    /**
     * 设置分类间隔 相对于控件宽高
     * @param scale 百分比
     */
    public void setVarietySpanScale(float scale) {
        this.mVarietySpanScale = scale;
    }

    /**
     * 设置水平方向上竖线 相对于控件宽高的比例
     * @param scale 百分比
     */
    public void setHorizontalSpanScale(float scale) {
        this.mHorizontalSpanScale = scale / 100;
    }

    /**
     * 设置竖直方向上横线 相对于控件宽高的比例
     * @param scale 百分比
     */
    public void setVerticalSpanScale(float scale) {
        this.mVerticalSpanScale = scale / 100;
    }

    /**
     * 设置水平方向上的竖线宽度
     * @param span px
     */
    public void setHorizontalSpan(int span) {
        this.mHorizontalSpan = span;
    }

    /**
     * 设置竖直方向上的横线宽度
     * @param span px
     */
    public void setVerticalSpan(int span) {
        this.mVerticalSpan = span;
    }

    /**
     * 设置分类线的宽度
     * @param size px
     */
    public void setVarietySize(int size) {
        this.mVarietySize = size;
    }

    /**
     * 设置分类线的颜色
     * @param color argb
     */
    public void setVarietyColor(int color) {
        this.mVarietyColor = color;
    }

    /**
     * 设置每一个item的尺寸 优先级低于设置item的个数
     * @param size px
     */
    public void setItemSize(int size) {
        if (size > 0) {
            this.mItemSize = size;
        }
    }
    public void setItemCount(int count) {
        if (mItemCount > 0) {
            this.mItemCount = count;
        }
    }

    //每一个item尺寸
    private int mItemSize = 0;
    //每一行的item个数
    private int mItemCount = SF_DEF_ITEM_NUM;

    private int mMeasureItemCount;
    private int mMeasureItemSize;

    private List<IconItem>[] mItems;

    /**
     * 设置需要显示的图片，允许为空
     */
    @SuppressWarnings("unchecked")
    public void setItems(List<List<Drawable>> items) {
        List<IconItem>[] newItems = null;

        if (items != null) {
            newItems = new List[items.size()];
            for (int i = 0; i < items.size(); i++) {
                ArrayList<IconItem> listItem = new ArrayList<>();
                IconItem iconItem;
                for (int idx = 0, n = items.get(i).size(); idx < n; idx++) {
                    iconItem = new IconItem(items.get(i).get(idx), new Rect());
                    listItem.add(iconItem);
                }
                newItems[i] = listItem;
            }
        }

        //FIXME 通过计算 判断是否需要重新计算布局
        mItems = newItems;
        if (getMeasuredWidth() != 0) {
            requestLayout();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                clickEvent(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
    private void clickEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (mItems != null && mItemClickListener != null) {
            int idx = 0;
            for (int i = 0; i < mItems.length; i++) {
                for (int j = 0; j < mItems[i].size(); j++) {
                    IconItem ii = mItems[i].get(j);
                    if (ii.mRect != null && ii.mRect.contains(x, y)) {
                        mItemClickListener.itemClickListener(this, idx);
                        return;
                    }
                    idx++;
                }
            }
        }
    }

    private OnItemClickListener mItemClickListener;
    public void onItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void itemClickListener(MultiIconDisplayView view, int position);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mItems == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (mGravity == GRAVITY_CENTER_VERTICAL_STYLE1) {
            measureCenterStyle1(widthMeasureSpec, heightMeasureSpec);
        } else if (mOritation == LinearLayout.VERTICAL) {
            measureVerticalLayout(widthMeasureSpec, heightMeasureSpec);
        } else {
            // 未实现水平布局
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    private int mCenterVerticalStyle1Lines = 2;
    private float mCenterVerticalStyle1ItemSizeScale = 0.3F;//占高度的多少
    private float mCenterVerticalStyle1ItemSpaceScale = 0.14F;//占高度的多少
    private int mCenterVerticalStyle1LayoutChangeCount = 3;//当为三项或者更低的时候 需要改变布局方案
    private void measureCenterStyle1(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        if (h == 0 && getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            h = viewGroup.getHeight();
        }
        if (h == 0 || mItemCount == 0) {
            setMeasuredDimension(w, h);
            return;
        }

        int heightSpace = (int) (h * mCenterVerticalStyle1ItemSpaceScale);
        int itemSize = (int) (h * mCenterVerticalStyle1ItemSizeScale);

        int itemLeft;
        int itemTop = heightSpace;
        int itemVerticalSpace;
        int itemHorizontalSpace = heightSpace;
        final int itemLen = mItems[0].size();
        int lines = itemLen / mItemCount + ((itemLen % mItemCount != 0) ? 1 : 0);
        int extraLine = lines - mCenterVerticalStyle1Lines;
        if (itemLen <= mItemCount) {
            itemTop = (h - itemSize) / 2;
        }

        if (itemLen <= mCenterVerticalStyle1LayoutChangeCount) {
            itemVerticalSpace = (w - itemLen * itemSize) / itemLen;
            itemLeft = itemVerticalSpace / 2;
        } else {
            itemVerticalSpace = (w - mItemCount * itemSize) / (mItemCount + 1);
            itemLeft = itemVerticalSpace;
        }
        int curLeft = itemLeft, curTop = itemTop;
        for (int i = 0; i < itemLen;) {
            IconItem ii = mItems[0].get(i);
            ii.updateRect(curLeft, curTop, curLeft + itemSize, curTop + itemSize);

            i++;
            if (i != 0 && (i % mItemCount) == 0) {
                curLeft = itemLeft;
                curTop += (itemSize + itemHorizontalSpace);
            } else {
                curLeft += (itemSize + itemVerticalSpace);
            }
        }

        mMeasureItemCount = mItemCount;
        mMeasureItemSize = itemSize;

        setMeasuredDimension(w, extraLine <= 0 ? h : (int) (h + extraLine * h * 0.5F + 1));
    }

    private void measureVerticalLayout(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int hm = MeasureSpec.getMode(heightMeasureSpec);

        int row = 0;
        int itemSize = 0;
        int lines = 0;
        int variaCount = mItems.length - 1;
        int heightCount = 0;

        if (mItemCount != 0) {
            row = mItemCount;
            int vsp = mVerticalSpanBoundValid ? (row + 1) : (row - 1);
            itemSize = (int) ((w - vsp * getVerticalSpan(w, h) * 1F) / row);
        } else if (mItemSize != 0) {
            itemSize = mItemSize;
            int vp = getVerticalSpan(w, h);
            int countW = mVerticalSpanBoundValid ? (w - vp) : (w + vp);
            row = countW / (itemSize + vp);
        } else {
            throw new IllegalArgumentException("row and itemSize is all zero!!");
        }

        int curTop = mHorizontalBoundValid ? getHorizontalSpan(w, h) : 0;
        for (int i = 0, l = mItems.length; i < l; i++) {
            int n = mItems[i].size();
            int curLeft = getItemLeft(row, 0,  mItems[i].size(), w, h, itemSize);

            lines += (n / row + ((n % row) == 0 ? 0 : 1));
            for (int j = 0, k = mItems[i].size(); j < k; j++) {
                IconItem ii = mItems[i].get(j);
                ii.updateRect(curLeft, curTop, curLeft + itemSize, curTop + itemSize);

                if (j != 0 && ((j % row) == (row - 1)) && (j != n - 1)) {
                    curLeft = getItemLeft(row, j + 1,  mItems[i].size(), w, h, itemSize);
                    curTop += (itemSize + getHorizontalSpan(w, h));
                } else {
                    curLeft += (itemSize + getVerticalSpan(w, h));
                }
            }
            curTop += (itemSize + getVarietySpan(w, h));
        }

        int rl = lines - variaCount;
        heightCount = lines * itemSize + variaCount * getVarietySpan(w, h) +
                (mHorizontalBoundValid ? (rl + 1) : (rl - 1)) * getHorizontalSpan(w, h);

        if (heightCount < 0) {
            heightCount = 0;
        }

        mMeasureItemCount = row;
        mMeasureItemSize = itemSize;

        setMeasuredDimension(w, heightCount);
    }

    /**
     * 处理布局方案 现在只支持左对齐和居中对齐
     */
    private int getItemLeft(int row, int idx, int total, int width, int height, int itemSize) {
        int surp = total - idx;
        int vSpace = mVerticalSpanBoundValid ? getVerticalSpan(width, height) : 0;
        if (mGravity == GRAVITY_LEFT) {
            return vSpace;
        }
        if (surp >= row) {
            return vSpace;
        } else {
            int totalSize = itemSize * surp + (surp - 1) * vSpace;
            return (width - totalSize ) / 2;
        }
    }

    private int getVarietySpan(int w, int h) {
        return mVarietySpanScale != 0 ?
                ((int) (mVarietySpanScale * getStandardSide(w, h))) : mVarietySpan;
    }

    private int getVerticalSpan(int w, int h) {
        return mVerticalSpanScale != 0 ?
                ((int) (mVerticalSpanScale * getStandardSide(w, h))) : mVerticalSpan;
    }

    private int getHorizontalSpan(int w, int h) {
        return mHorizontalSpanScale != 0 ?
                ((int) (mHorizontalSpanScale * getStandardSide(w, h))) : mHorizontalSpan;
    }

    private int getStandardSide(int width,  int height) {
        if (mOritation == LinearLayout.VERTICAL) {
            return width;
        } else {
            return height;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0, l = mItems.length; i < l; i++) {
            List<IconItem> vItems = mItems[i];
            int j = 0;
            int k = vItems.size();
            if (k == 0) {
                continue;
            }

            for (;j < k; j++) {
                vItems.get(j).draw(canvas);
            }

            // Vertical style
            if (i != 0) {
                IconItem lastItem;
                if (vItems.size() >= mMeasureItemCount) {
                    lastItem = vItems.get(mMeasureItemCount - 1);
                } else {
                    lastItem = vItems.get(vItems.size() - 1);
                }

                int left = mVerticalSpanBoundValid ? getVerticalSpan(getWidth(), getHeight()) : 0;
                drawVairetyLine(canvas, left,
                        lastItem.mRect.top - getVarietySpan(getWidth(), getHeight()),
                        lastItem.mRect.right,
                        lastItem.mRect.top);
            }
        }
    }

    private void drawVairetyLine(Canvas canvas, int left, int top, int right, int bottom) {
        int hv = getVarietySpan(getWidth(), getHeight()) / 2;
        int dt = top + hv - mVarietySize / 2;
        int db = dt + mVarietySize;

        mLinePaint.setColor(mVarietyColor);
        canvas.drawRect(left, dt, right, db, mLinePaint);
    }

    private class IconItem {
        //绘制
        Drawable mDrawable;
        //item占用区域
        Rect mRect;
        //绘制区域 mPaddingScale > 0 有效
        Rect mTempDrawRect;

        //用户设置padding
        float mPaddingScale;
        //用于染色
        ColorFilter mColorFilter;


        public IconItem(@NonNull Drawable drawable, @NonNull Rect rect) {
            this.mDrawable = drawable;
            this.mRect = rect;
        }
        public void updateRect(int l, int t, int r, int b) {
            mRect.set(l, t, r, b);
        }
        public void draw(Canvas c) {
            mDrawable.setBounds(getDrawBound());
            if (mColorFilter != null) {
                mDrawable.setColorFilter(mColorFilter);
            }
            mDrawable.draw(c);
        }
        public void setColorFilter(int color) {
            setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        }
        public void setColorFilter(ColorFilter filter) {
            this.mColorFilter =  filter;
            if (mRect.width() != 0) {
                invalidate();
            }
        }
        public void setPaddingScale(float scale) {
            mPaddingScale = scale;
            if (scale > 0) {
                mTempDrawRect = new Rect();
            } else {
                mTempDrawRect = null;
            }
        }
        private Rect getDrawBound() {
            if (mTempDrawRect != null) {
                int scaleSize = Math.round(mRect.width() * mPaddingScale);
                int scaleLeft = mRect.left + (mRect.width() - scaleSize) / 2;
                int scaleTop = mRect.top + (mRect.width() - scaleSize) / 2;
                mTempDrawRect.set(scaleLeft, scaleTop, scaleLeft + scaleSize, scaleTop + scaleSize );

                return mTempDrawRect;
            } else {
                return mRect;
            }
        }
    }
}