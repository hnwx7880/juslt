package com.juslt.common.widget.toolbar

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.juslt.common.R
import org.jetbrains.anko.find

/**
 * Created by wx on 2018/4/28.
 *  当app为沉浸式状态栏时，需要配合SysStatusBarUtil和SoftHideKeyBoardUtil使用 ，兼容所有版本的状态栏问题
 */
class CustomToolBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    private val tvTitle by lazy { find<TextView>(R.id.tv_title) }
    private val ivBack by lazy { find<ImageView>(R.id.iv_back) }
    private val vDivider by lazy { find<ImageView>(R.id.iv_divider) }
    private val ivRight by lazy { find<ImageView>(R.id.iv_right) }
    private val tvRightText by lazy { find<TextView>(R.id.tv_right_text) }

    private val llRightContainer by lazy { find<LinearLayout>(R.id.ll_right) }
    init {
        LayoutInflater.from(context).inflate(R.layout.v_toolbar, this)



        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.CustomToolBar)
        val isDarkStatusBar = typeArray.getBoolean(R.styleable.CustomToolBar_is_dark_status_bar,false) //是否设置深色状态栏 白底黑字
        val isImmersionStatusBar =typeArray.getBoolean(R.styleable.CustomToolBar_is_immersion_status_bar,true) //是否为沉浸式状态栏，默认为沉浸式
        val titleNameStyle = typeArray.getString(R.styleable.CustomToolBar_title_name)
        val titleColorStyle = typeArray.getColor(R.styleable.CustomToolBar_title_name_color, Color.parseColor("#4c4c4c"))
        val icBackStyle = typeArray.getResourceId(R.styleable.CustomToolBar_left_icon,R.mipmap.ic_back)
        val rightTextStyle = typeArray.getString(R.styleable.CustomToolBar_right_text)
        val rightTextColorStyle = typeArray.getColor(R.styleable.CustomToolBar_right_text_color, Color.parseColor("#4c4c4c"))
        val rightIconStyle = typeArray.getResourceId(R.styleable.CustomToolBar_right_icon,1)

        val isShowDividerStyle = typeArray.getBoolean(R.styleable.CustomToolBar_is_show_divider,false)
        val backgroundStyle = typeArray.getColor(R.styleable.CustomToolBar_background_color,Color.parseColor("#ffffff"))
        val isShowBackIconStyle = typeArray.getBoolean(R.styleable.CustomToolBar_is_show_back_icon,true)
        typeArray.recycle()

        tvTitle.text = titleNameStyle
        tvTitle.setTextColor(titleColorStyle)
        ivBack.setImageResource(icBackStyle)
        setBackgroundColor(backgroundStyle)

        tvRightText.text = rightTextStyle
        tvRightText.setTextColor(rightTextColorStyle)
        /** 显示右侧icon*/
        if(rightIconStyle==1){
            ivRight.visibility= View.GONE
        }else{
            ivRight.setImageResource(rightIconStyle)
        }
        /** 底部分界线*/
        if(isShowDividerStyle){
            vDivider.visibility = View.VISIBLE
        }else{
            vDivider.visibility = View.GONE
        }

        if(isShowBackIconStyle){
            ivBack.visibility = View.VISIBLE
        }else{
            ivBack.visibility = View.INVISIBLE
        }


        /**
         *  填充状态栏高度
         *  1.如果为沉浸式状态栏，需要设置一个和系统状态栏相同高度的顶部View ,@isImmersionStatusBar 默认为ture
         *  2.如果为深色字体的系统状态栏（白色背景黑色字体）并且系统版本小于6.0时，需要设置topView为背景为灰色
         */
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT&&isImmersionStatusBar){
            val topView = find<View>(R.id.view_toolbar_top)
            val statusBarHeight = getStatusBarHeight()
            topView.layoutParams.height =statusBarHeight
            topView.visibility = View.VISIBLE
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M && isDarkStatusBar){
                topView.setBackgroundColor(resources.getColor(R.color.status_bar_color))  //兼容6.0以下的沉浸式状态栏样式
            }
        }
    }

    fun setTitleText(title:String){
        tvTitle.text = title
    }
    fun setRightText(rtext:String){
        tvRightText.text = rtext
    }
    fun setBackListener(listener: OnClickListener) {
        ivBack.setOnClickListener(listener)
    }
    fun setRightOptionListener(listener:OnClickListener){
        llRightContainer.setOnClickListener(listener )
    }
    fun invisibleRightIcon(){
        ivRight.visibility = View.GONE
    }

    fun getStatusBarHeight():Int{
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height","dimen","android")
        if(resourceId>0){
            statusBarHeight = resources.getDimensionPixelOffset(resourceId)
        }
        return statusBarHeight
    }


}