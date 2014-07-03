package com.halimede.contactssms;



import com.halimede.contactssms.R;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class Help_Viewer extends SwipeActivity {

	private ViewFlipper mViewFlipper;
	
	private float lastX;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gsms_help_view);
		
		mViewFlipper = (ViewFlipper)findViewById(R.id.view_flipper);
		
//		ScrollView sv = (ScrollView)findViewById(R.id.scrollview);
		

//		TextView iv1 = (TextView)findViewById(R.id.imageView1);
//		iv1.setText(R.string.help_view_instructions);
		ImageView topView = (ImageView)findViewById(R.id.help_modes);
		topView.setImageBitmap(decodeSampleBitmapFromResource(getResources(), R.drawable.image1, 400, 400));
		
//		ImageView iv2 = (ImageView)findViewById(R.id.imageView2);
//		iv2.setImageBitmap(decodeSampleBitmapFromResource(getResources(), R.drawable.image1, 400, 400));
//		
//		ImageView iv3 = (ImageView)findViewById(R.id.imageView3);
//		iv3.setImageBitmap(decodeSampleBitmapFromResource(getResources(), R.drawable.symbols, 400, 400));
//		
//		ImageView iv4 = (ImageView)findViewById(R.id.imageView4);
//		iv4.setImageBitmap(decodeSampleBitmapFromResource(getResources(), R.drawable.numeric, 400, 400));
//		
//		ImageView iv5 = (ImageView)findViewById(R.id.imageView5);
//		iv5.setImageBitmap(decodeSampleBitmapFromResource(getResources(), R.drawable.alphanum, 400, 400));
//		
//		ImageView iv6 = (ImageView)findViewById(R.id.imageView6);
//		iv6.setImageBitmap(decodeSampleBitmapFromResource(getResources(), R.drawable.alpha, 400, 400));
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth,int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth) {
			
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		
		return inSampleSize;
	}
	
	public static Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId,options);
		
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}
	
	@Override
	public void onBackPressed() {
		
		Intent intent = new Intent("com.halimede.gearsms.ACTION_HELP_FINISHED");
		setResult(4567, intent);
		finish();
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			lastX = event.getX();
//			
//			break;
//			
//		case MotionEvent.ACTION_UP:
//		{
//			float currentX = event.getX();
//			
//			if(lastX < currentX){
//				if(mViewFlipper.getDisplayedChild() == 0)
//					break;
//				
//				mViewFlipper.setInAnimation(this,R.anim.in_from_left);
//				mViewFlipper.setOutAnimation(this,R.anim.out_to_right);
//				mViewFlipper.showNext();
//			}
//			
//			if (lastX > currentX) {
//				if(mViewFlipper.getDisplayedChild() == 1)
//					break;
//				
//				mViewFlipper.setInAnimation(this,R.anim.in_from_right);
//				mViewFlipper.setOutAnimation(this,R.anim.out_to_left);
//				mViewFlipper.showPrevious();
//			}
//			break;
//		}
//
//		default:
//			break;
//		}
//		return false;
//	}

	@Override
	protected void previous() {
		if(mViewFlipper.getDisplayedChild() == 1)
			return;
		
		mViewFlipper.setInAnimation(this,R.anim.in_from_right);
		mViewFlipper.setOutAnimation(this,R.anim.out_to_left);
		mViewFlipper.showPrevious();
		
	}

	@Override
	protected void next() {
		if(mViewFlipper.getDisplayedChild() == 0)
			return;
		
		mViewFlipper.setInAnimation(this,R.anim.in_from_left);
		mViewFlipper.setOutAnimation(this,R.anim.out_to_right);
		mViewFlipper.showNext();
		
	}
}
