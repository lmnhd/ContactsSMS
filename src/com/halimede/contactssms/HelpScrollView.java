package com.halimede.contactssms;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class HelpScrollView extends ScrollView {

	public HelpScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}


	private float lastX;
	private float lastY;
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		
       
        case (MotionEvent.ACTION_MOVE) :
            
            return true;
        
        case (MotionEvent.ACTION_CANCEL) :
            
            return true;
        case (MotionEvent.ACTION_OUTSIDE) :
            
        	return true;
		case MotionEvent.ACTION_DOWN:
			lastX = event.getX();
			lastY =  event.getY();
			break;
			
		case MotionEvent.ACTION_UP:
		{
			float currentX = event.getX();
			float currentY = event.getY();
			
			float diffX = Math.abs(currentX - lastX);
			float diffY = Math.abs(currentY - lastY);
			
			
			
			if(diffX < diffY){
				return false;
			}else{
				return true;
			}
			
			
			
		}

		default:
			break;
		}
		return false;
	}

}
