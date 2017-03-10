# MyCustomTextView
自定义View动态点击显示随机数


>效果图:

![image](https://github.com/chenshouyin/MyCustomTextView/blob/master/ScrrenShort/%E8%87%AA%E5%AE%9A%E4%B9%89View%E7%82%B9%E5%87%BB%E6%95%88%E6%9E%9C.gif)


**第一步:建立一个MyCustomView继承View,实现构造方法**

```
/**
 * Created by chenshouyin on 17/3/9.
 */

public class MyCustomView extends View {
    //1.此处应该继承View

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
    }

}
```

**第二步:在res资源文件夹新建atts.xml文件设置自定义View需要用到的属性**

```
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!--1.属性声明-->

    <!--此处定义了字体类型,字体大小,字体颜色，当然还有很多其它的属性比如View的其它属性,如-->
    <attr name="titleText" format="string"></attr>
    <attr name="titleColor" format="color"></attr>
    <attr name="titleSize" format="dimension"></attr>

    <!--2.定义样式-->
    <declare-styleable name="MyCustomTextViewAtts">
        <attr name="titleText"></attr>
        <attr name="titleSize"></attr>
        <attr name="titleColor"></attr>
    </declare-styleable>

</resources>

`

**第三步:布局文件中引用自定义属性并且使用自定义属性**
```
    
![Paste_Image.png](http://upload-images.jianshu.io/upload_images/2704327-03ab97ff80e82af6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>小技巧:主要输入custom:编译器会自动给我们导入


![zhu](http://upload-images.jianshu.io/upload_images/2704327-888aaea991f5eddc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


>网上导入是这样子的
```
xmlns:custom="http://schemas.android.com/apk/res/hxing.com.mycustomtextview.MyCustomTextView
```

可能AS版本的问题,我这里会提示用
```
xmlns:custom="http://schemas.android.com/apk/res-auto"

```

![csy.jpg](http://upload-images.jianshu.io/upload_images/2704327-968a5017a650a66f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


>好了,到此,我们在布局文件也已经引入了自定义View,那么运行程序,是不是可以看到TextView了呢?别急,我们还没把View画出来呢。

**第四步:设置画笔大小,颜色,字体。**
```
  private String mTextViewString;
  private int mTextViewColor;
  private int mTextViewSize;
```



>思考:为什么MyCustomView中已经设置了文字,文字颜色,文字大小,还要设置画笔大小,颜色,字体呢?

```
  custom:titleColor="#d41e1e"
  custom:titleSize="16sp"
  custom:titleText="我是自定义居中的View"
```

>因为这里要获取我们定义的属性的值

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/2704327-2880ae9a2f151abd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



第三个构造方法里面的具体设置如下:
```
public MyCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //3.此处得到attrs.xml里面,我们的自定义的样式属性  Attribute属性的意思,AttributeSet那么就是xml中的属性设置的意思
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs,R.styleable.MyCustomTextViewAtts,defStyleAttr,0);
        for (int i=0;i<array.getIndexCount();i++){
            //思考array.getIndexCount() 如果R.styleable.MyCustomTextViewAtts没有属性会怎么样呢?
            int atts = array.getIndex(i);
            switch (atts){//注意是getIndex(i)
                case R.styleable.MyCustomTextViewAtts_titleText:
                    mTextViewString = array.getString(atts);//注意是getString(i)
                    break;
                case R.styleable.MyCustomTextViewAtts_titleSize:
                    mTextViewSize = (int)array.getDimension(atts,16);//默认16sp
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
        mTextViewPaint.getTextBounds(mTextViewString,0,mTextViewString.length(),mTextViewBoubd);//String text, int start, int end, Rect bounds
    }
```


第五步:onMeasure,我们后面再讨论。

第六步:onLayout,也放在后面再讨论。

第七步:onDraw。

```
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //5.绘制画布大小颜色
        mTextViewPaint.setColor(Color.BLUE);
        //float left, float top, float right, float bottom, Paint paint
        //参数设置参考本人博客:http://blog.csdn.net/e_inch_photo/article/details/60978088
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mTextViewPaint);//画布大小,onMeasure中测量大小

        //6.绘制文字位置,大小颜色
        mTextViewPaint.setColor(mTextViewColor);//重置画笔颜色
        //String text, int start, int end, float x, float y, Paint paint
        //参数设置参考本人博客:http://blog.csdn.net/e_Inch_Photo/article/details/60981766
        //y是指定这个字符baseline在屏幕上的位置,参照http://www.jianshu.com/p/f80f2f73bf3f
        Paint.FontMetricsInt fontMetrics = mTextViewPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(mTextViewString,getMeasuredWidth()/2-mTextViewBoubd.width()/2,baseline,mTextViewPaint);//文字在画布中的位置  居中

    }

