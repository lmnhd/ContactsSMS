package com.halimede.contactssms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import com.halimede.contactssms.ViewDataProvider.LocalBinder;
import com.halimede.contactssms.dataModels.FetchModelImpl.ContactModel;
import com.halimede.contactssms.dataModels.FetchModelImpl.MessageModel;

import android.R.bool;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;

public class IncomingSMS extends BroadcastReceiver {
	final android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
	private ViewDataProvider mSmartView;
	private List<MessageModel> mMessages;
	private MessageModel mLastMessage;
	private boolean mMessageWaiting = false;
	private boolean mHasAPlusInFront = false;
//	public IncomingSMS(){
//		super();
//	}
	private ServiceConnection mSmartViewConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSmartView = null;
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mSmartView = ((LocalBinder) service).getService();
			
			if(mMessageWaiting){
				mSmartView.AddMessage(mLastMessage);
				try {
					mSmartView.sendNewMessages(mSmartView.getCurrentConnectionID(), null);
					mMessageWaiting = false;
				} catch (JSONException e) {
					if(STDirector.mSendErrorReports){
						mSmartView.sendReport(System.currentTimeMillis(),"onServiceConnected", e.getMessage(), true, false,
								ViewDataProvider.exceptionStacktraceToString(e), 
								mLastMessage.getAddress(), "HasPlusInFrontOfNumber : " +
								Boolean.toString(mHasAPlusInFront));
					}
					
				}
			}
		}
	};

	@Override
	public void onReceive(Context context, Intent intent) {

		final Bundle bundle = intent.getExtras();
		//final List<MessageModel> messages = new ArrayList<MessageModel>();
		mHasAPlusInFront = false;
		mMessages =  new ArrayList<MessageModel>();
		
		
		Intent smsIntent = new Intent(context,ViewDataProvider.class);
		context.bindService(smsIntent, mSmartViewConnection,Context.BIND_AUTO_CREATE);
		
		try{
			
			if(bundle != null){
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				
				for (int i = 0; i < pdusObj.length; i++){
					
					SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage.getDisplayOriginatingAddress();
					
					String senderNum = phoneNumber;
					
					if(senderNum.contains("+")){
						mHasAPlusInFront = true;
						String temp = senderNum.replace("+", "");
						senderNum = temp;
					}
					
					String message = currentMessage.getDisplayMessageBody();
					Long date = currentMessage.getTimestampMillis();
					SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss",Locale.getDefault());
					Date _date = new Date(date);
					//Calendar cal = Calendar.getInstance(Locale.US);
					//Long hour = (long) cal.get(Calendar.HOUR_OF_DAY);
					//String meridian = hour > 11 ? "PM" : "AM";
					String time = sdf.format(_date);
					
					
					MessageModel mod = new MessageModel(senderNum, message, time,date, "", "", "", "","","");
					
					mLastMessage = mod;
					if(mSmartView!=null){
						
						mSmartView.AddMessage(mod);
						if(mSmartView.GetMessages().size() > 0){
							
							try {
								mSmartView.sendNewMessages(mSmartView.getCurrentConnectionID(), null);
							} catch (JSONException e) {
								if(STDirector.mSendErrorReports){
									mSmartView.sendReport(System.currentTimeMillis(),"onReceive", e.getMessage(), true, false,
											ViewDataProvider.exceptionStacktraceToString(e), 
											senderNum, "HasPlusInFrontOfNumber : " +
											Boolean.toString(mHasAPlusInFront));
									e.printStackTrace();
								}
								
							}
						}
					}else{
						mMessageWaiting = true;
					}
					
				mMessages.add(mod);
					
					
					//mSmartView.AddMessage(mod);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			if(mSmartView != null){
				if(STDirector.mSendErrorReports){
					mSmartView.sendReport(System.currentTimeMillis(),"onReceive", e.getMessage(), true, false, ViewDataProvider.exceptionStacktraceToString(e), "", "");
				}
				
			}
		}
		
		
		
		

	}

}