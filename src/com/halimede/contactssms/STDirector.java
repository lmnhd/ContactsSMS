package com.halimede.contactssms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.halimede.contactssms.ViewDataProvider;
import com.halimede.contactssms.ViewDataProvider.LocalBinder;
import com.halimede.contactssms.dataModels.FetchModelImpl.ContactModel;
import com.halimede.contactssms.dataModels.FetchModelImpl.MessageModel;
import com.halimede.contactssms.errorreporter.ErrorReport;
import com.halimede.contactssms.errorreporter.ErrorReport.DeviceInfoReportModel;
import com.halimede.contactssms.util.SystemUiHider;
import com.halimede.contactssms.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class STDirector extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = false;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private static final String TAG = "STDirector";
	private SystemUiHider mSystemUiHider;
	public static boolean mSendErrorReports = false;
	
	public static boolean MIN_MODE = true;
	private Context mCntx;
	private ViewDataProvider mSmartView;
	
	private CheckBox mCBSignature;
	
	private String mCustomSignature;
	private boolean mUseSignature;
	
	private long mRefreshDownTime = 0l;
	private long mClearMsgDownTime = 0l;
	private static STDirector mCurrent;
	public static boolean clickHelp=true;
	public static boolean refreshClicked=true;
	public static boolean signatureClicked=true;
	public static boolean clearClicked=true;
	private Button mClearMessagesBTN;
	private Button mRefreshMessagesBTN;
	private Button mHelpButton;
	private ImageView mQuickHelp;
	private TextView mFullScreenContent;
	private TextView mInfoText;
	private TextView mSpacer;
	//public static boolean clickHelp=true;
	 public static long lastServerCheck = 0;
	 
	 public static long currentErrorReportStamp = 0;
	
	private ServiceConnection mSmartViewConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "STYTI service connection lost");
			mSmartView = null;
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "STYTI service connected!!!");
			mSmartView = ((LocalBinder) service).getService();
			mSmartView.SetUseSignature(mUseSignature);
			mSmartView.SetCustomSignature(mCustomSignature);
			mSmartView.SetDirector(STDirector.this);
			
			
			
			
		}
	};
//	public String GetCustomSignature(){
//		Editable result = mSigEditText.getText();
//		String res = result.toString();
//		return res;
//	}
	public boolean UseSignature(){
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		
		
		mUseSignature = prefs.getBoolean("usesig", mCBSignature.isChecked()); 
		
		
		return mUseSignature;
	}
	public String GetSignature(){
		
		return persistSignature();
	}
	

