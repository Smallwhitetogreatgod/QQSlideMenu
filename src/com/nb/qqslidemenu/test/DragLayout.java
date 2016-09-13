package com.nb.qqslidemenu.test;


import javax.security.auth.callback.Callback;

import com.nb.qqslidemenu.ColorUtil;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class DragLayout  extends FrameLayout{
	private View redView;
	private View blueView;
	
	private ViewDragHelper viewDragHelper;
	//alt+shift+s->(然后恩)c复写构造方法；	
	
	private Scroller scroller;
	public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public DragLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DragLayout(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		viewDragHelper=ViewDragHelper.create(this, callback);
		scroller=new Scroller(getContext());
	}
	/**
	 * 当DragLayout的xml布局的结束标签读取完成时，会执行该方法，此时会会知道自己有几个子view
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	    redView = getChildAt(0);
	    blueView = getChildAt(1);
	}
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		//要测量自己的子view
////		int size=getResources().getDimension(R.dimen.width);
////		int measureSpec=MeasureSpec.makeMeasureSpec(redView.getLayoutParams().width, MeasureSpec.EXACTLY);//精确
////		redView.measure(measureSpec, measureSpec);
////		blueView.measure(measureSpec, measureSpec);
//		//如果说没有特殊的对子view的测量需求
//		measureChild(redView, widthMeasureSpec, heightMeasureSpec);
//		measureChild(blueView, widthMeasureSpec, heightMeasureSpec);
//
//	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int left=getPaddingLeft();
		int top=getPaddingTop();
		redView.layout(left, top,left+redView.getMeasuredWidth(), top+redView.getMeasuredHeight());
		blueView.layout(left, redView.getBottom(),left+redView.getMeasuredWidth(), redView.getBottom()+redView.getMeasuredHeight());

	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 让ViewDragHelpter帮我们判断是否应该拦截
		boolean result=viewDragHelper.shouldInterceptTouchEvent(ev);
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//将触摸时间交给ViewDragHelper来解析处理
		viewDragHelper.processTouchEvent(event);
		return true;
	}
	
	
	private ViewDragHelper.Callback callback=new  ViewDragHelper.Callback() {
	
		
		/* @return: boolean 用于判断是否捕获当前child的触摸事件 child 当前触摸的子view */
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child==blueView||child==redView;
		}
	
		/*当view被开始捕获的解析的回调     capturedChild当前别捕获的view		 */
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			super.onViewCaptured(capturedChild, activePointerId);
		}
		/*获取view水平方向的拖拽
		  *但是目前不能限制边界。返回的值目前用在手指抬起的时候view缓慢移动的动画事件的计算上面
		 * 最好不要返回零
		 * */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return getMeasuredWidth()-child.getMeasuredWidth();
		}
		
		/*获取view垂直方向的拖拽 ： 
		 * 但是目前不能限制边界。返回的值目前用在手指抬起的时候view缓慢移动的动画事件的计算上面
		 * 最好不要返回零
		 * */
		public int getViewVerticalDragRange(View child) {
			return getMeasuredHeight()-child.getMeasuredHeight();
			
		};
		/*控制child在水平方向上的移动
		 * left:表示ViewDragHelper认为你想然当前child的left改变的数值，left=child.getLeft()+dx;
		 * dx:本次child水平方向移动的距离
		 * return : 表示你想让child left变成的值。
		 * */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(left<0){
				left=0;//限制左边距
			}else if(left>(getMeasuredWidth()-child.getMeasuredWidth())){
				left=getMeasuredWidth()-child.getMeasuredWidth();
			}
			return left;
		}
		
		
		/*控制child在垂直方向上的移动
		 * top:表示ViewDragHelper认为你想然当前child的top改变的数值，top=child.getLeft()+dy;
		 * dx:本次child水平方向移动的距离
		 * return : 表示你想让child top变成的值。
		 * */
		public int clampViewPositionVertical(View child, int top, int dy) {
			if(top<0){
				top=0;
			}else if(top>(getMeasuredHeight()-child.getMeasuredHeight())){
				top=getMeasuredHeight()-child.getMeasuredHeight();
			}
			return top;
		};
		
		/*child 位置改变的时候执行， 
		 * changedView  位置改变的child
		 * left： child最新的left
		 * dx： 本次移动的水平距离
		 * */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if(changedView==blueView){//blueView移动的时候需要让redView跟随移动
		      redView.layout(redView.getLeft()+dx, redView.getTop()+dy, redView.getRight()+dx,
		    		redView.getBottom()+dy);
			}else if(changedView==redView){
				blueView.layout(blueView.getLeft()+dx, blueView.getTop()+dy, blueView.getRight()+dx,
						blueView.getBottom()+dy);
			}
			//1、计算view移动的百分比
			float fraction=changedView.getLeft()*1f/(getMeasuredWidth()-changedView.getMeasuredWidth());
			//2。执行一系列的伴随动画
			executeAnim(fraction);
		}
	
		/**
		 * 手指抬起时，执行该方法
		 * releasedChild：当前抬起的view
		 * xvel：x方向的移动速度 正：向右移动  负：向左移动
		 * yvel：y方向的移动速度
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			int centerLeft=getMeasuredWidth()/2-releasedChild.getMeasuredWidth()/2;
			if(releasedChild.getLeft()<centerLeft){
				//在左半边，应该向左半边移动
//				scroller.startScroll(startX, startY, dx, dy);
//				invalidate();
				viewDragHelper.smoothSlideViewTo(releasedChild, 0, releasedChild.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			}else{
				viewDragHelper.smoothSlideViewTo(releasedChild, getMeasuredWidth()-releasedChild.getMeasuredWidth(), releasedChild.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			}
			
		}

		

		
		
	};
	/**
	 * 执行伴随动画
	 */
	private void executeAnim(float fraction){
		//缩放
//		ViewHelper.setScaleX(redView,1+0.5f*fraction);
//		ViewHelper.setScaleY(redView,1+0.5f*fraction);
//旋转	
//		ViewHelper.setRotation(view, rotation);
		ViewHelper.setRotationX(redView, 360*fraction);
		ViewHelper.setRotationX(blueView, 360*fraction);
//		//平移
//		ViewHelper.setTranslationX(redView, 80*fraction);
//		//透明
//		ViewHelper.setAlpha(redView, 1-fraction);
	//设置过渡颜色的渐变	
		redView.setBackgroundColor((Integer)ColorUtil.evaluateColor(fraction, Color.RED,Color.GREEN));
	}
	public void computeScroll() {
//		if(scroller.computeScrollOffset()){
//			scrollTo(scroller.getCurrX(), scroller.getCurrY());
//			invalidate();
//		}
		if(viewDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(DragLayout.this);
		}
	};

}
