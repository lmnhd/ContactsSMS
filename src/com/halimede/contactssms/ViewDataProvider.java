package com.halimede.contactssms;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.SmsManager;


import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;







import com.halimede.contactssms.STDirector.ServerGlobalsResponse;
import com.halimede.contactssms.STDirector.ShouldSendErrorReports;
import com.halimede.contactssms.dataModels.*;
import com.halimede.contactssms.dataModels.FetchModelImpl.ContactModel;
import com.halimede.contactssms.dataModels.FetchModelImpl.ContactsInfoResp;
import com.halimede.contactssms.dataModels.FetchModelImpl.FromClientTextMsgResp;
import com.halimede.contactssms.dataModels.FetchModelImpl.IncomingMessageResp;
import com.halimede.contactssms.dataModels.FetchModelImpl.MessageModel;
import com.halimede.contactssms.dataModels.FetchModelImpl.SentMessageResult;
import com.halimede.contactssms.dataModels.FetchModelImpl.ToggleMinModeOBJ;
import com.halimede.contactssms.errorreporter.ErrorReport;
import com.halimede.contactssms.errorreporter.ErrorReport.DeviceInfoReportModel;
import com.halimede.contactssms.R;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;



public class ViewDataProvider extends SAAgent {

	public static final String TAG = "ViewDataProviderService";
	public static boolean SENDING_MESSAGE = false;
	public static final int CHANNEL_ID = 105;
	
	HashMap<Integer, ViewDataProviderConnection> mConnectionsMap = null;
	
	public static final String ACTION_ADD_DEVICE = "android.appcessory.device.ADD_DEVICE";
    private boolean useSignature = true;
    private SA mAccessory;
    private int mCurrentConnectionID;
    private List<ContactModel> mContacts;
    private List<MessageModel> mNewMessages;
    private String mCustomSignature;
    private String mCurrentReportMessage = "";
    
    private STDirector mDirector;

    private IncomingSMS smsReciever;
    private final IBinder mBinder = new LocalBinder();
   
    public int getCurrentConnectionID(){
    	return mCurrentConnectionID;
    }

    
    public class LocalBinder extends Binder {
    	public ViewDataProvider getService() {
    		return ViewDataProvider.this;
    	}
    
    }
    public void SetDirector(STDirector dir){
    	mDirector = dir;
    }
    public static String exceptionStacktraceToString(Exception e)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        ps.close();
        return baos.toString();
    }
    public void sendReport(long timeStamp, String functionScope,String message, Boolean isError,Boolean isReply,String stackTrack,String number,String... extras){
    	if(STDirector.currentErrorReportStamp == timeStamp){
    		return;
    	}
    	if(message == mCurrentReportMessage){
    		return;
    	}
    	mCurrentReportMessage = message;
    	STDirector.currentErrorReportStamp = timeStamp;
    	try{
    		List<String> currentProperties = new ArrayList<String>();
        	currentProperties.add("CHANNEL_ID : " + Integer.toString(CHANNEL_ID));
        	currentProperties.add("mCustomSignature : " + mCustomSignature != null ? mCustomSignature : "null");
        	currentProperties.add("mNewMessages.size() : " + mNewMessages != null ? Integer.toString(mNewMessages.size()) : "null");
        	currentProperties.add("useSignature : " + Boolean.toString(useSignature));
        	currentProperties.add("sendingnumber : " + number != null ? number : "no-value");
        	currentProperties.add("isreply : " + Boolean.toString(isReply));
        	
        	currentProperties.addAll(Arrays.asList(extras));
        	
        	
        	ErrorReport.DeviceInfoReportModel model = new DeviceInfoReportModel(functionScope, isError, message, stackTrack, currentProperties.toArray(new String[0]));
        	
        	new ErrorReport().execute(model);
    	}catch(Exception e){
    		
    	}
    	
    }
    
	@Override
	protected void onFindPeerAgentResponse(SAPeerAgent arg0, int arg1) {
		Log.d(TAG, "onFindPeerAgentResponse  arg1 =" + arg1);

	}
	
	
	