public void sendReport(long timeStamp,String functionScope,String message, Boolean isError,Boolean isReply,String stackTrack,String number,String... extras){
    	
	if(currentErrorReportStamp == timeStamp){
		return;
	}
	currentErrorReportStamp = timeStamp;
	try{
		List<String> currentProperties = new ArrayList<String>();
    	//currentProperties.add("CHANNEL_ID : " + Integer.toString(CHANNEL_ID));
    	currentProperties.add("mCustomSignature : " + mCustomSignature != null ? mCustomSignature : "null");
    	//currentProperties.add("mNewMessages.size() : " + mNewMessages != null ? Integer.toString(mNewMessages.size()) : "null");
    	currentProperties.add("useSignature : " + Boolean.toString(mUseSignature));
    	currentProperties.add("sendingnumber : " + number != null ? number : "no-value");
    	currentProperties.add("isreply : " + Boolean.toString(isReply));
    	
    	currentProperties.addAll(Arrays.asList(extras));
    	
    	
    	ErrorReport.DeviceInfoReportModel model = new DeviceInfoReportModel(functionScope, isError, message, stackTrack, currentProperties.toArray(new String[0]));
    	
    	new ErrorReport().execute(model);
	}catch(Exception e){
		
	}
    	
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data.getAction().equals("com.halimede.gearsms.ACTION_HELP_FINISHED")){
			clickHelp = true;
		}
		
	}
	
	public void handleServerGlobalsResponse(ServerGlobalsResponse obj) throws JSONException{
		String generalMessage = obj.getGeneralMessage();
		String minModeMessage = obj.getMinModeMessage();
		String fullModeMessage = obj.getFullModeMessage();
		Boolean enableMinModeNow = obj.getEnableMinMode();
		Boolean disableMinModeNow = obj.getDisableMinMode();
		
		
		if(enableMinModeNow){
			persistMinMode(1);
		}else if(disableMinModeNow){
			persistMinMode(2);
		}
		
		
		
		if(generalMessage != ""){
			showMessage(generalMessage);
		}
		if(MIN_MODE){
			if(minModeMessage != ""){
				showMessage(minModeMessage);
			}
		}else{
			if(fullModeMessage != ""){
				showMessage(fullModeMessage);
			}
		}
	}
	
	private void updateMinMode(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(!MIN_MODE){
					
					mRefreshMessagesBTN.setVisibility(View.VISIBLE);
					mClearMessagesBTN.setVisibility(View.VISIBLE);
					mHelpButton.setVisibility(View.VISIBLE);
					mQuickHelp.setVisibility(View.GONE);
					mFullScreenContent.setVisibility(View.VISIBLE);
					
					
				}else{
					
					mRefreshMessagesBTN.setVisibility(View.GONE);
					mClearMessagesBTN.setVisibility(View.GONE);
					mHelpButton.setVisibility(View.GONE);
					mQuickHelp.setVisibility(View.VISIBLE);
					mFullScreenContent.setVisibility(View.GONE);
					
					mInfoText.setText(R.string.disclaimer);
				}
				
			}
		});
		
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		try{
			
			super.onCreate(savedInstanceState);
			
			mCurrent = this;

			setContentView(R.layout.activity_gsmssmart_view);

			final View controlsView = findViewById(R.id.fullscreen_content_controls);
			final View contentView = findViewById(R.id.fullscreen_content);
			
			final EditText mSigEditText = (EditText)findViewById(R.id.customSignature);
			SharedPreferences settings = getPreferences(MODE_PRIVATE);
			
			
			mCBSignature = (CheckBox)findViewById(R.id.use_sig);
			mCustomSignature = settings.getString("signature", mSigEditText.getText().toString());
			mSigEditText.setText(mCustomSignature);
			
			mUseSignature = settings.getBoolean("usesig", mCBSignature.isChecked());
			mCBSignature.setChecked(mUseSignature);
			
			mRefreshMessagesBTN = (Button) findViewById(R.id.refresh_messages);
			mClearMessagesBTN = (Button) findViewById(R.id.clear_messages);
			mHelpButton = (Button) findViewById(R.id.option_button_1);
			mQuickHelp = (ImageView) findViewById(R.id.quick_help);
			//mSpacer = (TextView) findViewById(R.id.extra_space);
			mInfoText = (TextView) findViewById(R.id.info_display);
			
			
			//mQuickHelp.setImageBitmap(Help_Viewer.decodeSampleBitmapFromResource(getResources(), R.drawable.image2, 300, 300));
			mFullScreenContent = (TextView) findViewById(R.id.fullscreen_content);
			MIN_MODE = settings.getBoolean("minmode", MIN_MODE);
			
			updateMinMode();

			// Set up an instance of SystemUiHider to control the system UI for
			// this activity.
			mSystemUiHider = SystemUiHider.getInstance(this, contentView,
					HIDER_FLAGS);
			mSystemUiHider.setup();
//			mSystemUiHider
//					.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
//						// Cached values.
//						int mControlsHeight;
//						int mShortAnimTime;
//
//						@Override
//						@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//						public void onVisibilityChange(boolean visible) {
//							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//								// If the ViewPropertyAnimator API is available
//								// (Honeycomb MR2 and later), use it to animate the
//								// in-layout UI controls at the bottom of the
//								// screen.
//								if (mControlsHeight == 0) {
//									mControlsHeight = controlsView.getHeight();
//								}
//								if (mShortAnimTime == 0) {
//									mShortAnimTime = getResources().getInteger(
//											android.R.integer.config_shortAnimTime);
//								}
//								controlsView
//										.animate()
//										.translationY(visible ? 0 : mControlsHeight)
//										.setDuration(mShortAnimTime);
//							} else {
//								// If the ViewPropertyAnimator APIs aren't
//								// available, simply show or hide the in-layout UI
//								// controls.
//								controlsView.setVisibility(visible ? View.VISIBLE
//										: View.GONE);
//							}
//
////							if (visible && AUTO_HIDE) {
////								// Schedule a hide().
////								//delayedHide(AUTO_HIDE_DELAY_MILLIS);
////							}
//						}
//					});
			
			

			// Set up the user interaction to manually show or hide the system UI.
			contentView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (TOGGLE_ON_CLICK) {
						//mSystemUiHider.toggle();
					} else {
						mSystemUiHider.show();
					}
				}
			});

			// Upon interacting with UI controls, delay any scheduled hide()
			// operations to prevent the jarring behavior of controls going away
			// while interacting with the UI.
			
			
			mHelpButton.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(clickHelp){
						
						try{
							Intent intent = new Intent(getApplicationContext(),Help_Viewer.class);
							startActivityForResult(intent,4567);
						}catch(Exception e){
							
							if(STDirector.mSendErrorReports){
								sendReport(System.currentTimeMillis(), "startHelpHandler",
										e.getMessage(), true, false,
										ViewDataProvider.exceptionStacktraceToString(e), "", "");
							}
							
						}
						
						
					}
					clickHelp = false;
					
					
					
					
					return true;
				}
			});
