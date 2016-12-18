package jp.co.aplabs.htmlbase;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class OAuth2GoogleActivity extends Activity {


	public static final int REQCODE_OAUTH = 0;
	public static final String CLIENT_ID = "CLIENT_ID";
	public static final String CLIENT_SECRET = "CLIENT_SECRET";
	public static final String AUTH_URL = "AUTH_URL";
	public static final String TOKEN_URL = "TOKEN_URL";
	public static final String REDIRECT_URL = "REDIRECT_URL";
	public static final String SCOPE = "SCOPE";
	public static final String ACCESS_TOKEN = "access_token";

	protected String clientId;
	protected String clientSecret;
	protected String authURL;
	protected String tokenURL;
	protected String redirectURL;
	protected String scope;
	
	protected WebView mWv;


	@Override
	public void onCreate(Bundle savedInstanceState) {
	
	    super.onCreate(savedInstanceState);
	
	    // Intentからパラメータ取得
	    Intent intent = getIntent();
	    clientId = intent.getStringExtra(CLIENT_ID);
	    clientSecret = intent.getStringExtra(CLIENT_SECRET);
	    authURL = intent.getStringExtra(AUTH_URL);
	    tokenURL = intent.getStringExtra(TOKEN_URL);
	    redirectURL = intent.getStringExtra(REDIRECT_URL);
	    scope = intent.getStringExtra(SCOPE);
	
	    // 各種Viewを取得
	    mWv = new WebView(this);
		WebSettings settings = mWv.getSettings();
		settings.setJavaScriptEnabled(true);
	
		setContentView(mWv);
	
	    // WebView設定
		mWv.setWebViewClient(new WebViewClient() { // これをしないとアドレスバーなどが出る
	
			boolean hascode = false;
		
			@Override
			public void onPageFinished(WebView view, String url) { // ページ読み込み完了時
		
				if( hascode )
					return;
				// ページタイトルからコードを取得
				String title = view.getTitle();
				if(url.startsWith(redirectURL)){
					title = url;
				}
				String code = getCode(title);
		
				// コード取得成功ページ
				if (code != null) {
					hascode = true;
					mWv.loadUrl("file:///android_asset/waiting.html");
					new TaskGetAccessToken().execute(code); // アクセストークン取得開始
				}
			}
		});
	
	    // 認証ページURL
	    String url = authURL   // "https://accounts.google.com/o/oauth2/auth" // ここに投げることになってる
	        + "?client_id=" + clientId // アプリケーション登録してもらった
	        + "&response_type=code" // InstalledAppだとこの値で固定
	        + "&redirect_uri="+redirectURL // urn:ietf:wg:oauth:2.0:oob" // タイトルにcodeを表示する場合は固定
	        + "&scope=" + URLEncoder.encode(scope); // 許可を得たいサービス
	
	 
	    // 認証ページロード開始
	    mWv.loadData("<html><script>function init(){location.href=\""+url+"\";}</script><body onload='init()'>Loading....</body></html>", 
	    		"text/html", "utf-8");
	}


 /**
  * 認証成功ページのタイトルは「Success code=XXXXXXX」という風になっているので、   
  * このタイトルから「code=」以下の部分を切り出してOAuth2アクセスコードとして返す
  * 
  * @param title
  *          ページタイトル
  * @return OAuth2アクセスコード
  */
	protected String getCode(String title) {
		String code = null;
		String codeKey = "code=";

		if( title==null )
			return null;
    
		int idx = title.indexOf(codeKey);
		if (idx != -1) { // 認証成功ページだった
			code = title.substring(idx + codeKey.length()); // 「code」を切り出し
		}
		return code;
	}


	// アクセストークン取得タスク
	protected class TaskGetAccessToken extends AsyncTask<String, Void, String> {

//    @Override
//    protected void onPreExecute() {
//      Log.v("onPostExecute", "アクセストークン取得開始");
//    }

		@Override
		protected String doInBackground(String... codes) {
			String token = null;
			DefaultHttpClient client = new DefaultHttpClient();
			String result = "";
			try {
				// パラメータ構築
				ArrayList<NameValuePair> formParams = new ArrayList<NameValuePair>();
				formParams.add(new BasicNameValuePair("code", codes[0]));
				formParams.add(new BasicNameValuePair("client_id", clientId));
				formParams.add(new BasicNameValuePair("client_secret", clientSecret));
				formParams.add(new BasicNameValuePair("redirect_uri", redirectURL));
				formParams.add(new BasicNameValuePair("grant_type", "authorization_code"));

				// トークンの取得はPOSTで行うことになっている
				HttpPost httpPost = new HttpPost( tokenURL );
				httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8")); // パラメータセット
				HttpResponse res = client.execute(httpPost);
				HttpEntity entity = res.getEntity();
				result = EntityUtils.toString(entity);

				// JSONObject取得
				JSONObject json = new JSONObject(result);
				if ( json.has("access_token") ) {
					token = json.getString("access_token");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				String[] items = result.split("&");
				for(int n=0; n < items.length; ++n){
					if(items[n].startsWith("access_token="))	{
						token=items[n].substring(13);
						break;
					}
				}
			} finally {
				client.getConnectionManager().shutdown();
			}
			return token;
		}

		@Override
		protected void onPostExecute(String token) {
			if (token != null) {
				Intent intent = new Intent();
				intent.putExtra(ACCESS_TOKEN, token);
				setResult(Activity.RESULT_OK, intent);
			}
			finish();
		}
	} // END class TaskGetAccessToken
} // END class OAuth2GoogleActivity
