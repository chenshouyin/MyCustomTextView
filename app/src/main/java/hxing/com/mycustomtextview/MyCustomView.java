package hxing.com.mycustomtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by chenshouyin on 17/3/9.
 */

public class MyCustomView extends View implements View.OnClickListener{
    //1.此处应该继承View

    //获取atts里面定义的属性
    private String mTextViewString;
    private int mTextViewColor;
    private int mTextViewSize;

    //设置画笔和画布大小
    private Paint mTextViewPaint;
    private Rect mTextViewBoubd;//矩形区域,绘制时控制绘制的范围

    //2.此处用this依次调用第二个第三个构造方法
    public MyCustomView(Context context) {
        //super(context);
        this(context, null);
    }

    public MyCustomView(Context context, @Nullable AttributeSet attrs) {
        //super(context, attrs);
        this(context, attrs, 0);
    }

    public MyCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //3.此处得到attrs.xml里面,我们的自定义的样式属性  Attribute属性的意思,AttributeSet那么就是xml中的属性设置的意思
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyCustomTextViewAtts, defStyleAttr, 0);
        for (int i = 0; i < array.getIndexCount(); i++) {
            //思考array.getIndexCount() 如果R.styleable.MyCustomTextViewAtts没有属性会怎么样呢?
            int atts = array.getIndex(i);
            switch (atts) {//注意是getIndex(i)
                case R.styleable.MyCustomTextViewAtts_titleText:
                    mTextViewString = array.getString(atts);//注意是getString(i)
                    break;
                case R.styleable.MyCustomTextViewAtts_titleSize:
                    mTextViewSize = (int) array.getDimension(atts, 16);//默认16sp
                    break;
                case R.styleable.MyCustomTextViewAtts_titleColor:
                    mTextViewColor = array.getInteger(atts, Color.BLACK); //默认颜色设置为黑色
                    break;
            }
        }
        array.recycle();//回收

        //4.设置画笔属性
        mTextViewPaint = new Paint();
        mTextViewPaint.setTextSize(mTextViewSize);
        //mTextViewPaint.setColor(mTextViewColor);每次画的时候需要设置颜色
        //5.设置画布的宽高
        mTextViewBoubd = new Rect();
        //getTextBounds 由调用者返回在边界(分配)的最小矩形包含所有的字符,以隐含原点(0,0)
        mTextViewPaint.getTextBounds(mTextViewString, 0, mTextViewString.length(), mTextViewBoubd);//String text, int start, int end, Rect bounds

        setOnClickListener(this);//设置点击事件
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //当View设置的不是固定大小的时候,需要测量
        int myWidth, myHeight;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);//得到布局文件中宽高设置的类型
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {//设置的固定的,如100dp,MATCH_PARENT
            myWidth = widthMeasureSpec;
        } else {//包括其他不确定的情况
            mTextViewPaint.setTextSize(mTextViewSize);
            mTextViewPaint.getTextBounds(mTextViewString, 0, mTextViewString.length(), mTextViewBoubd);
            float textWidth = mTextViewBoubd.width();//文字宽度
            myWidth = (int) (textWidth + getPaddingLeft() + getPaddingRight());//加上左右间距
        }

        //高度
        if (heightMode == MeasureSpec.EXACTLY) {//设置的固定的,如100dp,MATCH_PARENT
            myHeight = heightMeasureSpec;
        } else {//包括其他不确定的情况
            mTextViewPaint.setTextSize(mTextViewSize);
            mTextViewPaint.getTextBounds(mTextViewString, 0, mTextViewString.length(), mTextViewBoubd);
            float textHeight = mTextViewBoubd.height();//文字宽度
            myHeight = (int) (textHeight + getPaddingTop() + getPaddingBottom());//加上左右间距
        }

        setMeasuredDimension(myWidth, myHeight);//设置宽高

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //5.绘制画布大小颜色
        mTextViewPaint.setColor(Color.BLUE);
        //float left, float top, float right, float bottom, Paint paint
        //参数设置参考本人博客:http://blog.csdn.net/e_inch_photo/article/details/60978088
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mTextViewPaint);//画布大小,onMeasure中测量大小

        //6.绘制文字位置,大小颜色
        mTextViewPaint.setColor(mTextViewColor);//重置画笔颜色
        //String text, int start, int end, float x, float y, Paint paint
        //参数设置参考本人博客:http://blog.csdn.net/e_Inch_Photo/article/details/60981766
        //y是指定这个字符baseline在屏幕上的位置,参照http://www.jianshu.com/p/f80f2f73bf3f
        Paint.FontMetricsInt fontMetrics = mTextViewPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(mTextViewString, getMeasuredWidth() / 2 - mTextViewBoubd.width() / 2, baseline, mTextViewPaint);//文字在画布中的位置  居中

    }

    @Override
    public void onClick(View view) {
        //点击的时候改变文字，并且重绘
        Random random = new Random();
        mTextViewString = "随机数:"+random.nextInt(100000);
        postInvalidate();//会调用onDraw方法一次
        //Android中实现view的更新有两组方法，一组是invalidate，另一组是postInvalidate，
        //其中前者是在UI线程自身中使用，而后者在非UI线程中使用。
    }
}