//			
			mCBSignature.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					try{
						
						mUseSignature = isChecked;
						SharedPreferences settings = getPreferences(MODE_PRIVATE);
						settings.edit().putBoolean("usesig",mUseSignature).commit();
						if(mSmartView != null){
							mSmartView.SetUseSignature(mUseSignature);
						}
						
					}catch(Exception e){
						if(STDirector.mSendErrorReports){
							sendReport(System.currentTimeMillis(), "onCheckedChanged",
									e.getMessage(), true, false,
									ViewDataProvider.exceptionStacktraceToString(e), "", "");
							
						}
						
					}
					
					
				}
				
			});
			mSigEditText.setOnEditorActionListener((new OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					try{
						if(actionId == 0){
							persistSignature();
						}
							
					}catch(Exception e){
						if(STDirector.mSendErrorReports){
							sendReport(System.currentTimeMillis(),"editSignature", e.getMessage(),
									true, false, ViewDataProvider.exceptionStacktraceToString(e), "", "");
						}
						
					}
					
					
					return true;
				}
			}));
			
			
			
			mRefreshMessagesBTN.setOnTouchListener(new OnTouchListener() {
//				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					try{
						if(event.getDownTime() != mRefreshDownTime){
							mRefreshDownTime = event.getDownTime();
							//sendDummyTexts();
							try {
								mSmartView.sendNewMessages(mSmartView.getCurrentConnectionID(),null);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}catch(Exception e){
				if(STDirector.mSendErrorReports){
	               sendReport(System.currentTimeMillis(), "refreshMessagesHandler", e.getMessage(), true, false, ViewDataProvider.exceptionStacktraceToString(e), "", "");
				}
				

					}
					
					return true;
				}
			});
			mClearMessagesBTN.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					try{
						
						if(event.getDownTime() != mClearMsgDownTime){
							mClearMsgDownTime = event.getDownTime();
							mSmartView.clearNewMessages(mSmartView.getCurrentConnectionID());
							
						}
					}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(), "clearMessagesOnTouchHandler",
				e.getMessage(), true, false,
				ViewDataProvider.exceptionStacktraceToString(e), "", "");

			}
						
					}
					
					
					return true;
				}
			});
			
			
			
//			mSigEditText.setOnTouchListener( new OnTouchListener() {
//				
//				@Override
//		        public boolean onTouch(View v, MotionEvent event) {
//					mSigEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
	//
