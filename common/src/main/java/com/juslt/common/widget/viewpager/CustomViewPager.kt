package com.juslt.common.widget.viewpager

import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.juslt.common.R
import com.juslt.common.utils.SizeUtil
import org.jetbrains.anko.find
import java.lang.ref.WeakReference

/**
 * Created by wx on 2018/5/30.
 */
class CustomViewPager @JvmOverloads constructor(context:Context,attributeSet: AttributeSet?=null,defAttr:Int=0):FrameLayout(context,attributeSet,defAttr),ViewPager.OnPageChangeListener{
    private val BANNER_NEXT=100
    private val BANNER_PAUSE=200
    private val BANNER_RESUME=300

    val viewPager by lazy { this.find<ViewPager>(R.id.view_pager) }
    val indicator by lazy { this.find<ViewPagerIndicator>(R.id.indicator) }


    private var isAutoScroll =false
    private var isHaveTransformer =false

    val handler by lazy { PagerHandler(WeakReference(this)) }

    private val imageList = ArrayList<View>()

    init {
        LayoutInflater.from(context).inflate(R.layout.v_viewpager,this)

        //xml自定义属性
        val typeArray = context.obtainStyledAttributes(attributeSet,R.styleable.CustomViewPager)
        isAutoScroll=typeArray.getBoolean(R.styleable.CustomViewPager_is_auto_scroll,false)
        isHaveTransformer=typeArray.getBoolean(R.styleable.CustomViewPager_is_have_transformer,false)

    }

    fun update(any: Any){
        imageList.clear()
        imageList.addAll(any as ArrayList<View>)

        viewPager.adapter=ViewPagerAdapter(context,imageList)
        viewPager.setOnPageChangeListener(this)

        if(imageList.size>1){
            indicator.removeAllViews()
            indicator.initPointNum(imageList.size)
            indicator.setSelected((Int.MAX_VALUE/2)%imageList.size)

            viewPager.currentItem = Int.MAX_VALUE/2
        }


        if(isHaveTransformer){
            viewPager.pageMargin= SizeUtil.dip2px(context,0f) //设置page间间距
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(SizeUtil.dip2px(context,20f),0,SizeUtil.dip2px(context,20f),0)
            viewPager.layoutParams=params
            viewPager.setPageTransformer(false,DepthPageTransformer())  //缩放和渐变效果
        }

        if(isAutoScroll){
            handler.sendEmptyMessageDelayed(BANNER_NEXT,10000)  //自动轮播
        }
    }


    override fun onPageScrollStateChanged(state: Int) {
        when(state){
            ViewPager.SCROLL_STATE_DRAGGING->{
                handler.sendEmptyMessage(BANNER_PAUSE)
            }
            ViewPager.SCROLL_STATE_IDLE->{
                if(isAutoScroll){
                    handler.sendEmptyMessageDelayed(BANNER_NEXT,10000)  //自动轮播
                }
                viewPager.invalidate()
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        indicator.setSelected(position%imageList.size)
    }

    override fun onPageSelected(position: Int) {

    }

    class PagerHandler(private val weakReference: WeakReference<CustomViewPager>):Handler(){

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val customView: CustomViewPager? = weakReference.get() ?: return

            if(customView!!.handler.hasMessages(customView.BANNER_NEXT)){
                customView.handler.removeMessages(customView.BANNER_NEXT)
            }
            when(msg!!.what){
                customView.BANNER_NEXT->{
                    var curItem = customView.viewPager.currentItem
                    customView.viewPager.currentItem = ++curItem
                    customView.handler.sendEmptyMessageDelayed(customView.BANNER_NEXT,10000)
                }
                customView.BANNER_PAUSE->{
                    return
                }
                customView.BANNER_RESUME->{
                    customView.handler.sendEmptyMessageDelayed(customView.BANNER_NEXT,10000)
                }
            }
        }
    }
}