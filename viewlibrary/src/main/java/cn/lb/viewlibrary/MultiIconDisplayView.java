package cn.lb.viewlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MultiIconDisplayView extends View {
    private static final boolean DEBUG = true;
    public MultiIconDisplayView(Context context) {
        this(context, null);
    }

    public MultiIconDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (DEBUG) {

            GradientDrawable debugDrawable = new GradientDrawable();
            debugDrawable.setColor(0xFF00A080);
            debugDrawable.setShape(GradientDrawable.RECTANGLE);

            Drawable drawable = debugDrawable;

            List<Drawable> list = new ArrayList<>();
            List<Drawable> list1 = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                list.add(drawable);
            }
            for (int i = 0; i < 4; i++) {
                list1.add(drawable);
            }

            setItems(list, list, list);
        }
    }

    private Paint mLinePaint;
    private int mOritation = LinearLayout.VERTICAL;

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

    private int mItemSize = 0;
    //每一行的item个数
    private int mItemCount = SF_DEF_ITEM_NUM;
    //默认个数 当未设置个数和item尺寸的时候有效
    private static final int SF_DEF_ITEM_NUM = 6;

    private List<IconItem>[] mItems;
    public void setItems(List<Drawable> ... items) {
        List<IconItem>[] oldItems = new List[items.length];
        for (int i = 0; i < items.length; i++) {
            ArrayList<IconItem> listItem = new ArrayList<>();
            IconItem iconItem;
            for (int idx = 0, n = items[i].size(); idx < n; idx++) {
                iconItem = new IconItem(items[i].get(idx), new Rect());
                listItem.add(iconItem);
            }
            oldItems[i] = listItem;
        }

        //FIXME 通过计算 判断是否需要重新计算布局
        mItems = oldItems;
        if (getMeasuredWidth() != 0) {
            requestLayout();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mItems == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (mOritation == LinearLayout.VERTICAL) {
            measureVerticalLayout(widthMeasureSpec, heightMeasureSpec);
        } else {
            // 未实现水平布局
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureVerticalLayout(int widthMeasureSpec, int heightMeasureSpec) {
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        int h = View.MeasureSpec.getSize(heightMeasureSpec);
        int hm = View.MeasureSpec.getMode(heightMeasureSpec);

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
            int curLeft = mVerticalSpanBoundValid ? getVerticalSpan(w, h) : 0;

            lines += (n / row + ((n % row) == 0 ? 0 : 1));
            for (int j = 0, k = mItems[i].size(); j < k; j++) {
                IconItem ii = mItems[i].get(j);
                ii.updateRect(curLeft, curTop, curLeft + itemSize, curTop + itemSize);

                if (j != 0 && ((j % row) == (row - 1)) && (j != n - 1)) {
                    curLeft = mVerticalSpanBoundValid ? getVerticalSpan(w, h) : 0;
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

        setMeasuredDimension(w, heightCount);
    }

    private int getVarietySpan(int measureWidth, int measureHeight) {
        return mVarietySpan;
    }

    private int getVerticalSpan(int measureWidth, int measureHeight) {
        if (mOritation == LinearLayout.VERTICAL) {
            return mVerticalSpan;
        } else {
            return mVerticalSpan;
        }
    }
    private int getHorizontalSpan(int measureWidth, int measureHeight) {
        if (mOritation == LinearLayout.VERTICAL) {
            return mHorizontalSpan;
        } else {
            return mHorizontalSpan;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0, l = mItems.length; i < l; i++) {
            List<IconItem> vItems = mItems[i];
            int j = 0;
            int k = vItems.size();
            for (;j < k; j++) {
                vItems.get(j).draw(canvas);
            }

            // Vertical style
            if (i != 0) {
                IconItem lastItem;
                if (vItems.size() >= mItemCount) {
                    lastItem = vItems.get(mItemCount - 1);
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
        public void setColorFilter(ColorFilter filter) {
            this.mColorFilter =  filter;
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