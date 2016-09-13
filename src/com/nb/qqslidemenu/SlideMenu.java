package com.nb.qqslidemenu;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


public class SlideMenu extends FrameLayout{

	private View menuView;//菜单view
	private View mainView;//主界面view
	private int width;
	private float dragRange;//拖拽范围
	
	private ViewDragHelper viewDragHelper;

	private FloatEvaluator floatEvaluator;
	
	private IntEvaluator intEvaluator;

	
	public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideMenu(Context context) {
		super(context);
		init();
	}
	
	//定义状态常量
	enum DragState{
		Open ,Close;
	}
	
	private DragState currentState=DragState.Close;;
	
	private void init(){
		viewDragHelper=ViewDragHelper.create(this, callback);
		floatEvaluator=new FloatEvaluator();
		intEvaluator=new IntEvaluator();
	}
	
	/*
	 * 获取当前状态
	 * */
	public DragState getState(){
		return currentState;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//简单的异常处理
		if(getChildCount()!=2){
			throw new IllegalArgumentException("SlideMenu only hava 2 child !");
		}
		menuView = getChildAt(0);
		mainView = getChildAt(1);
	}
	
	/*
	 * 该方法在onMeasure执行完之后执行，可以在该方法中初始化宽高
	 * */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width=getMeasuredWidth();
		dragRange=width*0.6f;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		return true;//自己处理事件，return true
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return viewDragHelper.shouldInterceptTouchEvent(ev);
		
	}
	
	private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {
		
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child==menuView||child==mainView;
		}
		
		public int getViewHorizontalDragRange(View child) {
			return (int)dragRange;
			
		}
		
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(child==mainView){
				if(left<0)left=0;//限制左边
				if(left>dragRange)left=(int)dragRange;//限制右边
			}
			
				
			
			return left;
		}
		
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			if(changedView==menuView){
				//固定住menuView
				menuView.layout(0,0, menuView.getMeasuredWidth(),menuView.getMeasuredHeight());	
				//mainView动起来
				int newLeft=mainView.getLeft()+dx;
				if(newLeft<0)newLeft=0;
				if(newLeft>dragRange)newLeft=(int)dragRange;
				mainView.layout(newLeft, mainView.getTop()+dy, newLeft+mainView.getMeasuredWidth()
						, mainView.getBottom()+dy);
			}
		//1.计算滑动的百分比
			float fraction=mainView.getLeft()/dragRange;
		//2.执行伴随动画
			executeAnim(fraction);
			System.out.println(fraction);
			System.out.println(currentState);
		//3.更改状态，回调方法
			
			if(fraction<=0.1f&&currentState!=DragState.Close){
			//更改状态为关闭，并回调关闭的方法
				
				//更改状态为关闭，并回调关闭的方法
				System.out.println("onViewPositionChanged");
				
				
				if(listener!=null){
					listener.onClose();
				}
				currentState=DragState.Close;
			}else if(fraction>0.95f&&currentState!=DragState.Open){
				System.out.println(currentState+"=====");
				currentState=DragState.Open;
				if(listener!=null){
					listener.onOpen();
				}
			}
			//将drag的fraction暴露给外界
			if(listener!=null){
				listener.onDraging(fraction);
			}
		}
		
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if(mainView.getLeft()<dragRange/2){
				//左边
				if(xvel>200&&currentState!=DragState.Open){
					open();
					
					
				}else{
					System.out.println(currentState+"onViewReleased");
					close();
					

				}
				
				
			}else{
				if(xvel<-200&&currentState!=DragState.Close){
					close();
					
				}else{
					open();
				}
				//在右边
				
				
			}
			
			
			//处理用户的稍微滑动, 正值向右
//			if(xvel>200&&currentState!=DragState.Open){
//				open();
//				return ;
//			}else if(xvel<-200&&currentState!=DragState.Close){
//				SlideMenu.this.close();
//				return ;
//			}
		}
		
	};
	
	//关闭slideMenu
	public void close() {
		if(listener!=null){
			listener.onClose();
		}
		currentState=DragState.Close;
		viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}
	
	//打开slideMenu
	public void open() {
		listener.onOpen();
		    currentState=DragState.Open;
			viewDragHelper.smoothSlideViewTo(mainView, (int)dragRange, mainView.getTop());
			ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		}
	
	
	/*
	 * 执行伴随动画
	 */
	private void executeAnim(float fraction){
		//缩小mainview
		
//		float scaleValue=0.8f+0.2f*(1-fraction);
		ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
		ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
	//移动menuview
		ViewHelper.setTranslationX(menuView, intEvaluator.evaluate(fraction, -menuView.getMeasuredWidth()/2, 0));
		ViewHelper.setScaleX(menuView, floatEvaluator.evaluate(fraction, 0.6f, 1f));
		ViewHelper.setScaleY(menuView, floatEvaluator.evaluate(fraction, 0.6f, 1f));
		ViewHelper.setAlpha(menuView, floatEvaluator.evaluate(fraction, 0.3f, 1f));

		getBackground().setColorFilter((int) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
	}
	
	public void computeScroll() {
		if(viewDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		}
	};
	private OnDragStateChangeListener listener;
	
	public void setOnDragStateChangeListerer(OnDragStateChangeListener listener){
		this.listener=listener;
	}
	
	public interface  OnDragStateChangeListener{
		//打开的回调
		void onOpen();
		//关闭的回调
		void onClose();
		//拖拽中的回调
		void onDraging(float fraction);
	}
}
