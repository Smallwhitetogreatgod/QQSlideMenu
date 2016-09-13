package com.nb.qqslidemenu;

import com.nb.qqslidemenu.SlideMenu.DragState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/*
 * 当slideMenu打开的时候，拦截并消费触摸事件
 * */
public class MyLinearLayout extends LinearLayout{

	

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context) {
		super(context);
	}
	
	private SlideMenu slideMenu;
	public void setSlideMenu(SlideMenu slideMenu){
		this.slideMenu=slideMenu;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(slideMenu!=null&&slideMenu.getState()==DragState.Open){
			
			
			
			//如果slideMenu打开应当拦截并消费事件
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}
	long currentMS=0;
	float DownX=-1;
	float DownY=-1;
	
	float moveX = 0,moveY=0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(slideMenu!=null && slideMenu.getState()==DragState.Open){
			System.out.println("  onTouchEvent");
			this.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(slideMenu.getState()==DragState.Open){
						slideMenu.close();
					}
				}
			});
//			long time=0;
//			if(event.getAction()==MotionEvent.ACTION_DOWN){
//				 time=System.currentTimeMillis();
//			}
//			
//			if(event.getAction()==MotionEvent.ACTION_UP){
//				long time2=System.currentTimeMillis();
//				//抬起则应该关闭slideMenu
//				if((time2-time)<100){
//					System.out.println(time2-time);
//					slideMenu.close();
//				}
//				
//			}
			
			switch(event.getAction()){
				 case MotionEvent.ACTION_DOWN:
					
					 currentMS=0;
						 DownX=-1;
						 DownY=-1;
						
					 moveX = 0;
					 moveY=0;
					 DownX = event.getX();//float DownX
                     DownY = event.getY();//float DownY
                    
                     currentMS = System.currentTimeMillis();//long currentMS     获取系统时间
					 break;
				 case MotionEvent.ACTION_MOVE:
					
					
                   
                     moveX += Math.abs(event.getX() - DownX);//X轴距离
                     moveY += Math.abs(event.getY() - DownY);//y轴距离
                     DownX = event.getX();
                     DownY = event.getY();
					 break;
				 case MotionEvent.ACTION_UP:
					if(moveX<=200&&moveY<=200){
						slideMenu.close();
						
					}
				
					 break;
				
			}
			
			//如果slideMenu打开则应该拦截并消费掉事件
			return true;
		}
		return super.onTouchEvent(event);
	}
}
