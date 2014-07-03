package com.halimede.contactssms.errorreporter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.halimede.contactssms.STDirector;
import com.halimede.contactssms.dataModels.Model;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class ErrorReport extends AsyncTask<Object,Void,Void> {

public static final class DeviceInfoReportModel implements Model.JsonSerializable {

		
	    private String mBOARD = "";
	    private String mBOOTLOADER = "";
	    private String mBRAND = "";
	    private String mCPU_ABI = "";
	    private String mCPU_ABI2 = "";
	    private String mDEVICE = "";
	    private String mDISPLAY = "";
	    private String mFINGERPRINT = "";
	    private String mHARDWARE = "";
	    private String mHOST = "";
	    private String mID = "";
	    private String mMANUFACTURER = "";
	    private String mMODEL = "";
	    private String mPRODUCT = "";
	    private String mRADIO = "";
	    private String mSERIAL = "";
	    private String mTAGS= "";
	    private String mTYPE = "";
	    private String mUSER = "";
	    
	    private String mCODENAME = "";
	    private String mINCREMENTAL = "";
	    private String mRELEASE = "";
	    private int mSDK_INT = 0;
	    
	    private String mMessage = "";
	    
	    private String mStackTrace = "";
	    
	    private String mScope = "";
	    
	    private Boolean mIsError = false;
	    
	    private List<String> mExtras;
	    
		
		
		public DeviceInfoReportModel(String scope,Boolean isError,String message,String stack,String... extras){
			mBOARD = Build.BOARD;
			mBOOTLOADER = Build.BOOTLOADER;
			mBRAND = Build.BRAND;
			mCPU_ABI = Build.CPU_ABI;
			mCPU_ABI2 = Build.CPU_ABI2;
			mDEVICE = Build.DEVICE;
			mDISPLAY = Build.DISPLAY;
			mFINGERPRINT = Build.FINGERPRINT;
			mHARDWARE = Build.HARDWARE;
			mHOST = Build.HOST;
			mID = Build.ID;
			mMANUFACTURER = Build.MANUFACTURER;
			mMODEL = Build.MODEL;
			mPRODUCT = Build.PRODUCT;
			mRADIO = Build.getRadioVersion();
			mSERIAL = Build.SERIAL;
			mTAGS = Build.TAGS;
			mTYPE = Build.TYPE;
			mUSER = Build.USER;
			
			mCODENAME = Build.VERSION.CODENAME;
			mINCREMENTAL = Build.VERSION.INCREMENTAL;
			mRELEASE = Build.VERSION.RELEASE;
			mSDK_INT = Build.VERSION.SDK_INT;
			
			mIsError = isError;
			
			if(scope != null){
				mScope = scope;
			}
			
			if(message != null){
				mMessage = message;
			}
			if(stack != null){
				mStackTrace = stack;
			}
			if(extras != null){
				mExtras = Arrays.asList(extras);
			}
			
		}
		
		

		@Override
		public Object toJSON() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put("board", mBOARD);
			obj.put("bootloader",mBOOTLOADER);
			obj.put("brand", mBRAND);
			obj.put("codename", mCODENAME);
			obj.put("cpu_abi", mCPU_ABI);
			obj.put("cpu_abi2", mCPU_ABI2);
			obj.put("device", mDEVICE);
			obj.put("display", mDISPLAY);
			obj.put("fingerprint", mFINGERPRINT);
			obj.put("hardware", mHARDWARE);
			obj.put("host", mHOST);
			obj.put("id", mID);
			obj.put("incremental", mINCREMENTAL);
			obj.put("manufacturer", mMANUFACTURER);
			obj.put("message", mMessage);
			obj.put("model", mMODEL);
			obj.put("product", mPRODUCT);
			
			obj.put("radio", mRADIO);
			obj.put("release", mRELEASE);
			obj.put("scope", mScope);
			obj.put("sdk_int", mSDK_INT);
			
			obj.put("serial", mSERIAL);
			obj.put("tags", mTAGS);
			obj.put("type", mTYPE);
			
			obj.put("user", mUSER);
			obj.put("iserror", mIsError);
			obj.put("stack", mStackTrace);
			if(mExtras != null){
				String r = "";
				for (String s : mExtras){
					r += s + "^^^BREAK^^^\n";
					
				}
				obj.put("extra_info", r );
			}else{
				obj.put("extra_info", "");
			}
			
			return obj;
		}

		@Override
		public void fromJSON(Object json) throws JSONException {
			
			
		}
		
	}





	public ErrorReport() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Void doInBackground(Object... params) {
		
		
		// InputStream inputStream = null;
		 
	        //String result = "";
	        String url = "http://untamemusic.com/untame14/api/ArtistServices/errorreport";
			//String url = "http://192.168.1.105:60139/api/ArtistServices/errorreport";
	        try {
	        	
	        	com.halimede.contactssms.errorreporter.ErrorReport.DeviceInfoReportModel mod = (DeviceInfoReportModel)params[0];
	        	
	        	
	        	
	        	JSONObject obj = (JSONObject) mod.toJSON();
	 
	            // 1. create HttpClient
	            HttpClient httpclient = new DefaultHttpClient();
	 
	            // 2. make POST request to the given URL
	            HttpPost httpPost = new HttpPost(url);
	 
	            String json = "";
	 
	            // 3. build jsonObject
	            
	            
	 
	            // 4. convert JSONObject to JSON to String
	            json = obj.toString();
	 
	            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
	            // ObjectMapper mapper = new ObjectMapper();
	            // json = mapper.writeValueAsString(person);
	 
	            // 5. set json to StringEntity
	            StringEntity se = new StringEntity(json);
	            String responseString = null;
	            // 6. set httpPost Entity
	            httpPost.setEntity(se);
	 
	            // 7. Set some headers to inform server about the type of the content   
	            httpPost.setHeader("Accept", "application/json");
	            httpPost.setHeader("Content-type", "application/json");
	 
	            // 8. Execute POST request to the given URL
	            HttpResponse httpResponse = httpclient.execute(httpPost);
	            //httpResponse.getEntity();
	            // 9. receive response as inputStream
	            StatusLine stat = httpResponse.getStatusLine();
				
				if(stat.getStatusCode() == HttpStatus.SC_OK){
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					httpResponse.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
					
					Boolean temp = Boolean.parseBoolean(responseString);

					try{
						STDirector.mSendErrorReports = temp;
					}catch(Exception e){
						
					}
				}
	            
	 
	            // 10. convert inputstream to string
	           
	             
	 
	        } catch (Exception e) {
	            Log.d("InputStream", e.getLocalizedMessage());
	        }
	 
		
		
		
		
		return null;
	}

	

	

}