//	public List<MessageModel> fetchInbox() {
//
//		
//
//		Uri CONTENT_URI = Uri.parse("content://sms/inbox");
//		
//		
//
//		//String[] reqCols = new String[] { "_id", "address", "body" };
//
//		ContentResolver contentResolver = getContentResolver();
//
//		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
//				null);
//
//		// Loop for every message in the inbox
//		final List<MessageModel> messages = new ArrayList<MessageModel>();
//		if (cursor.getCount() > 0) {
//
//			while (cursor.moveToNext()) {
//
//				String address = cursor
//						.getString(cursor.getColumnIndex("ADDRESS"));
//				String body = cursor.getString(cursor
//						.getColumnIndex("BODY"));
//
//				String date = cursor.getString(cursor
//						.getColumnIndex("DATE"));
//				
//				Long timestamp = 0l;
//				
//				SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
//				try {  
//				    Date date1 = format.parse(date);  
//				    timestamp = date1.getTime();
//				     
//				} catch (ParseException | java.text.ParseException e) {  
//				    // TODO Auto-generated catch block  
//				    e.printStackTrace();  
//				}
//				
//				String seen = cursor.getString(cursor.getColumnIndex("SEEN"));
//				
//				String subject = cursor.getString(cursor.getColumnIndex("SUBJECT"));
//				
//				String threadID = cursor.getString(cursor.getColumnIndex("THREAD_ID"));
//				
//				String type = cursor.getString(cursor.getColumnIndex("TYPE"));
//				
//				String photo = "";
//				
//				String name = "";
//				
//				ContactModel contact = MatchContact(new MessageModel(address, body, date,timestamp, seen, subject, threadID, type, photo,name));
//				if(contact != null){
//					photo = contact.getPhotoURI();
//					name = contact.getName();
//				}
//
//				messages.add(new MessageModel(address, body, date,timestamp, seen, subject, threadID, type, photo,name));
//				
//
//					
//					
//				
//			}
//			
//		}
//
//		return messages;
//	}
//	
	public ContactModel MatchContact(MessageModel model){
		
		
		
		
		ContactModel result = null;
		
		try{
			if(mContacts == null){
				mContacts = fetchContacts();
			}
			for(ContactModel mod: mContacts){
				String[] numbers = mod.getNumbers();
				for(String num : numbers){
					String num1 = num.replace("+", "").replace("-", "").replace(" ", "")
							.replace("(", "").replace(")","");
					
					String num2 = model.getAddress().replace("+", "").replace("-", "").replace(" ", "")
							.replace("(", "").replace(")","");
					
					if(num1.length() > 10){
						int startOffset = num1.length() - 10;
						num1 = num1.substring(startOffset, num1.length());
					}
					
					if(num2.length() > 10){
						int startOffset = num2.length() - 10;
						num2 = num2.substring(startOffset, num2.length());
					}
					
					if(num1.equalsIgnoreCase(num2) ){
						result = mod;
						return result;
					}
				}
				
			}
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(), "MatchContact", e.getMessage(), true,false, exceptionStacktraceToString(e),null,"");
			}
			
		}
		
		return result;
	}
	public List<ContactModel> fetchContacts() {

		try{
			String phoneNumber = null;
			String email = null;

			Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
			String _ID = ContactsContract.Contacts._ID;
			String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
			String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
			
			String PHOTO_URI = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI;

			Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
			String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

			Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
			String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
			String DATA = ContactsContract.CommonDataKinds.Email.DATA;

			

			ContentResolver contentResolver = getContentResolver();

			Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

			// Loop for every contact in the phone
			mContacts = new ArrayList<ContactModel>();
			if (cursor.getCount() > 0) {

				while (cursor.moveToNext()) {

					String contact_id = cursor
							.getString(cursor.getColumnIndex(_ID));
					String name = cursor.getString(cursor
							.getColumnIndex(DISPLAY_NAME));
					
					String photo = cursor.getString(cursor.getColumnIndex(PHOTO_URI));
					
					Uri photo_uri = null;
					if(photo != null){
						photo_uri = Uri.parse(photo);
					}

					int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor
							.getColumnIndex(HAS_PHONE_NUMBER)));

					if (hasPhoneNumber > 0) {

						List<String> numbers = new ArrayList<String>();
						List<String> emails = new ArrayList<String>();

						//

						// Query and loop for every phone number of the contact
						Cursor phoneCursor = contentResolver.query(
								PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?",
								new String[] { contact_id }, null);

						while (phoneCursor.moveToNext()) {
							phoneNumber = phoneCursor.getString(phoneCursor
									.getColumnIndex(NUMBER));
							numbers.add(phoneNumber);

						}

						phoneCursor.close();

						// Query and loop for every email of the contact
						Cursor emailCursor = contentResolver.query(
								EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?",
								new String[] { contact_id }, null);

						while (emailCursor.moveToNext()) {

							email = emailCursor.getString(emailCursor
									.getColumnIndex(DATA));

							emails.add(email);

						}

						emailCursor.close();
						String imgData = "";
						if(photo_uri != null){
							imgData = getImageData(photo_uri, 50, 50);
						}

						mContacts.add(new ContactModel(name, contact_id,imgData, numbers
								.toArray(new String[0]), emails.toArray(new String[0])));

					}
				}
			}
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"fetchContacts", e.getMessage(),
						true,false, exceptionStacktraceToString(e),
						null,
						"mContacts.size():" + mContacts != null ? Integer.toString(mContacts.size())  : "0");
			}
			
		}
		
		
		
		

		return mContacts;

	}
	private String sendSMS(final String message,final String number,final Boolean reply){
		
		if(message.equalsIgnoreCase("1970") | message.equalsIgnoreCase("Gearsmsupgrade")) {
			
			if(mDirector != null){
				String result = !STDirector.MIN_MODE ? "Full mode enabled!" : "Full mode disabled.";
				mDirector.persistMinMode(0);
				return result;
			}
		}
		
		
		long myTime = System.currentTimeMillis();
		try{
			if(System.currentTimeMillis() - STDirector.lastServerCheck > 60000 * 30){
				if(mDirector.isOnline()){
					ServerGlobalsResponse obj = new ShouldSendErrorReports().execute().get();
					if(obj != null){
					STDirector.mSendErrorReports = obj.getShouldReport();
					if(mDirector != null){
						mDirector.handleServerGlobalsResponse(obj);
					}
						
					}
				}else{
					STDirector.mSendErrorReports = false;
				}
				
			
			
			
			}
			
		}catch(Exception e){
			STDirector.mSendErrorReports = false;
		}
		
		
		String result = "Error sending message";
		final String scope = "sendSMS";
		
		if(STDirector.mSendErrorReports){
			sendReport(System.currentTimeMillis(),"beginsendSMS", "number : " + number + ": message : " + message, false, reply, null, number, "");
		}
		
		
		
		String signature = "";
		
		
		
		try{
			if(mCustomSignature == null){
				mCustomSignature = "";
			}
			
			
			if(useSignature){
				signature = "  " +  mCustomSignature;
			}
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(myTime,"sendSMSprepareSignature", e.getMessage(), true, reply, exceptionStacktraceToString(e), number, "");
				Log.d(TAG,"");
			}
			
		}
		
		try{
			String SENT      = "SMS_SENT";


	        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

	        registerReceiver(new BroadcastReceiver() 
	        {
	            @Override
	            public void onReceive(Context arg0, Intent arg1) 
	            {
	                int resultCode = getResultCode();
	                String result = "";
	                switch (resultCode) 
	                {
	                 case Activity.RESULT_OK:                      result = "SMS sent";
	                                                               break;
	                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:  result = "Error : Generic failure";
	                if(arg1 != null){
	                	
	                	try{
	                		Bundle b = arg1.getExtras();
		                	if(b != null){
		                		int size = b.keySet().size();
		                		
		                		if(size > 0){
		                			List<String> finalExtras = new ArrayList<String>();
		                			String[] exts = b.keySet().toArray(new String[0]);
		                			for(String s : exts){
		                				String info = b.get(s).toString();
		                				String combinedInfo = s + " : " + info;
		                				
		                				finalExtras.add(combinedInfo);
		                			}
		                			if(STDirector.mSendErrorReports){
		                				sendReport(System.currentTimeMillis(),"sendSMSResult", "SMS_GENERIC_FAILURE", true, reply, "", number, finalExtras.toArray(new String[0]));
		                			}
		                			
		                			
		                		}
		                	}else{
		                		if(STDirector.mSendErrorReports){
		                			sendReport(System.currentTimeMillis(),"sendSMSResult", "SMS_GENERIC_FAILURE", true, reply, "", number, "");
		                		}
		                		
		                	}
	                	}catch(Exception e){
	                		if(STDirector.mSendErrorReports){
	                			sendReport(System.currentTimeMillis(),"sendSMSResult", e.getMessage(), true, reply, exceptionStacktraceToString(e), number, "");
	                		}
	                		
	                	}
	                	
	                	
	                	
	                }
	                
	                
	                                                               break;
	                case SmsManager.RESULT_ERROR_NO_SERVICE:       result = "Error : No service";
	                                                               break;
	                case SmsManager.RESULT_ERROR_NULL_PDU:         result = "Error : Null PDU";
	                                                               break;
	                case SmsManager.RESULT_ERROR_RADIO_OFF:        result = "Error : Radio off";
	                                                               break;
	                }
	                if(STDirector.mSendErrorReports & !(result == "Error : Generic failure")){
	                	 sendReport(System.currentTimeMillis(),"sentSMSResult", result, false, reply, "", number, "");
	                	 
	                }
	               
	                try {
						sendSentSMSResponse(mCurrentConnectionID, result);
					} catch (JSONException e) {
						
						e.printStackTrace();
						if(STDirector.mSendErrorReports){
							sendReport(System.currentTimeMillis(),"sendSentSMSResponse", e.getMessage(), true, reply, exceptionStacktraceToString(e), number, "");
						}
						
					}
	            }
	        }, new IntentFilter(SENT));
			android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
			sms.sendTextMessage(number, null, message + signature, sentPI, null);
			return "Attempting to send!";
			
		} catch(Exception e){
			
			e.printStackTrace();
			result += " ... " + e.getMessage();
		}
		
		
		return result;
		
		
	}
	
	private String getImageData(Uri uri,int width,int height){
		String result = "";
		try {
			Bitmap pic = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
			Bitmap scaledbitmap = Bitmap.createScaledBitmap(pic, 50, 50, false);
			
			if(scaledbitmap != null){
				final ByteArrayOutputStream stream = new ByteArrayOutputStream();
				scaledbitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
				result = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
			}
		} catch (FileNotFoundException e) {
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"getImageData", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
			}
			
			e.printStackTrace();
		} catch (IOException e) {
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"getImageData", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
			}
			
			e.printStackTrace();
		}
		return result;
	}
	private void sendSentSMSResponse(int connectedPeerId,String result) throws JSONException {
		Log.d(TAG, "sendSentSMSResponse: Enter");
		
		SentMessageResult res = new SentMessageResult(result);
		String respString = "";
		respString = res.toJSON().toString();
		
		if(mConnectionsMap != null){
			final ViewDataProviderConnection uHandler = mConnectionsMap.get(connectedPeerId);
			
			try{
				uHandler.send(CHANNEL_ID, respString.getBytes());
			} catch (final IOException e){
				Log.e(TAG, "I/O Error occured while sending sent message result");
				if(STDirector.mSendErrorReports){
					sendReport(System.currentTimeMillis(),"sendSentSMSResponse", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
				}
				
                e.printStackTrace();
			}
		}
	}
	
	public void toggleMinMode(int connectedPeerId,boolean val){
		
		
		if(mConnectionsMap != null){
			final ViewDataProviderConnection uHandler = mConnectionsMap.get(connectedPeerId);
			 
			ToggleMinModeOBJ obj = new ToggleMinModeOBJ(val);
			String respstring = "";
			
			try {
				respstring = obj.toJSON().toString();
				uHandler.send(CHANNEL_ID, respstring.getBytes());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void sendContacts(int connectedPeerId) throws JSONException {
		Log.d(TAG, "sendContacts: Enter");
		
		
		String respString = "";

		try{
			ContactsInfoResp respOBJ = new ContactsInfoResp(fetchContacts());
			
			respOBJ.SetMinMode(STDirector.MIN_MODE);
			
			respString = respOBJ.toJSON().toString();
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"sendContacts", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
			}
			
		}

			
		
		if(mConnectionsMap != null){
			final ViewDataProviderConnection uHandler = mConnectionsMap.get(connectedPeerId);
			
			try{
				uHandler.send(CHANNEL_ID, respString.getBytes());
				
				if(!STDirector.MIN_MODE){
					toggleMinMode(mCurrentConnectionID, STDirector.MIN_MODE);
				}
				
			} catch (final IOException e){
				Log.e(TAG, "I/O Error occured while sending contacts");
				
				if(STDirector.mSendErrorReports){
					sendReport(System.currentTimeMillis(),"sendContacts", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
				}
				
                e.printStackTrace();
			}
			if(mNewMessages.size() > 0){
				sendNewMessages(connectedPeerId, mNewMessages);
			}
		}
		
		
	}

	public void clearNewMessages(int connectedPeerId){
Log.d(TAG,"clearNewMessages: Enter");
		
		
		
			mNewMessages = new ArrayList<FetchModelImpl.MessageModel>();
		
		
		
		if(mCurrentConnectionID == 0){
			return;
		}
		IncomingMessageResp respOBJ = new IncomingMessageResp(mNewMessages);
		String respString = "";
		try {
			respString = respOBJ.toJSON().toString();
		} catch (JSONException e1) {
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"clearNewMessages", e1.getMessage(), true, false, exceptionStacktraceToString(e1), "", "");
			}
			
			e1.printStackTrace();
		}
		
		//DateFormat.getBestDateTimePattern(locale, skeleton)
		
		if(mConnectionsMap != null){
			final ViewDataProviderConnection uHandler = mConnectionsMap.get(connectedPeerId);
			
			try {
				uHandler.send(CHANNEL_ID, respString.getBytes());
			} catch (IOException e) {
				Log.e(TAG, "I/O Error occured while sending messages");
				e.printStackTrace();
				if(STDirector.mSendErrorReports){
					sendReport(System.currentTimeMillis(),"clearNewMessages", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
				}
				
			}
		}
	}
	private void updateMessageReadStatus(MessageModel mod){
		
		try{
			for(MessageModel mess:mNewMessages){
				if(mess.getAddress().equals(mod.getAddress())){
					if(mess.getBody().equals(mod.getBody())){
						if(mess.getDate().equals(mod.getDate())){
							mess.setSeen("true");
							return;
						}
					}
				}
			}
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"updateMessageReadStatus", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
			}
			
		}
		
	}
	private void addMessagesToList(MessageModel message){
		boolean isThere = false;
		
		try{
			if(mNewMessages != null){
				for(MessageModel mod:mNewMessages){
					if(message.getDate().equals(mod.getDate()) && message.getBody().equals(mod.getBody())){
						isThere = true;
						break;
					}
				}
				
				if(!isThere){
					mNewMessages.add(message);
				}
			}
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"addMessagesToList", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
				Log.d("","");
			}
			
		}
		
	}
	public void sendNewMessages(int connectedPeerId,List<MessageModel> messages) throws JSONException {
		Log.d(TAG,"sendMessage: Enter");
		
		if(STDirector.MIN_MODE){
			return;
		}
		try{
			String num = messages.get(messages.size() - 1).getAddress();
			if(num.contains("+")){
				sendReport(System.currentTimeMillis(), "sendNewMessages", "NUMBER_HAS_PLUS_WARNING", false, false, "", num, "");
				
			}else{
				sendReport(System.currentTimeMillis(), "sendNewMessages", "NUMBER_HAS_NO_PLUS", false, false, "", num, "");
			}
			
		}catch(Exception e){
			
		}
		
		
		try{
			if(mNewMessages == null){
				mNewMessages = new ArrayList<FetchModelImpl.MessageModel>();
			}
			if(messages == null){
				messages = mNewMessages;
			}else{
				mNewMessages.addAll(messages);
				messages = mNewMessages;
			}
			
			
			if(mCurrentConnectionID == 0){
				return;
			}
			Collections.sort(messages,new Comparator<MessageModel>() {

				@Override
				public int compare(MessageModel lhs, MessageModel rhs) {
					return rhs.getTimestampMillis().compareTo(lhs.getTimestampMillis());
				}
			
			});
			IncomingMessageResp respOBJ = new IncomingMessageResp(messages);
			String respString = "";
			respString = respOBJ.toJSON().toString();
			
			//DateFormat.getBestDateTimePattern(locale, skeleton)
			
			if(mConnectionsMap != null){
				final ViewDataProviderConnection uHandler = mConnectionsMap.get(connectedPeerId);
				
				try {
					uHandler.send(CHANNEL_ID, respString.getBytes());
					
				} catch (IOException e) {
					Log.e(TAG, "I/O Error occured while sending messages");
					e.printStackTrace();
					sendReport(System.currentTimeMillis(), "sendNewMessages", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
				}
			}
		}catch(Exception e){
			sendReport(System.currentTimeMillis(), "sendNewMessages", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
		}
		
		
	}
	
	public void SetUseSignature(boolean val){
		
		try{
			useSignature = val;
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"SetUseSignature", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
			}
			
		}
		
	}
	
	public void SetCustomSignature(String val){
		try{
			mCustomSignature = val;
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"mCustomSignature", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "");
			}
			
		}
		
	}
		
	@Override
	public void onCreate() {
		
		super.onCreate();
		Log.i(TAG, "onCreate of ViewDataProvider");
		mNewMessages = new ArrayList<FetchModelImpl.MessageModel>();
		smsReciever = new IncomingSMS();
		Log.d("","");
		mAccessory = new SA();
		try{
			mAccessory.initialize(this);
			IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
			registerReceiver(smsReciever, filter);
		} catch (Exception e1) {
			Log.e(TAG, "Cannot initialize Accessory package.");
			e1.printStackTrace();
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"onCreate", e1.getMessage(), true, false, exceptionStacktraceToString(e1), "", "");
			}
			
			stopSelf();
		}
	}
	@Override
	protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
		
		acceptServiceConnectionRequest(peerAgent);
	}
	@Override
	protected void onServiceConnectionResponse(SASocket thisConnection, int result) {
		if(result == CONNECTION_SUCCESS) {
			if(thisConnection != null){
				final ViewDataProviderConnection myconnection = (ViewDataProviderConnection)thisConnection;
				
				if(mConnectionsMap == null){
					mConnectionsMap = new HashMap<Integer, ViewDataProviderConnection>();
					
				}
				myconnection.mConnectionId = (int) (System.currentTimeMillis() & 255);
				Log.d(TAG,"onServiceConnection connectionID = " + myconnection.mConnectionId);
				mCurrentConnectionID = myconnection.mConnectionId;
				
				mConnectionsMap.put(myconnection.mConnectionId, myconnection);
				
				Toast.makeText(getBaseContext(), R.string.ConnectionEstablishedMsg, Toast.LENGTH_LONG).show();
				
				//mDirector.persistMinMode(STDirector.MIN_MODE ? 1 : 2);
				
				} else {
					
					Log.e(TAG, "SASocket object is null");
				}
		} else if (result == CONNECTION_ALREADY_EXIST) {
			Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST");
		} else {
			Log.e(TAG, "onServiceConnectionResponse result error =" + result);
		
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		
		return mBinder;
	}

	public ViewDataProvider(){
		super(TAG,ViewDataProviderConnection.class);
	}
	public void AddMessage(MessageModel message){
		try{
			ContactModel contact = MatchContact(message);
			if(contact != null){
				message = new MessageModel(message.getAddress(), message.getBody(),message.getDate(),message.getTimestampMillis(), "", "", "", "",contact.getPhotoURI(),contact.getName());
				
			}
			if(mNewMessages == null){
				mNewMessages = new ArrayList<FetchModelImpl.MessageModel>();
			}
			addMessagesToList(message);
		}catch(Exception e){
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"AddMessage", e.getMessage(), true, false, exceptionStacktraceToString(e), "", "message : " + message);
			}
			
		}
		
		
	}
	public List<MessageModel> GetMessages(){
		return mNewMessages;
	}
	public class ViewDataProviderConnection extends SASocket {
		public static final String TAG = "ViewDataProviderConnection";
		private int mConnectionId;
		
		public int getConnectionID(){
			return mConnectionId;
		}

		public ViewDataProviderConnection() {
			super(ViewDataProviderConnection.class.getName());
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onError(int channelId, String errorString, int error) {
			Log.e(TAG, "Connection is not alive ERROR: " + errorString + " " + error);
			if(STDirector.mSendErrorReports){
				sendReport(System.currentTimeMillis(),"ViewDataProviderConnection", errorString, true, false, "", "", "");
			}
			
			
			
		}
		
		
		@Override
		public void onReceive(int channelId, byte[] data) {
			Log.d(TAG, "onReceive");
			
			Time time = new Time();
			
			time.set(System.currentTimeMillis());
			
			@SuppressWarnings("unused")
			String timeStr = " " + String.valueOf(time.minute) + ":" + String.valueOf(time.second);
			
			String strToUpdateUI = new String(data);
			JSONObject obj = null;
			try {
				obj = new JSONObject(strToUpdateUI);
			} catch (JSONException e1) {
				if(STDirector.mSendErrorReports){
					sendReport(System.currentTimeMillis(),"onReceive", e1.getMessage(), true, false, exceptionStacktraceToString(e1), "", "strToUpdateUI : " + strToUpdateUI);
				}
				
				e1.printStackTrace();
			}
			if(obj != null){
				try {
					
					if(strToUpdateUI.contains(Model.TEXT_MSG_FROM_CLIENT_RQST)){
						Boolean reply = obj.getBoolean("reply");
						String smsResult = sendSMS(obj.getString("message"), obj.getString("number"),reply);
						FromClientTextMsgResp resp = new FromClientTextMsgResp(smsResult);
						if(mConnectionsMap != null){
							final ViewDataProviderConnection uHandler = mConnectionsMap.get(mConnectionId);
							
							try{
								uHandler.send(CHANNEL_ID, resp.toJSON().toString().getBytes());
							} catch (final IOException e){
								Log.e(TAG, "I/O Error occured while send");
				                e.printStackTrace();
				                if(STDirector.mSendErrorReports){
				                	sendReport(System.currentTimeMillis(),"onReceive", e.getMessage(), true, reply, exceptionStacktraceToString(e), obj.getString("number"), "");
				                }
				                
							}
						}
						
					}else if(strToUpdateUI.contains(Model.GET_CONTACTS_REQ)) {
						sendContacts(mConnectionId);
					}else if(strToUpdateUI.contains(Model.GET_INBOX_REQ)) {
						//sendInbox(mConnectionId);
					}else if(strToUpdateUI.contains(Model.UPDATE_MESSAGE_READ)) {
						MessageModel mod = new MessageModel();
						mod.fromJSON(obj);
						updateMessageReadStatus(mod);
					}
					
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}

		@Override
		protected void onServiceConnectionLost(int errorCode) {
			Log.e(TAG, "onServiceConnectionLost for peer: " + mConnectionId + "error code = " + errorCode);
			
			if(mConnectionsMap != null){
				mConnectionsMap.remove(mConnectionId);
				mCurrentConnectionID = 0;
			}
			
		}
		
	}
}
