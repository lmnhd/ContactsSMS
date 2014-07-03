package com.halimede.contactssms;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class Help_Instruction_Text extends TextView {
	
	private float lastX;
	private float lastY;
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = event.getX();
			//lastY =  event.getY();
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

	public Help_Instruction_Text(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Help_Instruction_Text(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Help_Instruction_Text(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	

}