```


好了此时看效果图:


![效果图1.png](http://upload-images.jianshu.io/upload_images/2704327-bbe763f25b68578c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


>但是很明显View的宽高不符合我们的预期,设置的是
```
android:layout_width="wrap_content"
android:layout_height="wrap_content"
```

但是占据了整个屏幕。


>如果设置成match_parent会怎样呢?效果还是一样的。
```
android:layout_width="match_parent"
android:layout_height="match_parent"
```

如果给它设置固定大小呢?

```
 android:layout_width="200dp"
 android:layout_height="50dp"
```

效果图如下:


![效果图2.png](http://upload-images.jianshu.io/upload_images/2704327-022479a05b439103.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>我这里使用的是约束布局,在父布局中居中的设置,这些都是小细节了~


```
    <hxing.com.mycustomtextview.MyCustomView
        android:layout_width="200dp"
        android:layout_height="50dp"
        custom:titleColor="#d41e1e"
        custom:titleSize="16sp"
        custom:titleText="我是自定义居中的View"
        custom:layout_constraintTop_toTopOf="parent"
        android:layout_marginTop="8dp"
        android:layout_marginRight="8dp"
        custom:layout_constraintRight_toRightOf="parent"
        custom:layout_constraintLeft_toLeftOf="parent"
        android:layout_marginLeft="8dp"
        custom:layout_constraintHorizontal_bias="0.565"
        custom:layout_constraintBottom_toBottomOf="parent"
        android:layout_marginBottom="8dp"
        custom:layout_constraintVertical_bias="0.498" />
```


>加单的自定义View已经结束了,我们在回头看下第五步
第五步:onMeasure


系统帮我们测量的高度和宽度都是MATCH_PARNET。

当我们设置明确的宽度和高度时，系统帮我们测量的结果就是我们设置的结果。

当我们设置为WRAP_CONTENT,或者MATCH_PARENT系统帮我们测量的结果就是MATCH_PARENT的长度。


所以，当设置了WRAP_CONTENT时，我们需要自己进行测量，即重写onMesure方法：

重写之前先了解MeasureSpec的specMode,一共三种类型：
EXACTLY：一般是设置了明确的值或者是MATCH_PARENT
AT_MOST：表示子布局限制在一个最大值内，一般为WARP_CONTENT
UNSPECIFIED：表示子布局想要多大就多大，很少使用

我们再把布局文件中的宽高改一下,设置成
```
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
```

但是我们还是想要得到固定大小的View。
```
  android:layout_width="200dp"
  android:layout_height="50dp"
```

重写onDraw方法

```
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
```

效果图


![效果图3.png](http://upload-images.jianshu.io/upload_images/2704327-86b563014d4bfbbd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



>关于第六步:onLayout,后面再专门讨论。
接下来实现点击事件和更新TextView文字。


```
1、implements View.OnClickListener

2、setOnClickListener(this);//设置点击事件

   
@Override
    public void onClick(View view) {
        //点击的时候改变文字，并且重绘
        Random random = new Random();
        mTextViewString = "随机数:"+random.nextInt(100000);
        postInvalidate();//会调用onDraw方法一次
        //Android中实现view的更新有两组方法，一组是invalidate，另一组是postInvalidate，
        //其中前者是在UI线程自身中使用，而后者在非UI线程中使用。
    }

```




接下来看效果图:
![自定义View点击效果.gif](http://upload-images.jianshu.io/upload_images/2704327-82f35e8a7b8be8f0.gif?imageMogr2/auto-orient/strip)


>Github地址:
https://github.com/chenshouyin/MyCustomTextView
>
博客地址:
http://blog.csdn.net/e_Inch_Photo/article/details/61195762
>简书地址:http://www.jianshu.com/p/2eecf72c54d6
