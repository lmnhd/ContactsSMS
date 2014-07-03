package com.halimede.contactssms.dataModels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.menu;





public class FetchModelImpl {

	
	public static final class FromClientTextMsg implements Model.JsonSerializable {

		
		String mMessageID = "";
		String mMessageToSend = "";
		String mRecipientAddress = "";
		Boolean mIsReply = false;
		
		
		public String getMessage(){
			return mMessageToSend;
		}
		
		public Long getNumber(){
			return Long.parseLong(mRecipientAddress);
		}
		
		public static final String ID = "id";
	    
		
		
		public FromClientTextMsg(){
			
		}
		
		public FromClientTextMsg(String address, String message,Boolean isReply){
			mMessageID = Model.TEXT_MSG_FROM_CLIENT_RQST;
			mRecipientAddress = address;
			mMessageToSend = message;
			mIsReply = isReply;
			
		}

		@Override
		public Object toJSON() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put("message", mMessageToSend);
			obj.put("number", mRecipientAddress);
			obj.put("reply", mIsReply);
			return obj;
		}

		@Override
		public void fromJSON(Object json) throws JSONException {
			JSONObject obj = (JSONObject)json;
			mMessageToSend = obj.getString("message");
			mRecipientAddress = obj.getString("number");
			mIsReply = obj.getBoolean("reply");
		}
		
	}
	public static final class FromClientTextMsgResp implements Model.JsonSerializable{
		String mMessageID = "";
		String mResult = "";
		
		public String getMessage(){
			return mResult;
		}
		public String getMessageID(){
			return mMessageID;
		}
		
		public FromClientTextMsgResp(String message){
			mResult = message;
			mMessageID = Model.TEXT_MSG_FROM_CLIENT_RESP;
			
		}
		@Override
		public Object toJSON() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put("id", mMessageID);
			obj.put("result", mResult);
			return obj;
		}
		@Override
		public void fromJSON(Object json) throws JSONException {
			JSONObject obj = (JSONObject)json;
			mResult = obj.getString("result");
			
		}
		
	}
	
	public static final class ToggleMinModeOBJ implements Model.JsonSerializable {
		String mMessageID = Model.TOGGLE_MIN_MODE_REQUEST;
		Boolean mVal = false;
		
		
		public ToggleMinModeOBJ(Boolean val){
			mVal = val;
		}


		@Override
		public Object toJSON() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put("msgId", mMessageID);
			obj.put("val", mVal);
			return obj;
		}


		@Override
		public void fromJSON(Object json) throws JSONException {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	public static final class ContactsInfoResp implements Model.JsonSerializable {
		
		String mMessageID = Model.GET_CONTACTS_RESP;
		
		
		JSONArray mContactList = new JSONArray();
		
		private Boolean mMinMode = true;
		
		
		public void SetMinMode(Boolean val){
			mMinMode = val;
		}
		
		public static final String ID = "id";
		
		public ContactsInfoResp(List<ContactModel> contacts){
			for(ContactModel mod : contacts){
				try {
					mContactList.put(mod.toJSON());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

		@Override
		public Object toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put("contacts", mContactList);
			
			json.put("minmode", mMinMode);
			
			json.put(Model.MSG_ID, mMessageID);
			
			return json;
		}

		@Override
		public void fromJSON(Object json) throws JSONException {
			// TODO Auto-generated method stub
			
		}
		
	}
	public static final class IncomingMessageResp implements Model.JsonSerializable {
		String mMessageID =  Model.INCOMING_MESSAGE_RESP;
		
JSONArray mMessages = new JSONArray();
		
		
		
		
		
		
		public static final String ID = "id";
		
		public IncomingMessageResp(List<MessageModel> messages){
			for(MessageModel mod : messages){
				try {
					mMessages.put(mod.toJSON());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

		@Override
		public Object toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put("messages", mMessages);
			
			
			
			json.put(Model.MSG_ID, mMessageID);
			
			return json;
		}

		@Override
		public void fromJSON(Object json) throws JSONException {
			// TODO Auto-generated method stub
			
		}
		
	}
    public static final class InBoxResp implements Model.JsonSerializable {
		
		String mMessageID = Model.GET_INBOX_RESP;
		
		
		JSONArray mMessages = new JSONArray();
		
		
		
		
		
		
		public static final String ID = "id";
		
		public InBoxResp(List<MessageModel> messages){
			for(MessageModel mod : messages){
				try {
					mMessages.put(mod.toJSON());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

		@Override
		public Object toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put("messages", mMessages);
			
			
			
			json.put(Model.MSG_ID, mMessageID);
			
			return json;
		}

		@Override
		public void fromJSON(Object json) throws JSONException {
			// TODO Auto-generated method stub
			
		}
		
	}
	public static final class FromHostTextMsg implements Model.JsonSerializable {

		@Override
		public Object toJSON() throws JSONException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void fromJSON(Object json) throws JSONException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static final class ClockReqMsg implements Model.JsonSerializable {
		
		public ClockReqMsg(){
			
		}

		@Override
		public Object toJSON() throws JSONException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void fromJSON(Object json) throws JSONException {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	public static final class ClockRespMsg implements Model.JsonSerializable {

		@Override
		public Object toJSON() throws JSONException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void fromJSON(Object json) throws JSONException {
			// TODO Auto-generated method stub
			
		}
		
	}
	public static final class TBListReqMsg implements Model.JsonSerializable {

		String mMessageID = "";
		Long mId = -1L;
		public static final String ID = "offset";

		// public static final String COUNT = "count";

	    /**
	     * 
	     */
		public TBListReqMsg() {

		}

	    /**
	     * 
	     * @param id
	     */
		public TBListReqMsg(Long id) {
			mMessageID = Model.THUMBNAIL_LIST_RQST;
			mId = id;

		}
	    /**
	     * 
	     * @return Object
	     */
		public Object toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(Model.MSG_ID, mMessageID);
			json.put(ID, mId);
			return json;
		}

	    /**
	     * 
	     * @param Object
	     */
		public void fromJSON(Object obj) throws JSONException {
			JSONObject json = (JSONObject) obj;
			mMessageID = json.getString(Model.MSG_ID);
			mId = json.getLong(ID);

		}

	    /**
	     * 
	     * @return String
	     */
		public String getMessageIdentifier() {
			return mMessageID;
		}

	    /**
	     * 
	     * @return long
	     */
		public long getID() {
			return mId;
		}

	}

	public static final class TBListRespMsg implements Model.JsonSerializable {
		String mMessageID = "";
		String mResult = "";
		int mReason = 0;
		int mCount = 0;
		public List<TBModelJson> msgTBList = null;

		public static final String COUNT = "count";
		public static final String LIST = "list";
		public static final String REASON = "reason";
		public static final String RESULT = "result";

	    /**
	     * 
	     */
		public TBListRespMsg() {
		}

	    /**
	     * 
	     * @param result
	     * @param reason
	     * @param count
	     * @param TBlist
	     */
		public TBListRespMsg(String result, int reason, int count,
				List<TBModelJson> TBList) {
			mMessageID = Model.THUMBNAIL_LIST_RESP;
			mResult = result;
			mReason = reason;
			mCount = count;
			msgTBList = new ArrayList<TBModelJson>();
			msgTBList.addAll(TBList);
		}

	    /**
	     * 
	     * @return Object
	     */
		@Override
		public Object toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(Model.MSG_ID, mMessageID);
			json.put(RESULT, mResult);
			json.put(REASON, mReason);
			json.put(COUNT, mCount);

			JSONArray msgarray = new JSONArray();

			for (TBModelJson sms : msgTBList) {
				Object obj = sms.toJSON();
				msgarray.put(obj);
			}

			json.put(LIST, msgarray);

			return json;
		}

	    /**
	     * 
	     * @param Object
	     */
		@Override
		public void fromJSON(Object obj) throws JSONException {
			JSONObject json = (JSONObject) obj;
			mMessageID = json.getString(Model.MSG_ID);
			mResult = json.getString(RESULT);
			mReason = json.getInt(REASON);
			mCount = json.getInt(COUNT);

			JSONArray jsonArray = json.getJSONArray(LIST);
			msgTBList = new ArrayList<TBModelJson>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObjct = (JSONObject) jsonArray.getJSONObject(i);
				TBModelJson sms = new TBModelJson();
				sms.fromJSON(jsonObjct);
				msgTBList.add(sms);
			}
		}

	    /**
	     * 
	     * @return String
	     */
		public String getMessageIdentifier() {
			return mMessageID;
		}

	    /**
	     * 
	     * @return int
	     */
		public int getMsgCount() {
			return mCount;
		}

	    /**
	     * 
	     * @return String
	     */
		public String getResult() {
			return mResult;
		}

	    /**
	     * 
	     * @return int
	     */
		public int getReason() {
			return mReason;
		}

	    /**
	     * 
	     * @return List<TBModelJson>
	     */
		public List<TBModelJson> getmsgTBList() {
			return msgTBList;
		}

	}

	public static final class ImgReqMsg implements Model.JsonSerializable {

		String mMessageID = "";
		public static final String ID = "id";
		public static final String WIDTH = "width";
		public static final String HEIGHT = "height";

		Long mId = -1L;
		int mWidth = 0;
		int mHeight = 0;

	    /**
	     * 
	     */
		public ImgReqMsg() {

		}

	    /**
	     * 
	     * @param id
	     * @param width
	     * @param height
	     */
		public ImgReqMsg(Long id, int width, int height) {
			mMessageID = Model.DOWNSCALE_IMG_RQST;
			mId = id;
			mWidth = width;
			mHeight = height;
		}

	    /**
	     * 
	     * @return String
	     */
		public String getMessageIdentifier() {
			return mMessageID;
		}

	    /**
	     * 
	     * @return long
	     */
		public long getID() {
			return mId;
		}

	    /**
	     * 
	     * @return int
	     */
		public int getWidth() {
			return mWidth;
		}

	    /**
	     * 
	     * @return int
	     */
		public int getHeight() {
			return mHeight;
		}

	    /**
	     * 
	     * @return Object
	     */
		@Override
		public Object toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(Model.MSG_ID, mMessageID);
			json.put(ID, mId);
			json.put(WIDTH, mWidth);
			json.put(HEIGHT, mHeight);

			return json;
		}

	    /**
	     * 
	     * @param obj
	     */
		@Override
		public void fromJSON(Object obj) throws JSONException {
			JSONObject json = (JSONObject) obj;
			mMessageID = json.getString(Model.MSG_ID);
			mId = json.getLong(ID);
			mWidth = json.getInt(WIDTH);
			mHeight = json.getInt(HEIGHT);

		}

	}
	public static final class SentMessageResult implements Model.JsonSerializable{
		
		private String mMessageID = "";
		private String mResult = "";
		
		public SentMessageResult(String messageResult){
			mMessageID = Model.SENT_MESSAGE_RESPONSE;
			mResult = messageResult;
		}

		@Override
		public Object toJSON() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put("msgId", mMessageID);
			obj.put("result", mResult);
			return obj;
		}

		@Override
		public void fromJSON(Object json) throws JSONException {
			// TODO Auto-generated method stub
			
		}
		
	}

	public static final class ImgRespMsg implements Model.JsonSerializable {

		String mMessageID = "";
		String mResult = "";
		int mReason = 0;
		TBModelJson mDownscaledImg = null;

		public static final String RESULT = "result";
		public static final String REASON = "reason";
		public static final String IMAGE = "image";

	    /**
	     * 
	     */
		public ImgRespMsg() {

		}

	    /**
	     * 
	     * @param result
	     * @param reason
	     * @param img
	     */
		public ImgRespMsg(String result, int reason, TBModelJson img) {
			mMessageID = Model.DOWNSCALE_IMG_RESP;
			mResult = result;
			mReason = reason;
			mDownscaledImg = img;

		}

	    /**
	     * 
	     * @return Object
	     */
		@Override
		public Object toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(Model.MSG_ID, mMessageID);
			json.put(RESULT, mResult);
			json.put(REASON, mReason);

			//JSONObject obj = new JSONObject();
			//obj = (JSONObject) mDownscaledImg.toJSON();
			json.put(IMAGE, (JSONObject) mDownscaledImg.toJSON());

			return json;
		}

	    /**
	     * 
	     * @param Object
	     */
		@Override
		public void fromJSON(Object obj) throws JSONException {
			JSONObject json = (JSONObject) obj;
			mMessageID = json.getString(Model.MSG_ID);
			mResult = json.getString(RESULT);
			mReason = json.getInt(REASON);

			JSONObject jobj = json.getJSONObject(IMAGE);
			mDownscaledImg = new TBModelJson();
			mDownscaledImg.fromJSON(jobj);

		}

	    /**
	     * 
	     * @return String
	     */
		public String getMessageIdentifier() {

			return mMessageID;
		}

	    /**
	     * 
	     * @return String
	     */
		public String getResult() {
			return mResult;

		}

	    /**
	     * 
	     * @return int
	     */
		public int getReason() {
			return mReason;
		}
		
	    /**
	     * 
	     * @return TBModelJson
	     */
		public TBModelJson getDownscaledImg() {
			return mDownscaledImg;
		}

	}
	public static final class ContactModel implements Model.JsonSerializable {
		private String mContactID = "";
		private String mName = "";
		private String[] mNumbers = null;
		private String[] mEmails = null;
		private String mPhotoUri = "";
		private Boolean mMinMode = true;
		public String getContactID(){
			return mContactID;
		}
		public String getName(){
			return mName;
		}
		public String[] getNumbers(){
			return mNumbers;
		}
		public String[] getEmails(){
			return mEmails;
		}
		public String getPhotoURI(){
			return mPhotoUri;
		}
		
		public void setMinMode(Boolean val){
			mMinMode = val;
		}
		
		public ContactModel(String name,String contactID,String photoUri, String[] numbers, String[] emails){
			mName = name;
			mContactID = contactID;
			mNumbers = numbers;
			mEmails = emails;
			if(photoUri != null){
				mPhotoUri = photoUri;
			}
			
		}
		@Override
		public Object toJSON() throws JSONException {
			JSONObject result = new JSONObject();
			result.put("name", mName);
			result.put("id", mContactID);
			result.put("photo", mPhotoUri);
			result.put("minmode", mMinMode);
			JSONArray phoneNumbers = new JSONArray();
			JSONArray emailList = new JSONArray();
			
			for(String num : mNumbers){
				phoneNumbers.put(num);
			}
			for(String em : mEmails){
				emailList.put(em);
			}
			
			result.put("numbers", phoneNumbers);
			result.put("emails", emailList);
			return result;
		}
		@Override
		public void fromJSON(Object json) throws JSONException {
			JSONObject obj = (JSONObject)json;
			mName = obj.getString("name");
			mContactID = obj.getString("id");
			mPhotoUri = obj.getString("photo");
			mMinMode = obj.getBoolean("minmode");
			JSONArray numarray = obj.getJSONArray("numbers");
			JSONArray emailarray = obj.getJSONArray("emails");
			
			for(int i = 0; i < numarray.length(); i++){
				mNumbers[i] = numarray.getString(i);
			}
			for(int j = 0; j < emailarray.length(); j++){
				mEmails[j] = emailarray.getString(j);
			}
			
			
		}
	}
	
	public static final class MessageModel implements Model.JsonSerializable{
		private String Address = "";
		private String Body = "";
		private String Date = "";
		private Long TimestampMillis = 0l;
		private String Seen = "";
		private String Subject = "";
		private String ThreadID = "";
		private String Type = "";
		private String PhotoData = "";
		private String mName = "";
		public String getAddress(){
			return Address;
		}
		public String getBody(){
			return Body;
		}
		public String getDate(){
			return Date;
		}
		public Long getTimestampMillis(){
			return TimestampMillis;
		}
		public String getSeen(){
			return Seen;
		}
		public String getSubject(){
			return Subject;
		}
		public String getThreadID(){
			return ThreadID;
		}
		public String getType(){
			return Type;
		}
		public String getPhoto(){
			return PhotoData;
		}
		public String getName(){
			return mName;
		}
		public void setSeen(String val){
			Seen = val;
		}
		public MessageModel(){
			
		}
		public MessageModel(String address,String body,String date,Long timestamp,String seen,String subject,String threadID,String type,String photo,String name){
			Address = address;
			Body = body;
			Date = date;
			TimestampMillis = timestamp;
			Subject = subject;
			ThreadID = threadID;
			Type = type;
			Seen = (seen.toLowerCase() != "true" && seen.toLowerCase() != "false") ? "false" : seen.toLowerCase();
			PhotoData = photo;
			mName = name;
			
		}
		@Override
		public Object toJSON() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put("address", Address);
			obj.put("body", Body);
			obj.put("date", Date);
			obj.put("timestamp", TimestampMillis);
			obj.put("subject", Subject);
			obj.put("threadID", ThreadID);
			obj.put("type", Type);
			obj.put("seen", Seen);
			obj.put("read", Seen);
			obj.put("photo", PhotoData);
			obj.put("name", mName);
			return obj;
		}
		@Override
		public void fromJSON(Object json) throws JSONException {
			JSONObject obj = (JSONObject)json;
			Address = obj.getString("address");
			Body = obj.getString("body");
			Date = obj.getString("date");
			TimestampMillis = obj.getLong("timestamp");
			Subject = obj.getString("subject");
			ThreadID = obj.getString("threadID");
			Type = obj.getString("type");
			Seen = obj.getString("seen");
			PhotoData = obj.getString("photo");
			mName = obj.getString("name");
			
			
		}
	}

	public static final class TBModelJson implements Model.JsonSerializable {

	    /**
	     * 
	     * @return String
	     */
		public String getData() {
			return mData;
		}

	    /**
	     * 
	     * @return String
	     */
		public String getName() {
			return mName;
		}

	    /**
	     * 
	     * @return int
	     */
		public int getWidth() {
			return mWidth;
		}

	    /**
	     * 
	     * @return int
	     */
		public int getHeight() {
			return mHeight;
		}

	    /**
	     * 
	     * @return long
	     */
		public long getSize() {
			return mSize;
		}

	    /**
	     * 
	     * @return long
	     */
		public long getId() {
			return mId;
		}

		public static final String ID = "id";
		public static final String DATA = "image";
		public static final String SIZE = "size";
		public static final String NAME = "name";
		public static final String WIDTH = "width";
		public static final String HEIGHT = "height";

		long mId = -1L;
		String mData = "";
		long mSize = 0L;
		String mName = "";
		int mWidth = 0;
		int mHeight = 0;

	    /**
	     * 
	     */
		public TBModelJson() {
		};

	    /**
	     * 
	     * @param id
	     * @param name
	     * @param data
	     * @param size
	     * @param width
	     * @param height
	     */
		public TBModelJson(long id, String name, String data, long size,
				int width, int height) {
			super();
			mId = id;
			mName = name;
			mData = data;
			mWidth = width;
			mHeight = height;
			mSize = size;

		}

	    /**
	     * 
	     * @param jsonObj
	     */
		@Override
		public void fromJSON(Object jsonObj) throws JSONException {
			JSONObject json = (JSONObject) jsonObj;
			mId = json.getLong(ID);
			mData = json.getString(DATA);
			mName = json.getString(NAME);
			mSize = json.getLong(SIZE);
			mHeight = json.getInt(HEIGHT);
			mWidth = json.getInt(WIDTH);

		}

	    /**
	     * 
	     * @return Object
	     */
		@Override
		public Object toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(ID, mId);
			json.put(NAME, mName);
			json.put(DATA, mData);
			json.put(SIZE, mSize);
			json.put(WIDTH, mWidth);
			json.put(HEIGHT, mHeight);

			return json;
		}

	}

}