//		                @Override
//		                public void onFocusChange(View v, boolean hasFocus) {
//		                    if(!hasFocus){
//		                    	if(mSmartView != null){
//		                    		EditText text = (EditText)v;
//		                    		String result = text.getText().toString();
//		        					mSmartView.SetCustomSignature(result);
//		        				}
//		                    }
//		                }
//		            });
//					return true;
//		        }
//			});
			
			
			UseSignature();
			mCntx = getApplicationContext();
			
			mCntx.bindService(new Intent(mCntx,ViewDataProvider.class), mSmartViewConnection, Context.BIND_AUTO_CREATE);
			
			if(isOnline()){
				ServerGlobalsResponse obj = new ShouldSendErrorReports().execute().get();
				if(obj != null){
					mSendErrorReports = obj.getShouldReport();
					
					handleServerGlobalsResponse(obj);
				}
			}else{
				mSendErrorReports = false;
			}
			
			
			
			
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				if(e.getMessage() != null && e.getStackTrace() != null){
					
					sendReport(System.currentTimeMillis(),"STDirectorOnCreate", e.getMessage(), true, false, ViewDataProvider.exceptionStacktraceToString(e), "", "");
				
				
			}else{
				if(e.getMessage() == null){
					sendReport(System.currentTimeMillis(),"STDirectorOnCreate", "Error message is nill", true, false, ViewDataProvider.exceptionStacktraceToString(e), "", "");
				}else if(e.getStackTrace() == null){
					sendReport(System.currentTimeMillis(),"STDirectorOnCreate", e.getMessage(), true, false, "", "", "");
				}
				
			}
			
			}
			
		}
		
	}
	public void showMessage(final String message){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mInfoText.setText(message);
				
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		try{
			
				persistSignature();
		
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"onDestroypersistsignature", e.getMessage(),
						true, false, ViewDataProvider.exceptionStacktraceToString(e), "", "");
			}
			
		}
		super.onDestroy();
	}
	public boolean persistMinMode(int mode){
		
		if(mode > 0){
			if(mode == 1){
				MIN_MODE = true;
			}else if(mode == 2){
				MIN_MODE = false;
			}
		}else{
			MIN_MODE = !MIN_MODE;
		}
		
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		settings.edit().putBoolean("minmode", MIN_MODE).commit();
		updateMinMode();
		if(mSmartView != null){
			mSmartView.toggleMinMode(mSmartView.getCurrentConnectionID(), MIN_MODE);
		}
		return MIN_MODE;
	}
	private String persistSignature(){
		try{
			EditText et = (EditText) findViewById(R.id.customSignature);
			String result = et.getText().toString();
			mCustomSignature = result;
			
			SharedPreferences settings = getPreferences(MODE_PRIVATE);
			settings.edit().putString("signature", result).commit();
			
			if(mSmartView != null){
				mSmartView.SetCustomSignature(result);
			}
			return result;
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"persistSignature",
						e.getMessage(), true, false,
						ViewDataProvider.exceptionStacktraceToString(e), "", "");
			}
		
		}
		
		return "";
		
		
	}
	
	public  void sendDummyTexts(){
		List<MessageModel> messages = new ArrayList<MessageModel>();
		SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss",Locale.US);
		Long date = System.currentTimeMillis();
		Date _date = new Date(date);
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		Long hour = (long) cal.get(Calendar.HOUR_OF_DAY);
		String meridian = hour > 11 ? "PM" : "AM";
		String time = sdf.format(_date) + meridian;
		String name = "";
		ContactModel contact = mSmartView.MatchContact(new MessageModel("+14074864955", "", "",date, "", "", "", "", "",""));
		
		String photo = "";
		if(contact != null){
			photo = contact.getPhotoURI();
			name = contact.getName();
		}
		MessageModel msg = new MessageModel("19195551212", Long.toString(System.currentTimeMillis()), time, date, "", "", "", "",photo, name);
		//messages.add(new MessageModel("15552573090", "Hello", time,date, "", "", "", "",photo,name));
		messages.add(msg);
		//messages.add(new MessageModel("15554760098", "Please return my call!", time,date, "", "", "", "","",name));
		//messages.add(new MessageModel("+ 15556678921", "Hello", time,date, "", "", "", "",photo,name));
		try {
			int conID = mSmartView.getCurrentConnectionID();
			mSmartView.AddMessage(msg);
			mSmartView.sendNewMessages(conID, null);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		//delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
//	private Runnable updateTimerThread = new Runnable() {
//		public void run() {
//			
//		}
//	}
	public class ServerGlobalsResponse {
		private String generalMessage;
		private String minModeMessage;
		private String fullModeMessage;
		private Boolean enableMinModeNow;
		private Boolean disableMinModeNow;
		private Boolean shouldReport;
		
		public String getGeneralMessage(){
			return generalMessage;
		}
		public void setGeneralMessage(String generalMessage){
			this.generalMessage = generalMessage;
		}
		public String getMinModeMessage(){
			return minModeMessage;
		}
		public void setMinModeMessage(String minModeMessage){
			this.minModeMessage = minModeMessage;
		}
		public String getFullModeMessage(){
			return fullModeMessage;
		}
		public void setFullModeMessage(String fullModeMessage){
			this.fullModeMessage = fullModeMessage;
		}
		public Boolean getDisableMinMode(){
			return disableMinModeNow;
		}
		public void setDisableMinMode(Boolean disableMinModeNow){
			this.disableMinModeNow = disableMinModeNow;
		}
		public Boolean getEnableMinMode(){
			return enableMinModeNow;
		}
		public void setShouldReport(Boolean shouldReport){
			this.shouldReport = shouldReport;
		}
		public Boolean getShouldReport(){
			return shouldReport;
		}
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	public static class ShouldSendErrorReports extends AsyncTask<Void, Void, ServerGlobalsResponse> {
		
		
		
		

		@Override
		protected ServerGlobalsResponse doInBackground(Void... params) {
			
			
			
			try {
				
				
				
				lastServerCheck = System.currentTimeMillis();
				//String url = "http://192.168.1.105:60139/api/ArtistServices/DeviceShouldReport";
				String url = "http://untamemusic.com/untame14/api/ArtistServices/DeviceShouldReport";
				String groupName = "android_gearsms_v1";
				
				String responseString = null;
				String props = "serial,release,model,host,id,board,bootloader,brand";
				String vals = Build.SERIAL + "," + Build.VERSION.RELEASE + "," +
						Build.MODEL + "," + Build.HOST + "," +
						Build.ID + "," + Build.BOARD + "," + Build.BOOTLOADER + "," +
						Build.BRAND;
				url = url.concat("?groupName=" + groupName + "&propertyToCheck=" + props
						+ "&valueToLookFor=" + vals);
				HttpClient httpclient = new DefaultHttpClient();
				HttpParams parms = httpclient.getParams();
				parms.setParameter("groupName", groupName);
				parms.setParameter("propertyToCheck", props);
				parms.setParameter("valueToLookFor", vals);
				
				HttpResponse resp = httpclient.execute(new HttpGet(url));
				
				StatusLine stat = resp.getStatusLine();
				
				if(stat.getStatusCode() == HttpStatus.SC_OK){
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					resp.getEntity().writeTo(out);
					out.close();
					//responseString = out.toString();
					//responseString = responseString.replace("\"{", "{").replace("}\"", "}");
					//responseString = responseString.substring(1, responseString.length() - 2);
					
					String respoString = out.toString().replace("\\", "");
    	    		if(respoString.startsWith("\"")){
    	    			respoString = respoString.substring(1, respoString.length() - 1);
    	    		}
    	    		if(respoString.endsWith("\"")){
    	    			respoString = respoString.substring(0,respoString.length() - 2);
    	    		}
					
			
					//JSONObject json = new JSONObject(respoString);
					Gson gson = new Gson();
					ServerGlobalsResponse sgr = gson.fromJson(respoString, ServerGlobalsResponse.class);
					
					if(sgr != null){
						return sgr;
					}
					//Boolean temp = Boolean.parseBoolean(responseString);
					return null;
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			
			return null;
		}
		
//		@Override
//		protected void onPostExecute(JSONObject result) {
//			
//			super.onPostExecute(result);
//			
//			try {
//				if(result != null) {
//					STDirector.mSendErrorReports = result.getBoolean("shouldReport");
//				}
//				
//				
//			} catch (JSONException e) {
//				
//				e.printStackTrace();
//			}
//			
//		}
	}
}
