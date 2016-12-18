package jp.co.aplabs.htmlbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HtmlBaseActivity extends Activity {
	static HtmlView	mHtmlView;
	static final String	TAG = "HtmlBase";

	public static final int ReqPickImage = 0;
	public static final int ReqText = 1;
	public static final int ReqPhoto = 2;
	public static final int ReqIntent = 3;
	public static final int ReqExtra = 4;
	public static final int ReqBarcode = 5;
	public static final int ReqOAuth = 6;

	private Twitter mTwitter;
	public static String CallbackURL = "pie://twitter";
    public static String SetActionbarURL = "pie://actionbar/";
    public static String UploadURL = "pie://upload/";
    private RequestToken mRequestToken;
	public static final String FirstPage = "#firstpage";
	
    class ScrModule {
    	List<String> mMlist; 

    	ScrModule(){
    		mMlist = new ArrayList<String>();
    	}
    	
    	String[] getList(){
    		String[] ml = new String[mMlist.size()];
    		for(int n=0; n < ml.length; ++n ){
    			ml[n] = mMlist.get(n);
    		}
    		return ml;
    	}
    	
    	void add(String module){
    		if( mMlist.indexOf(module) == -1){
    			mMlist.add(module);
    		}
    	}

    	void remove(String module){
    		int inx = mMlist.indexOf(module);
    		if( inx != -1 ){
    			mMlist.remove(inx);
    		}
    	}
    	
    	int size(){
    		return mMlist.size();
    	}
    	
    	String get(int n){
    		return mMlist.get(n);
    	}
    	
    	String export(){
    		StringBuilder sb = new StringBuilder();
    		sb.append("<modules>");
    		for( int n=0; n < mMlist.size(); ++n) {
				sb.append("<module><name>");
				sb.append(mMlist.get(n));
				sb.append("</name></module>");
			}
    		sb.append("</modules>");
    		
    		return sb.toString();
    	}

    	void load(String exml){
    		String[] mtks = exml.split("<module>");
    		mMlist.clear();
    		for( int n=1; n < mtks.length; ++n ){
    			mMlist.add(mtks[n].split("</module>")[0]);
    		}
    	}
    }
        
    ScrModule mModules;

	public String content2path(String cstr){
		Uri uri = Uri.parse(cstr);
		ContentResolver cr = getContentResolver();
//		String[] columns = {MediaStore.Images.Media.DATA };
//		Cursor c = cr.query(uri, columns, null, null, null);
		Cursor c = cr.query(uri, null, null, null, null);
//		startManagingCursor(c);
//		int num = c.getCount();
		String rtn = "";
		try {
			while (c.moveToNext()) {
				for (int i = 0; i < c.getColumnCount(); i++) {
					Log.d(getClass().getSimpleName(), " Column: " + c.getColumnName(i) + " String: " + c.getString(i));
				}
			}
			c.moveToFirst();
			rtn = c.getString(0);
			c.close();
		}catch(java.lang.NullPointerException e){
			e.printStackTrace();
		}
 		return rtn;
	}
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
        String action = intent.getAction();

		Log.d(TAG, "onCreate action="+action);
		mHtmlView = new HtmlView(this);
		setContentView(mHtmlView);
 //       if(TwitterUtils.hasAccessToken(this)) {
            mTwitter = TwitterUtils.getTwitterInstance(this);
            mHtmlView.setTwitter(mTwitter);
 //       }

		mModules  = new ScrModule();
		mModules.load(mHtmlView.getMomo().getItem("#Modules"));
		if( mModules.size()==0 ){
			try {
				String[]alist = getAssets().list("");
				for(int n=0; n < alist.length; ++n){
					if( Character.isUpperCase(alist[n].charAt(0)) ){
						mModules.add(alist[n]);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String firstpage = mHtmlView.getMomo().getItem(HtmlBaseActivity.FirstPage);
		if( firstpage.equals("") ){
			firstpage = "/Designer/loadsave.html";
		}
		mHtmlView.goPage(firstpage);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.html_base, menu);
	    menu.clear();
	    for( int n=0; n < mModules.size(); ++n ){
	    	menu.add(mModules.get(n));
	    }
	    
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
		mHtmlView.saveScale();
        super.onPause();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(mHtmlView == null){
			mHtmlView = new HtmlView(this);
	        if(TwitterUtils.hasAccessToken(this)) {
	            mTwitter = TwitterUtils.getTwitterInstance(this);
	            mHtmlView.setTwitter(mTwitter);
	        }
			mHtmlView.goPage("/Twitter/start.html");
			setContentView(mHtmlView);
    	}
	    menu.clear();
	    for( int n=0; n < mModules.size(); ++n ){
	    	String modname = mModules.get(n);
	    	if(!modname.equals(HtmlView.ModFolder)&&!modname.equals(HtmlView.SampleFolder)){
	    		menu.add(modname);
	    	}
	    }

    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(mHtmlView == null){
    		finish();
    		return true;
    	}
    	mHtmlView.goPage("/"+item.getTitle()+"/start.html");
    	
		return false;
    }

    @Override
	public boolean  onKeyDown  (int keyCode, KeyEvent event){
    	if(mHtmlView == null){
    		finish();
    		return true;
    	}
		switch( keyCode ){
		case KeyEvent.KEYCODE_BACK:
			mHtmlView.goPage("javascript:SKEY.onKeyBack()");
			return true;
		case KeyEvent.KEYCODE_SEARCH:
			return mHtmlView.onKeySearch();
		case KeyEvent.KEYCODE_VOLUME_UP:
			return mHtmlView.onKeyVUp();
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			return mHtmlView.onKeyVDown();
		}
		return false;
	}

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
    	if(resultCode == RESULT_OK){
			switch(requestCode){
			case ReqPickImage:
				mHtmlView.pickBack(data.getData());
				break;
			case ReqText:
				mHtmlView.textBack(data.getExtras().getString(TextInputActivity.DefText));
				break;
			case ReqIntent:
				mHtmlView.intentBack(data);
				break;
			case ReqPhoto:
				mHtmlView.cameraBack(data.getExtras().getString(PicMonActivity.CapFile));
				break;
			case ReqBarcode:
				mHtmlView.barcodeBack(data.getExtras().getString(QrActivity.BarcodeResult));
				break;
			case ReqOAuth:
				mHtmlView.authBack(data.getExtras().getString(OAuth2GoogleActivity.ACCESS_TOKEN));
				break;
			}
    	}
	}
   /////////////////////////////////////////////////////////////////////////// 
    public void twitterLogin(){
		if( mTwitter == null ){
            mTwitter = TwitterUtils.getTwitterInstance(this);
            mHtmlView.setTwitter(mTwitter);
            if(mTwitter==null){
            	return;
            }
		}
        if(!TwitterUtils.hasAccessToken(this)) {
        	startAuthorize();
        }
    }
    public void twitterLogout(){
    	TwitterUtils.removeAccessToken(this);
    	mTwitter = null;
    }
    
    /**
     * OAuth�F�؁i�����ɂ͔F�j���J�n���܂��B
     *
	 */
    private void startAuthorize() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken(CallbackURL);
                     return mRequestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            }
        };
        task.execute();
    }

    @SuppressLint("NewApi")
	@Override
    public void onNewIntent(Intent intent) {
        if (intent == null || intent.getData() == null ){
        	return;
        }
        String intentData = intent.getData().toString();
        if( intentData.startsWith(SetActionbarURL) ) {
			try {
				getActionBar().setTitle(intentData.substring(SetActionbarURL.length()));
			}catch(java.lang.NullPointerException e){
				e.printStackTrace();
			}
            return;
        }
  //      if( intentData.startsWith(UploadURL) ) {
  //      	uploadImage( mTwitter, intent.getData().toString().substring(UploadURL.length()) );
  //          return;
  //      }
        if( intentData.startsWith(CallbackURL) ) {
	        String verifier = intent.getData().getQueryParameter("oauth_verifier");
	
	        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
	            @Override
	            protected AccessToken doInBackground(String... params) {
	                try {
	                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
	                } catch (TwitterException e) {
	                    e.printStackTrace();
	                }
	                return null;
	            }
	
	            @Override
	            protected void onPostExecute(AccessToken accessToken) {
	                if (accessToken != null) {
	                    // �F�ؐ����I
	                    showToast("login success!");
	                    successOAuth(accessToken);
	                } else {
	                    // �F�؎��s�B�B�B
	                    showToast("login failed.");
	                }
	            }
	        };
	        task.execute(verifier);
        }
    }

    private void successOAuth(AccessToken accessToken) {
        TwitterUtils.storeAccessToken(this, accessToken);
    //    Intent intent = new Intent(this, HtmlBaseActivity.class);
    //    startActivity(intent);
    //    finish();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}

class TwitterUtils {
	public final static String LOGINCOM = "pie://LOGINCOM";

    private static final String TOKEN = "token";
    private static final String TOKEN_SECRET = "token_secret";
    private static final String PREF_NAME = "twitter_access_token";

    private static final String PREF_consumerKey = "consumerKey";
    private static final String PREF_consumerSecret = "consumerSecret";
    private static final String PREF_consumer = "twitter_consumer";

    public static void storeConsumerKey(String consumerKey) {
        SharedPreferences preferences = mContext.getSharedPreferences(PREF_consumer, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(PREF_consumerKey, consumerKey);
        editor.commit();
    }
    public static void storeConsumerSecret(String consumerSecret) {
        SharedPreferences preferences = mContext.getSharedPreferences(PREF_consumer, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(PREF_consumerSecret, consumerSecret);
        editor.commit();
    }

    public static String getConsumerKey() {
        SharedPreferences preferences = mContext.getSharedPreferences(PREF_consumer, Context.MODE_PRIVATE);
        return preferences.getString(PREF_consumerKey, null);
    }
    public static String getConsumerSecret() {
        SharedPreferences preferences = mContext.getSharedPreferences(PREF_consumer, Context.MODE_PRIVATE);
        return preferences.getString(PREF_consumerSecret, null);
    }

    private static Context	mContext;

    
    
    /**
     * Twitter�C���X�^���X���擾���܂��B�A�N�Z�X�g�[�N�����ۑ�����Ă���Ύ����I�ɃZ�b�g���܂��B
     * 
     */
    public static Twitter getTwitterInstance(Context context) {
    	mContext = context;
    	String consumerKey = getConsumerKey();
    	String consumerSecret = getConsumerSecret();

    	if( consumerKey==null || consumerSecret==null ){
    		return null;
    	}
    	
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        if (hasAccessToken(context)) {
            twitter.setOAuthAccessToken(loadAccessToken(context));
        }
        return twitter;
    }

    /**
     * �A�N�Z�X�g�[�N�����v���t�@�����X�ɕۑ����܂��B
     * 
     */
    public static void storeAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(TOKEN, accessToken.getToken());
        editor.putString(TOKEN_SECRET, accessToken.getTokenSecret());
        editor.commit();
    }

    public static void removeAccessToken(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.remove(TOKEN);
        editor.remove(TOKEN_SECRET);
        editor.commit();
    }

    /**
     * �A�N�Z�X�g�[�N�����v���t�@�����X����ǂݍ��݂܂��B
     * 
     */
    public static AccessToken loadAccessToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        String token = preferences.getString(TOKEN, null);
        String tokenSecret = preferences.getString(TOKEN_SECRET, null);
        if (token != null && tokenSecret != null) {
            return new AccessToken(token, tokenSecret);
        //	return null;
        } else {
            return null;
        }
    }

    /**
     * �A�N�Z�X�g�[�N�������݂���ꍇ��true��Ԃ��܂��B
     * 
     */
    public static boolean hasAccessToken(Context context) {
    	AccessToken at = loadAccessToken(context);
//    	if( at == null )
//    		return false;
        return at != null;
    }
}
