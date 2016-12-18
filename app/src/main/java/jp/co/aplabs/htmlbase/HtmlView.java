package jp.co.aplabs.htmlbase;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.SavedSearch;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.api.FavoritesResources;
import twitter4j.api.TweetsResources;
import twitter4j.api.UsersResources;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;
import android.R.drawable;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class HtmlView extends WebView {
	final static String	MAIN_SRC = "start.html";
	public static final String JSobjName = "uug";
	private static String mBaseName = "PineVentor - ";
	public static final String ModFolder = "Comp";
	public static final String SampleFolder = "Sample";
	public static final int ErrMsgMaxLength = 256*1024;
	public static final int ToastMsgMaxLength = 256;
	
	static String mOnKeyVDown;
	static String mOnKeyVUp;
	static String mOnKeySearch;	
	
	static class FileSort implements Comparator<File>{
		public int compare(File src, File target){
		int diff = src.getName().compareTo(target.getName());
		return diff;
		}
	}
	static class SimpleXml {
		// 
		static public String str2Value(String key, String target ){
			String[] iStrs =  target.split("<"+key);
			if(iStrs.length < 2)
				return "";

			String[] orgs = iStrs[1].split("</"+key);
			String[] poss = orgs[0].split(">", 2);
			if( poss.length < 2)
				return "";

			return poss[1];
		}
		
		static public String value2Tag(String tag, String val){
			return "<"+tag+">"+val+"</"+tag+">";
		}

	}
	void initKeyDispatch(){
		mOnKeyVDown="";
		mOnKeyVUp = "";
		mOnKeySearch = "";
	}
	public boolean onKeyVDown(){
		return onKeyDown(mOnKeyVDown);
	}
	public boolean onKeyVUp(){
		return onKeyDown(mOnKeyVUp);
	}
	public boolean onKeySearch(){
		return onKeyDown(mOnKeySearch);
	}
	
	boolean onKeyDown(String dispatch){
		if(dispatch.isEmpty()){
			return false;
		}
		goPage(dispatch);
		return true;
	}
	
	private static Toast t=null;

    public static void toast(String message) {
     if(t != null) {
      t.cancel();
     }
     t = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
     t.show();
    }

	static public String getPineDir(){
		return Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES ).getAbsolutePath()+"/pineVentor";
	}
	
	Momo.ITEMTYPE	mScrType;
	String	mCurFunc = "";
	String	mCurModule = "";
	String	mCurPagename = "";
	
	private static JSInterface jsi;
	static Momo	mMomo;
	static Context	mContext;

	Twitter	mTwitter;
	static Map<String,String> mCache =  new HashMap<String,String>();

	class SimpleParser {
		String mTarget;
		int		mScanp;

		String mTagname;
		ArrayList<String>	mAttrNames;
		ArrayList<String>	mAttrValues;
		
		SimpleParser( String target ){
			mTarget = target.trim();
			mScanp = 1;
			mAttrNames = new ArrayList<String>();
			mAttrValues = new ArrayList<String>();
		}
		
		int	getAttrSize(){
			return mAttrNames.size();
		}

		String getTagName(){
			return mTagname;
		}
		
		String getAttrName(int n){
			return mAttrNames.get(n);
		}
		String getAttrValue(int n){
			return mAttrValues.get(n);
		}
		String getAttr(String name){
			for( int n=0; n < mAttrNames.size(); ++n ){
				if( mAttrNames.get(n).equals(name)){
					return mAttrValues.get(n);
				}
			}
			return "";
		}
		
		@SuppressLint("NewApi")
		boolean parse(){
			if( mTarget.length() < mScanp )
				return false;
			if( !mTarget.startsWith("<") )
				return false;
			
			mTagname = getName();
			if(mTagname.isEmpty())
				return false;

			SkipSpace();
			while( mScanp < mTarget.length() ){
				String attrname = getName();
				if( attrname.isEmpty() )
					return true;
				
				SkipSpace();

				if( mTarget.charAt(mScanp)!='='){
					return false;
				}
				++mScanp;

				SkipSpace();
				if( mTarget.charAt(mScanp)!='"'){
					return false;
				}
				++mScanp;

				String attrvalue = getValue();

				mAttrNames.add(attrname);
				mAttrValues.add(attrvalue);
				
				SkipSpace();

				if( mTarget.charAt(mScanp) =='/' || mTarget.charAt(mScanp) =='>' ){
					return true;
				}
			}

			return false;
		}
		
		String getName(){
			StringBuffer name = new StringBuffer();
			if( Character.isJavaIdentifierStart( mTarget.charAt(mScanp) )){
				name.append(mTarget.charAt(mScanp++));
			}
			while( mScanp < mTarget.length() ){
				if(Character.isJavaIdentifierPart(mTarget.charAt(mScanp))){
					name.append(mTarget.charAt(mScanp++));					
				}else{
					break;
				}
			}
			return name.toString();
		}

		String getValue(){
			StringBuffer value = new StringBuffer();
			while( mScanp < mTarget.length() ){
				switch(mTarget.charAt(mScanp)){
				case '\\':
					++mScanp;
					switch(mTarget.charAt(mScanp)){
					case 'n':	value.append('\n');	mScanp++;	break;
					case 'r':	value.append('\r');	mScanp++;	break;
					case 't':	value.append('\t');	mScanp++;	break;
					default:
						value.append(mTarget.charAt(mScanp++));
					}
					break;
				case '"':
					++mScanp;
					return value.toString();
				default:
					value.append(mTarget.charAt(mScanp++));
				}
			}
			return value.toString();
		}

		void SkipSpace(){
			while( mScanp < mTarget.length() ){
				if(!Character.isWhitespace(mTarget.charAt(mScanp))){
					return;
				}
				mScanp++;
			}
		}
	
	}
	
	class ScrSource {
		StringBuffer		src;
		ArrayList<String>	lineSrc;

		ArrayList<String>	srcStack;
		ArrayList<Integer>		lnoStack;

		String	cursrc = "";
		int	curlno = 0;
		
		ScrSource(){
			lineSrc = new ArrayList<String>();
			src = new StringBuffer();
			srcStack = new ArrayList<String>();
			lnoStack = new ArrayList<Integer>();
			cursrc = "";
			curlno = 0;
		}
		
		public void clear(){
			src.delete(0, src.length());
			lineSrc.clear();
			srcStack.clear();
			lnoStack.clear();
			cursrc = "";
			curlno = 0;
		}
		
		public void append(String srcline){
			src.append(srcline);
			lineSrc.add(cursrc+" ("+Integer.toString(curlno)+")");
			++curlno;
		}

		public void appendLines( String str ){
			String[] strs = str.split("\n");
			for( int n=0; n < strs.length; ++n){
				append( strs[n]+"\n");
			}
		}
		
		public void appendCm(String cm){
			src.append(cm);
			lineSrc.add("");
		}
		
		public void push(String srcname){
			srcStack.add(new String(cursrc));
			lnoStack.add(new Integer(curlno));

			cursrc = srcname;
			curlno = 1;
		}
		
		public void pop(){
			int lastno = srcStack.size() - 1;

			cursrc = srcStack.get(lastno);
			srcStack.remove(lastno);

			curlno = lnoStack.get(lastno) + 1;
			lnoStack.remove(lastno);
		}
		
		public String toString(){
			return src.toString();
		}
		
		public String lno2srcname(int lno){
			try{
				return lineSrc.get(lno);
			}catch(IndexOutOfBoundsException e){
				return "lno("+Integer.toString(lno)+")";
			}
		}
	}

	ScrSource	mScrSrc;
		
	@SuppressLint("SetJavaScriptEnabled")
	public HtmlView(Context context) {
		super(context);
		Log.d(HtmlBaseActivity.TAG, "HtmlView()");
		mContext = context;
		mMomo = new Momo(context);
	//	mCache = new HashMap<String,String>();
		mScrSrc = new ScrSource();
		
		WebSettings settings = this.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSavePassword(false);
		settings.setSaveFormData(false);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

		//JavaScript
		jsi = new JSInterface(new Handler());
		this.addJavascriptInterface( jsi, JSobjName);
		this.setWebChromeClient(new WebChromeClient(){
			public boolean onConsoleMessage(ConsoleMessage cm) {
				String errmsg = mCurFunc + ": "+ cm.message() + " -- From line "
        				+ cm.lineNumber() + " of "
        				+ cm.sourceId() + "  "+mScrSrc.lno2srcname(cm.lineNumber())+"\n";
				logMsg("#debug", errmsg);
				
				String toastmsg = errmsg;
				if(toastmsg.length() > ToastMsgMaxLength){
					toastmsg = errmsg.substring(0, ToastMsgMaxLength);
				}
		//		Toast.makeText(mContext, toastmsg, Toast.LENGTH_LONG).show();
				toast(toastmsg);
				return true;
			}
		});

		loadScale();
	}

	public void logMsg(String key, String msg){
		String errmsg = msg + "\n" + mMomo.getItem(key);

		if( errmsg.length() > ErrMsgMaxLength ){
			errmsg = errmsg.substring(0, ErrMsgMaxLength);
		}
		mMomo.setItem(key, errmsg );
	}
	
	
	public Momo getMomo(){
		return mMomo;
	}

	String saveScaleName(){
		return "#scale/" + mCurPagename ;
	}
	
	@SuppressWarnings("deprecation")
	public void saveScale(){
		String scale = String.valueOf(getScale());
		getMomo().setItem(saveScaleName(), scale );
	}
	void loadScale(){
		String scalestr = mMomo.getItem(saveScaleName());
		if( scalestr.equals("") ){
			scalestr = "0";
		}
		int scale = (int)(Float.valueOf(scalestr)*100);
		this.setInitialScale(scale);
	}
	
	public boolean get_ZoomFlg(){
		WebSettings settings = this.getSettings();
		return settings.getBuiltInZoomControls();
	}
	public void set_ZoomFlg(boolean zm){
		WebSettings settings = this.getSettings();
		settings.setBuiltInZoomControls(zm);
	}
	
	
	public void goPage(String pagename){
		saveScale();

		if(pagename.indexOf(":")!=-1){
			this.loadUrl(pagename);
			return;
		}
	
		if(pagename.indexOf("/")!=-1){
			if(pagename.indexOf("/")==0){
				pagename = pagename.substring(1);
			}
			int p = pagename.indexOf('/');
			if( p == -1 ){
				mCurModule = "";
			}else{
				mCurModule = pagename.substring(0, p)+"/";
			}
		}else{
			pagename = mCurModule + pagename;
		}
		mScrType = getMomo().checkItem(pagename);
		if( mScrType != Momo.ITEMTYPE.None ){
			mCurPagename = pagename;
			this.loadPage(pagename);
		}
		
		jsi.setActionbarTitle(pagename);
		loadScale();
	}

	int	wc( String str ){
		return str.split("\r\n").length;
	}
	
	private void loadPage(String pagename){
		initKeyDispatch();
		getMomo().setItem(HtmlBaseActivity.FirstPage, pagename);
		mScrSrc.clear();
		parseSrc(pagename);
		this.loadDataWithBaseURL(mBaseName, mScrSrc.toString(), "text/html", "utf-8", null);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	void parseSrc(String src){
	
		mScrSrc.push(src);
		
		String pagestr = getMomo().getItem(src);
		String[] srcs = pagestr.split("\n");

		for( int n=0; n < srcs.length; ++n ){

			SimpleParser simp = new SimpleParser(srcs[n]);
			if( simp.parse() ){
				String tag = simp.getTagName();
				if(tag.equals("script")){
					String src1 = simp.getAttr("src");
					if( ! src1.isEmpty() ){
						String sname = mCurModule + src1;
						mScrSrc.push(sname);
						mScrSrc.appendCm("<script>\n");
						mScrSrc.appendLines(getMomo().getItem(sname));
						mScrSrc.appendCm("</script>\n");
						mScrSrc.pop();
					}else{
						mScrSrc.append(srcs[n]+"\n");
					}
				}else
				if(tag.equals("comp")){
					String mtype = ModFolder + "/" + simp.getAttr("type") + ".mod";
					String mname = simp.getAttr("name");
					String tname = mname + "Tag";
					String modstr = getMomo().getItem(mtype);

					for( int m=0; m < simp.getAttrSize(); ++m){
						modstr = modstr.replaceAll("\\$"+simp.getAttrName(m)+"\\$", simp.getAttrValue(m));
					}

					String[] defdef = modstr.split("\n",2);
					if( defdef.length > 1){
						SimpleParser defps = new SimpleParser(defdef[0]);
						if( defps.parse() && defps.getTagName().equals("default")){
							modstr = defdef[1];
							for( int m=0; m < defps.getAttrSize(); ++m){
								modstr = modstr.replaceAll("\\$"+defps.getAttrName(m)+"\\$", defps.getAttrValue(m));
							}
						}
					}
					
					if(simp.getAttr("tag").isEmpty()){
						modstr = modstr.replaceAll("\\$tag\\$", tname);
					}

					mScrSrc.push(mtype);
					mScrSrc.appendLines(modstr);
					mScrSrc.pop();
				}else
				if(tag.equals("link") && simp.getAttr("rel").equals("stylesheet")){
					String sname = mCurModule + simp.getAttr("href");
					mScrSrc.push(sname);
					mScrSrc.appendCm("<style>\n");
					mScrSrc.appendLines(getMomo().getItem(sname));
					mScrSrc.appendCm("</style>\n");
					mScrSrc.pop();					
				}else
				if(tag.equals("include")){
					String sname = mCurModule + simp.getAttr("src");
					parseSrc(sname);
				}else{
					mScrSrc.append(srcs[n]+"\n");
				}
			}else{
				mScrSrc.append(srcs[n]+"\n");
			}

		}
		mScrSrc.pop();
	}
	
	public void reset(){
	    AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
	    
	    // ダイアログの設定
	    alertDialog.setIcon(drawable.ic_delete);   //アイコン設定
	    alertDialog.setTitle("Reset");      //タイトル設定
	    alertDialog.setMessage("Reset all data？");  //内容(メッセージ)設定
	
	    // OK(肯定的な)ボタンの設定
	    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            // OKボタン押下時の処理
	            getMomo().clear();
	    		goPage("start.html");
	        }
	    });
	
	    // NG(否定的な)ボタンの設定
	    alertDialog.setNegativeButton("NG", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            // NGボタン押下時の処理
	            Log.d("AlertDialog", "Negative which :" + which);
	        }
	    });
	
	    // ダイアログの作成と描画
	//    alertDialog.create();
	    alertDialog.show();
	}
	
	void setTwitter(Twitter t){
		mTwitter = t;
	}

	String reqcodeToKeystr( int reqcode ){
		return "REQ"+Integer.toString(reqcode);
	}
	/////////////////////Intent result
	void pickBack(Uri uri){
		loadUrl("javascript:"+get_cache( reqcodeToKeystr( HtmlBaseActivity.ReqPickImage ) )+"('"+uri.toString()+"')");
	}
	void cameraBack(String uri){
		loadUrl("javascript:"+get_cache( reqcodeToKeystr( HtmlBaseActivity.ReqPhoto ) )+"('"+uri+"')");
	}
	void textBack(String str){
		put_cache(TextInputActivity.DefText, str);
		loadUrl("javascript:"+get_cache( reqcodeToKeystr( HtmlBaseActivity.ReqText ) )+"('"+TextInputActivity.DefText+"')");
	}
	void intentBack(Intent data){
		String extra = get_cache( reqcodeToKeystr( HtmlBaseActivity.ReqExtra ) );
		String dstr = data.getStringExtra(extra);
		put_cache(extra,dstr);
		loadUrl("javascript:"+get_cache( reqcodeToKeystr( HtmlBaseActivity.ReqIntent ) )+"('"+extra+"')");
	}
	void barcodeBack(String codeval){
		loadUrl("javascript:"+get_cache( reqcodeToKeystr( HtmlBaseActivity.ReqBarcode ) )+"('"+codeval+"')");
	}
	void authBack(String codeval){
		loadUrl("javascript:"+get_cache( reqcodeToKeystr( HtmlBaseActivity.ReqOAuth ) )+"('"+codeval+"')");
	}
	
	
	public void put_cache(String key, String val){
		mCache.put(key, val);
	}
	public String get_cache(String key){
		String rtv = mCache.get(key);
		if( rtv==null){
			return "";
		}
		mCache.remove(key);
		return rtv;
	}
	//////////////////////////////////////////////////////
	private String recp;
	private void recInit(String apiname){
		recp = apiname+"<hr/>";
	}
	private void recParam(String pname, String pval){
		recp = recp + pname + "=" +pval +"<br/>";
		getMomo().setItem("#recParam", recp);
	}
	@SuppressLint("NewApi")
	public final class JSInterface {
		private MediaPlayer mp;

		public void setOnKeyVDown(String dispatch){
			mOnKeyVDown = dispatch;
		}
		public void setOnKeyVUp(String dispatch){
			mOnKeyVUp = dispatch;
		}
		public void setOnKeySearch(String dispatch){
			mOnKeySearch = dispatch;
		}

		public JSInterface(Handler handler) {
			mp = new MediaPlayer();
		}
		
		public void setMedia( String src ){
			try {
				mp.reset();
				mp.setDataSource(src);
				mp.prepare();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void playMedia(){
			mp.start();
		}
		public void stopMedia(){
			mp.stop();
		}
		public void pauseMedia(){
			mp.pause();
		}
		public void seekMedia(int msec){
			mp.seekTo(msec);
		}
		public int getMediaDuration(){
			return mp.getDuration();
		}
		public int getMediaPosition(){
			return mp.getCurrentPosition();
		}

		
/**
Action: text			起動されるアクティビティのアクション。
ActivityClass: text		起動されるアクティビティのクラスの名前。
ActivityPackage: text	起動されるアクティビティのパッケージの名前。
DataUri: text			起動されるアクティビティに渡される URI 。
ExtraKey: text			アクティビティに渡されるテキストのキー名。
ExtraValue: text		アクティビティに渡されるテキストの値。
Result: text			起動されたアクティビティにより返された値。
ResultName: text		起動されたアクティビティから返された結果を抽出するために使用される名前。
ResultType: text		起動されたアクティビティから返された情報のタイプ。
ResultUri: text
 */
		public void intentCall(String action, String datauri, 
				String extrakey, String extravalue, String resultname, String callback){

			put_cache( reqcodeToKeystr( HtmlBaseActivity.ReqIntent ), callback );
			put_cache( reqcodeToKeystr( HtmlBaseActivity.ReqExtra ), resultname );
		    Intent intent = new Intent();
		    intent.setAction(action);

		    if( !datauri.isEmpty()){
		    	intent.setData(Uri.parse(datauri));
		    }
		    if( !extrakey.isEmpty()){
		    	intent.putExtra(extrakey, extravalue);
		    }

		    ((Activity) mContext).startActivityForResult(intent, HtmlBaseActivity.ReqIntent);
		}
		public void intentSend(String action, String itype, String datauri, String extrakey, String extravalue){
		    Intent intent = new Intent();
		    intent.setAction(action);

		    if( !datauri.isEmpty()){
		    	intent.setData(Uri.parse(datauri));
		    }
		    if( !extrakey.isEmpty()){
		    	intent.putExtra(extrakey, extravalue);
		    }
		    if( !itype.isEmpty()){
			    intent.setType(itype);
		    }
		    ((HtmlBaseActivity)mContext).startActivity(intent);
		}
		////DB読み書き//////////////////////////////////////////////
		public void setItem(String key, String val){
			getMomo().setItem(key, val);
		}
		public String getItem(String key){
			return getMomo().getItem(key);
		}
		public void removeItem(String key){
			getMomo().removeItem(key);
		}
		public String listItem(){
			return getMomo().listItem();
		}
		public String listItems(String query){
			return getMomo().listItem(query);
		}
		public String getAssetList(String key){
			String axml = "";
			try {
				String[] alist = mContext.getAssets().list(key);
				for( String a : alist){
					axml = axml + "<asset><name>"+a+"</name></asset>";
				}
			} catch (IOException e) {
			}
			return "<assets>"+axml+"</assets>";
		}

		public String getModules(){
			return ((HtmlBaseActivity)mContext).mModules.export();
		}

		public void logmsg(String logname, String msg){
			logMsg(logname,msg);
		}
		public String getCache(String key){
			return get_cache(key);
		}
		public boolean getZoomFlg(){
			return get_ZoomFlg();
		}
		public void setZoomFlg(boolean zm){
			set_ZoomFlg(zm);
		}

		public String getVersionName(){
			PackageManager packageManager = mContext.getPackageManager();
			try {
				PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
	            return packageInfo.versionName;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			}
		}
		
		@SuppressWarnings("deprecation")
		public int getOrientation(){
			Display display = ( (android.view.WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			int dir = display.getRotation();
			return dir;
		}
		public void setOrientation(int dir){
			Display display = ( (android.view.WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			switch(dir){
			case Surface.ROTATION_0:
				if( display.getHeight() > display.getWidth() ){
					dir = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				}else{
					dir = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				}
				break;
			case Surface.ROTATION_90:
				if( display.getHeight() < display.getWidth() ){
					dir = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				}else{
					dir = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
				}
				break;
			case Surface.ROTATION_180:
				if( display.getHeight() > display.getWidth() ){
					dir = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
				}else{
					dir = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
				}
				break;
			case Surface.ROTATION_270:
				if( display.getHeight() < display.getWidth() ){
					dir = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
				}else{
					dir = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				}
				break;
			}
			((Activity)mContext).setRequestedOrientation(dir);
		}

		public void storeConsumerKey(String consumerKey){
			TwitterUtils.storeConsumerKey(consumerKey);
		}
		
		public void storeConsumerSecret(String consumerSecret){
			TwitterUtils.storeConsumerSecret(consumerSecret);
		}
		
		@SuppressWarnings("resource")
		public String getIcon(int icon) {
			byte[] buffer= new byte[16*1024];
			ByteArrayOutputStream outs = new ByteArrayOutputStream(); 

			try {
				InputStream ris = mContext.getResources().openRawResource(icon);

	            int readBytes = 0;
	            while ((readBytes = ris.read(buffer)) != -1) {
	            	outs.write(buffer,0,readBytes);
	            }
			} catch (Exception e1) {
				e1.printStackTrace();
				return "";
			}
			
			return "data:image/png;base64,"+Base64.encodeToString(outs.toByteArray(), Base64.DEFAULT);
		}

		public String getQRcode( String str ){
			byte[] buffer= new byte[16*1024];
			ByteArrayOutputStream outs = new ByteArrayOutputStream(); 

			InputStream ris = QRCodeControler.mkQRimage( str );
            int readBytes = 0;
            try {
				while ((readBytes = ris.read(buffer)) != -1) {
					outs.write(buffer,0,readBytes);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
			return "data:image/png;base64,"+Base64.encodeToString(outs.toByteArray(), Base64.DEFAULT);
		}
		

		////コード変換//////////////////////////////////////////////
		public String transcode(String str, String enc){
			String ret;
			try {
				byte[] bytes = str.getBytes(enc);
				ret = new String(bytes,"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret = e.toString();
			}
			return ret;
		}
		////ページ遷移//////////////////////////////////////////////
		public void go(String pagename){
			goPage(pagename);
		}
		public void finish(){
			((HtmlBaseActivity)mContext).finish();
		}
		public void goCamera(int timer, String callback){
			put_cache( reqcodeToKeystr( HtmlBaseActivity.ReqPhoto ), callback );
			Intent intent = new Intent(mContext,jp.co.aplabs.htmlbase.PicMonActivity.class);
			intent.setAction(Intent.ACTION_MAIN);
			intent.putExtra(PicMonActivity.TimerValue, timer);
			((Activity) mContext).startActivityForResult(intent, HtmlBaseActivity.ReqPhoto);
		}
		public void goBarcode( String callback ){
			put_cache( reqcodeToKeystr( HtmlBaseActivity.ReqBarcode ), callback );
			Intent intent = new Intent(mContext,jp.co.aplabs.htmlbase.QrActivity.class);
			intent.setAction(Intent.ACTION_MAIN);
			((Activity) mContext).startActivityForResult(intent, HtmlBaseActivity.ReqBarcode);
		}
		public void cf(String curfunc){
			mCurFunc = curfunc;
		}
		
		//////////////////////////////////////////////////
		public void fwrite(String fname, String value){
	    	try {
	    		String fpath = fname2realpath(fname);
	    		int odp = fpath.lastIndexOf("/");
	    		String odir = fpath.substring(0, odp);

	    		File foutdir = new File(odir);
	        	if( !foutdir.exists() ){
	        		foutdir.mkdirs();
	        	}
	    		
	    		FileOutputStream f = new FileOutputStream( fpath );
				f.write(value.getBytes());
				f.close();
				
				// ContentProviderへの登録
				String[] paths = {fpath};
				MediaScannerConnection.scanFile(mContext, paths, null, null);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		public String getDir(String dname){
			String foldername = fname2realpath(dname);
			File dir = new File( foldername );
			StringBuffer fl = new StringBuffer();
			final File[] files = dir.listFiles();
			if(files!=null){
				Arrays.sort(files, new FileSort());
				for(int n=0; n < files.length; ++n){
					if(files[n].isDirectory() )
						fl.append( "<folder>" );
					else
						fl.append( "<file>" );

					fl.append("<name>"+files[n].getName() + "</name><length>"+files[n].length()+"</length>");
					String cap = getMomo().getItem(foldername+"/"+files[n].getName());
					String[] annos = cap.split("<anno");
					for( int m=1; m < annos.length; ++m ){
						String atext = SimpleXml.str2Value("text", annos[m]);
						if( !atext.equals("") ){
							fl.append(SimpleXml.value2Tag("text", atext));
						}
					}

					if(files[n].isFile())
						fl.append( "</file>" );
					else
						fl.append( "</folder>" );
				}
			}
			return "<files>"+fl.toString()+"</files>";
	
		}
		public String fname2realpath(String fname){
			String rtn;
			if( fname.startsWith("content://") ){
				return fname;
			}
			if( fname.startsWith("file:///android_asset/") ){
				return fname;
			}
			if( fname.startsWith("file://") ){
				return fname.substring(7);
			}
    		if(fname.startsWith("/")){
    			return fname;
    		}
    		return getPineDir()+"/"+fname;
		}
		public String fread(String fname){
        	StringBuffer sb = new StringBuffer();
	    	try {
				InputStream f;
				if( fname.startsWith("content:") ) {
					Uri contentUri = Uri.parse(fname);
					ContentResolver cr = mContext.getContentResolver();
					f = cr.openInputStream( contentUri );
				}else{
					String fpath = fname2realpath(fname);
					f = new FileInputStream(fpath);
				}
	        	byte[] sBuffer = new byte[10240];
	        	int s;
	        	while ((s=f.read(sBuffer)) != -1) {
	            	sb.append(new String(sBuffer, 0, s, "UTF-8"));
	            }
				f.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	return sb.toString();
	    }

		public String Base64encode( String str ){
			return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
		}
		public String Base64decode( String str ){
			byte d[] = Base64.decode(str, Base64.URL_SAFE);

			String s;
	//		try {
				s = new String(d);
				return s;
	//		} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		return "";
		}
		
		public void auth(String callback, String authurl, String tokenurl, String redirecturl, String clientid, String clientsecret, String scope ) {
			put_cache( reqcodeToKeystr( HtmlBaseActivity.ReqOAuth ), callback );
			Intent intent = new Intent(mContext, OAuth2GoogleActivity.class);
			intent.putExtra(OAuth2GoogleActivity.AUTH_URL, authurl);
			intent.putExtra(OAuth2GoogleActivity.TOKEN_URL, tokenurl);
			intent.putExtra(OAuth2GoogleActivity.REDIRECT_URL, redirecturl);
			intent.putExtra(OAuth2GoogleActivity.CLIENT_ID, clientid);
			intent.putExtra(OAuth2GoogleActivity.CLIENT_SECRET, clientsecret);
			intent.putExtra(OAuth2GoogleActivity.SCOPE, scope);
			((Activity) mContext).startActivityForResult(intent, HtmlBaseActivity.ReqOAuth );
		}

		public void sendHttpContent(final String method, final String url, final String ientity, final String header, String callback, String eid, String enc, String download ){
			final String callbackFunc = callback; 
			final String callbackPage = mCurPagename;
			final String Eid = eid; 
			final String Enc = enc; 
			final String Download = download; 
	        AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
				String callerModule;
				@Override
				protected String doInBackground(String... arg0) {
					callerModule = new String(mCurModule);
					String sendurl = url;
					if( !url.startsWith("http://") && !url.startsWith("https://")){
						sendurl = "http://"+url;
					}
					return sendUrlContent(method,sendurl,ientity,true,header,Enc,Download);
				}
	            @Override
	            protected void onPostExecute(String url) {
	            	if( callerModule.equals(mCurModule)){
	            		jsCallback(url, callbackFunc, Eid, callbackPage);
	            	}
	            }
	        };
	        task.execute(url, ientity, header);
		}
		
		
		private static final int HTTP_STATUS_OK = 200;
		DefaultHttpClient client ;

		public synchronized String sendUrlContent(String Method, String url, String ientity, boolean redirect, String header, String enc, String download ) {
			// Create client and set our specific user-agent string
			Log.d("pineVentor", "Method:"+Method+", url:"+ url+", ientity:"+ ientity+", header:"+ header );
			HttpRequestBase request = null;
			client = new DefaultHttpClient();
			try{
				if( Method.equalsIgnoreCase("DELETE")){
					request = new HttpDelete(url);
				}
				if( Method.equalsIgnoreCase("GET")){
					request = new HttpGet(url);
				}
				if( Method.equalsIgnoreCase("POST") ||  Method.equalsIgnoreCase("PUT")){
					if(  Method.equalsIgnoreCase("PUT") ){
						request = new HttpPut(url);
					}else{
						request = new HttpPost(url);
					}
					StringEntity sentity = null;
					if(!ientity.isEmpty()){
						try {
							sentity = new StringEntity(ientity,"UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						((HttpEntityEnclosingRequestBase) request).setEntity(sentity);
					}
				}
				
				client.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

			}catch( IllegalArgumentException e){
				return "<error>URI error</error>";
			}
			//	request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8;");
			if( header.indexOf(":")!=-1 ){
				String headers[] = header.split("\n");
				for( int n=0; n < headers.length; ++n ){
					String types[] = headers[n].split(":");
					request.setHeader(types[0], types[1]);
				}
			}
			request.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, redirect );

	        URI uri;
			try {
				uri = new URI(url);
			} catch (URISyntaxException e1) {
	             return "<error>URI error</error>";
			}
			int port = uri.getPort();
			if( port < 0 ){
				port = 80;
			}
			client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "AIRi");

	        try {
	            HttpResponse response = client.execute(request);

	            // Check if server response is valid
	            StatusLine status = response.getStatusLine();
	            if( status.getStatusCode() < HTTP_STATUS_OK || status.getStatusCode() >= 400 ) {
	            	String errmsg = "<status>"+status.getStatusCode()+" "+status.getReasonPhrase()+"</status>";
	                return errmsg;
	            }
	            // Cookie �����������i�[����

	            HttpEntity entity = response.getEntity();

	            if( enc.isEmpty() ){
	            	enc = "UTF-8";
		            if( entity.getContentType() != null ){
		            	String ct = entity.getContentType().getValue();
		            	int p = ct.indexOf("=");
		            	if( p != -1 ){
		            		enc = entity.getContentType().getValue().substring(p+1).trim();
		            	}
		            }
	            }

	            
	            if( !download.isEmpty() ){
		            InputStreamReader istreamReader;
		            InputStream content = entity.getContent();

		            StringBuffer sw = new StringBuffer();
		            sw.append("<status>"+response.getStatusLine().toString()+"</status>\n");
		            sw.append("<url>"+url+"</url>\n");
		            sw.append("<header>"); 
		            sw.append(getAllHeaders(response));
		            sw.append("</header>\n<entity><![CDATA[");
		            sw.append(saveFile(content));
		            sw.append("]]></entity>");
		            String msg = sw.toString();
		            return msg;

	            }else{

		            InputStreamReader istreamReader;
		            InputStream content = entity.getContent();

		            Header resheader = entity.getContentEncoding();
		            if( resheader != null) {
			            String encoding = resheader.getValue();
			            if( encoding.toLowerCase().indexOf("gzip")!=-1){
			            	Log.d("HtmlView","gzip");
			            	content = new GZIPInputStream(content);
			            }
		            }

		            try {
		            	istreamReader = new InputStreamReader( content, enc );
		            }catch(UnsupportedEncodingException e){
		            	istreamReader = new InputStreamReader( content );
		            }

		            // Pull content stream from response
		            int readBytes = 0;
		            char[] sBuffer = new char[20480];
		            StringWriter sw = new StringWriter( 204800 );
		            sw.write("<status>"+response.getStatusLine().toString()+"</status>\n");
		            sw.write("<url>"+url+"</url>\n");
		            sw.write("<header>"); 
		            sw.write(getAllHeaders(response));
		            sw.write("</header>\n<entity><![CDATA[");
		            while ((readBytes = istreamReader.read(sBuffer)) != -1) {
		            	sw.write(sBuffer,0,readBytes);
		            }
		            sw.write("]]></entity>");
		            String msg = sw.toString();
		            return msg;
	            }
	        } catch (IOException e) {
	        	String msg = "<error>IOException "+e.getMessage()+"/ "+
			            e.getLocalizedMessage()+"/ "+
			            e.getCause()+"/ "+
			            e.getStackTrace()+"</error>";
	            return msg+"<url>"+url+"</url>";
	        }
	    }
		String saveFile(InputStream content){
			String savedir = HtmlView.getPineDir();
	    	File foutdir = new File(savedir);
	    	if( !foutdir.exists() ){
	    		foutdir.mkdirs();
	    	}
		    String cappath = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	    	try {
	        	FileOutputStream f = new FileOutputStream( foutdir.getAbsolutePath()+"/"+cappath );

	            byte[] sBuffer = new byte[20480];
	            int readBytes;
	            while ((readBytes = content.read(sBuffer)) != -1) {
	            	f.write(sBuffer,0,readBytes);
	            }
				f.close();
				// ContentProviderへの登録
				String[] paths = {foutdir.getAbsolutePath()+"/"+cappath};
				String[] mimeTypes = {"application/octet-stream"};
				MediaScannerConnection.scanFile(mContext, paths, mimeTypes, null);
				
				return "file://"+foutdir.getAbsolutePath()+"/"+cappath;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
	    }

		private String getAllHeaders(HttpResponse response) {
			Header[] headers = response.getAllHeaders();
			StringBuffer allheaders = new StringBuffer("");
//			try {
//				allheaders.append("redirect:"+client.getRedirectHandler().getLocationURI(response, null)+";/ ");
//			} catch (ProtocolException e) {
//				allheaders.append(e.getMessage()+";/ ");
//			}
			for( int n=0; n<headers.length; ++n){
				allheaders.append(headers[n].getName()+":"+headers[n].getValue()+"\n");
			}
			return allheaders.toString();
		}
		//////////////////////////////////////////////////
		public void setActionbarTitle(String title){
			String UserScriptMark = "";
			switch(mScrType){
			case Asset:
				UserScriptMark = "";
				break;
			case Db:
				UserScriptMark = "UserScr* ";
				break;
			default:
				UserScriptMark = "Error ";
			}	
		    Intent intent = new Intent(mContext, HtmlBaseActivity.class);
		    intent.setData(Uri.parse(HtmlBaseActivity.SetActionbarURL+UserScriptMark+title));
		    ((HtmlBaseActivity)mContext).startActivity(intent);
		}
		public void pickImage(String callback) {
			put_cache( reqcodeToKeystr( HtmlBaseActivity.ReqPickImage ), callback );
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			((Activity) mContext).startActivityForResult(intent, HtmlBaseActivity.ReqPickImage);
		}
		public void pickFile(String stype, String callback) {
			put_cache( reqcodeToKeystr( HtmlBaseActivity.ReqPickImage ), callback );
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType(stype);
			((Activity) mContext).startActivityForResult(intent, HtmlBaseActivity.ReqPickImage);
		}
		
		public void getText(String wndtitle, String deftext, String callback, String refxml){
			put_cache( reqcodeToKeystr( HtmlBaseActivity.ReqText ), callback );
			Intent intent = new Intent(mContext, jp.co.aplabs.htmlbase.TextInputActivity.class);
			intent.putExtra(TextInputActivity.DefText, deftext);
			intent.putExtra(TextInputActivity.WndTitle, wndtitle);
			intent.putExtra(TextInputActivity.RefXML, refxml);
			intent.setType("text/*");
			intent.setAction(Intent.ACTION_MAIN);
			((Activity) mContext).startActivityForResult(intent, HtmlBaseActivity.ReqText);
		}
		////Twitter/////////////////////////////////////////////
		public void twStatus( final String tweetid, final String callback, final String errcallback){
			recInit("twStatus");
			recParam("tweetid",tweetid);
			recParam("callback",callback);
			recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			final String callbackPage = mCurPagename;

			AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
				String curModule = new String(mCurModule);
				@Override
				protected List<String> doInBackground(Void... params) {
					try {
						twitter4j.Status tweet = mTwitter.showStatus( Long.parseLong( tweetid ) );

						ArrayList<String> list = new ArrayList<String>();
					    list.add(status2xml(tweet));
						return list;
					} catch (TwitterException e) {
						getMomo().setItem("#exception", e.toString());
					}
					return null;
				}
				
				@Override
				protected void onPostExecute(List<String> result) {
		        	if( curModule.equals(mCurModule) ){
		        		postExecute( result, callback, callbackPage, errcallback, "タイムラインの取得に失敗しました。" );
		        	}
				}
			};
			task.execute();
		}

		public void twTimeline( final String sincestr, final String maxstr, final String callback, final String errcallback) {
			recInit("twTimeline");
			recParam("sincestr",sincestr);
			recParam("maxstr",maxstr);
			recParam("callback",callback);
			recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			final String callbackPage = mCurPagename;

		    AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
		    	String curModule = new String(mCurModule);
		        @Override
		        protected List<String> doInBackground(Void... params) {
		            try {
		            	Paging pg = mkPaging(sincestr, maxstr);
		                ResponseList<twitter4j.Status> timeline = mTwitter.getHomeTimeline(pg);

		                ArrayList<String> list = new ArrayList<String>();
		                for (twitter4j.Status status : timeline) {
		                    list.add(status2xml(status));
		                }
		                return list;
		            } catch (TwitterException e) {
		            	getMomo().setItem("#exception", e.toString());
		            }
		            return null;
		        }

		        @Override
		        protected void onPostExecute(List<String> result) {
		        	if( curModule.equals(mCurModule) ){
		        		postExecute(result, callback, callbackPage, errcallback, "タイムラインの取得に失敗しました。");
		        	}
		        }
		    };
		    task.execute();
		}

		public void twQuerry(final String querystr, final String sincestr, final String maxstr, 
									final String callback, final String errcallback) {
			recInit("twQuerry");
			recParam("querystr",querystr); recParam("sincestr",sincestr); recParam("maxstr",maxstr);
			recParam("callback",callback); recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			final String callbackPage = mCurPagename;

			AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
				String curModule = new String(mCurModule);
				@Override
				protected List<String> doInBackground(Void... params) {
				    try {
				    	Query	query = new twitter4j.Query(querystr);
				    	query.count(100);
						try{
							Long sinceid = Long.parseLong(sincestr);
				        	if( sinceid > 0 )
				        		query.setSinceId(sinceid);
						}catch(NumberFormatException e){}
						try{
							Long maxid = Long.parseLong(maxstr);
				        	if( maxid > 0 )
				        		query.setMaxId(maxid - 1);
						}catch(NumberFormatException e){}
				
						QueryResult timeline = mTwitter.search(query);
				
				    	List<twitter4j.Status> liststats = timeline.getTweets();
				        ArrayList<String> list = new ArrayList<String>();
				        for(twitter4j.Status status : liststats){
				            list.add(status2xml(status));
				        }
				        return list;
				    } catch (TwitterException e) {
				    	getMomo().setItem("#exception", e.toString());
				    }
				    return null;
				}
				
				@Override
				protected void onPostExecute(List<String> result) {
		        	if( curModule.equals(mCurModule) ){
		        		postExecute( result, callback, callbackPage, errcallback, "検索結果の取得に失敗しました。" );
		        	}
				}
			};
			task.execute();
		}

		public void twUserTimeline(final String uid, final String sincestr, final String maxstr, final String callback, final String errcallback) {
			recInit("twUserTimeline");
			recParam("uid",uid);
			recParam("sincestr",sincestr);
			recParam("maxstr",maxstr);
			recParam("callback",callback);
			recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			final String callbackPage = mCurPagename;

			AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
				String callerModule = new String(mCurModule);;
				@Override
				protected List<String> doInBackground(Void... params) {
					try {
						Paging pg = mkPaging(sincestr,maxstr);
						ResponseList<twitter4j.Status> timeline = mTwitter.getUserTimeline(Long.parseLong(uid),pg);

						ArrayList<String> list = new ArrayList<String>();
						for (twitter4j.Status status : timeline) {
						    list.add(status2xml(status));
						}
						return list;
					} catch (TwitterException e) {
						getMomo().setItem("#exception", e.toString());
					}
					return null;
				}
				
				@Override
				protected void onPostExecute(List<String> result) {
					if( callerModule.equals(mCurModule)) {
						postExecute( result, callback, callbackPage, errcallback, "タイムラインの取得に失敗しました。" );
					}
				}
			};
			task.execute();
		}

		public void twUserProfile(final String uid, final String callback, final String errcallback) {
			recInit("twUserProfile");
			recParam("uid",uid);
			recParam("callback",callback);
			recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			final String callbackPage = mCurPagename;

			AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
				String curModule = new String(mCurModule);
				@Override
				protected List<String> doInBackground(Void... params) {
					try {
						UsersResources ur = mTwitter.users();
						User user = ur.showUser(Long.parseLong(uid));

						ArrayList<String> list = new ArrayList<String>();
					    list.add(status2xml(user));
						return list;
					} catch (TwitterException e) {
						getMomo().setItem("#exception", e.toString());
					}
					return null;
				}
				
				@Override
				protected void onPostExecute(List<String> result) {
		        	if( curModule.equals(mCurModule) ){
		        		postExecute( result, callback, callbackPage, errcallback, "ユーザー情報の取得に失敗しました。" );
		        	}
				}
			};
			task.execute();
		}

		public void twMentions( final String sincestr, final String maxstr, final String callback, final String errcallback) {
			recInit("twMentions");
			recParam("sincestr",sincestr);
			recParam("maxstr",maxstr);
			recParam("callback",callback);
			recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			final String callbackPage = mCurPagename;

			AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
				String curModule = new String(mCurModule);
				@Override
				protected List<String> doInBackground(Void... params) {
				try {
					Paging pg = mkPaging(sincestr, maxstr);
					ResponseList<twitter4j.Status> timeline = mTwitter.getMentionsTimeline(pg);
				    ArrayList<String> list = new ArrayList<String>();
				    for (twitter4j.Status status : timeline) {
				        list.add(status2xml(status));
				    }
				    return list;
				} catch (TwitterException e) {
					getMomo().setItem("#exception", e.toString());
				}
				return null;
				}
				
				@Override
				protected void onPostExecute(List<String> result) {
		        	if( curModule.equals(mCurModule) ){
		        		postExecute( result, callback, callbackPage, errcallback, "通知の取得に失敗しました。" );
		        	}
				}
			};
			task.execute();
		}
		
		public void twLists( final String listid,  final String sincestr, final String maxstr, final String callback, final String errcallback) {
			recInit("twLists");
			recParam("listid",listid); recParam("sincestr",sincestr); recParam("maxstr",maxstr);
			recParam("callback",callback);
			recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			final String callbackPage = mCurPagename;

			AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
				String curModule = new String(mCurModule);
				@Override
				protected List<String> doInBackground(Void... params) {
				    try {
				    	Paging pg = mkPaging(sincestr,maxstr);
				    	int lid = (int) Math.floor(Double.parseDouble(listid));
				    	ResponseList<twitter4j.Status> ulist = mTwitter.getUserListStatuses(lid, pg);
				        ArrayList<String> list = new ArrayList<String>();
				        for (twitter4j.Status status : ulist) {
				            list.add(status2xml(status));
				        }
				        return list;
				    } catch (TwitterException e) {
				    	getMomo().setItem("#exception", e.toString());
				    }
				    return null;
				}
				
				@Override
				protected void onPostExecute(List<String> result) {
		        	if( curModule.equals(mCurModule) ){
		        		postExecute( result, callback, callbackPage, errcallback, "リストの取得に失敗しました。" );
		        	}
				}
			};
			task.execute();
		}

		public void twListLists( final String callback, final String errcallback) {
			recInit("twListLists");
			recParam("callback",callback);
			recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			final String callbackPage = mCurPagename;

		    AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
		    	String curModule = new String(mCurModule);
		        @Override
		        protected List<String> doInBackground(Void... params) {
		            try {
		            	ResponseList<UserList> ulist = mTwitter.list().getUserLists(mTwitter.getId());
		                ArrayList<String> list = new ArrayList<String>();
		                for (UserList status : ulist) {
		                    list.add(status2xml(status));
		                }
		                return list;
		            } catch (TwitterException e) {
		            	getMomo().setItem("#exception", e.toString());
		            }
		            return null;
		        }

		        @Override
		        protected void onPostExecute(List<String> result) {
		        	if( curModule.equals(mCurModule) ){
		        		postExecute( result, callback, callbackPage, errcallback, "リスト一覧の取得に失敗しました。" );
		        	}
		        }
		    };
		    task.execute();
		}

		public void twSavedSearch( final String callback, final String errcallback) {
			recInit("twSavedSearch");
			recParam("callback",callback);
			recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			final String callbackPage = mCurPagename;

			AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
				String curModule = new String(mCurModule);
		        @Override
		        protected List<String> doInBackground(Void... params) {
		            try {
		            	ResponseList<SavedSearch> slist = mTwitter.savedSearches().getSavedSearches();
		                ArrayList<String> list = new ArrayList<String>();
		                for (SavedSearch status : slist) {
		                    list.add(status2xml(status));
		                }
		                return list;
		            } catch (TwitterException e) {
		            	getMomo().setItem("#exception", e.toString());
		            }
		            return null;
		        }

		        @Override
		        protected void onPostExecute(List<String> result) {
		        	if( curModule.equals(mCurModule) ){
		        		postExecute( result, callback, callbackPage, errcallback, "保存済検索一覧の取得に失敗しました。" );
		        	}
		        }
		    };
		    task.execute();
		}

		public void twPutTweet(String str, String replyto, String callback, String errcallback){
			recInit("twPutTweet");
			recParam("str",str);
			recParam("replyto",replyto);
			recParam("callback",callback);
			recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			try {
				StatusUpdate su = new StatusUpdate(str);
				if( !replyto.isEmpty() ){
					su.setInReplyToStatusId(Long.parseLong(replyto));
				}
                jsCallback(mTwitter.updateStatus(su).toString(), callback, "#twitter",mCurPagename);
			} catch (TwitterException e) {
				e.printStackTrace();
				jsCallback(e.toString(), errcallback, "#twitter",mCurPagename);
			}
		}
		public void twReTweet(String id, String callback, String errcallback){
			recInit("twReTweet");
			recParam("id",id);
			recParam("callback",callback);
			recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			try {
				TweetsResources tr = mTwitter.tweets();
				Status st = tr.retweetStatus(Long.parseLong(id));
                jsCallback(st.toString(), callback, "#twitter",mCurPagename);
			} catch (TwitterException e) {
				e.printStackTrace();
				jsCallback(e.toString(), errcallback, "#twitter",mCurPagename);
			}
		}
		public void twFavTweet(String id, String callback, String errcallback){
			recInit("twFavTweet");
			recParam("id",id);
			recParam("callback",callback);
			recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			try {
				FavoritesResources fr = mTwitter.favorites();
				Status st = fr.createFavorite(Long.parseLong(id));
                jsCallback(st.toString(), callback, "#twitter",mCurPagename);
			} catch (TwitterException e) {
				e.printStackTrace();
				jsCallback(e.toString(), errcallback, "#twitter",mCurPagename);
			}
		}

		public void twFavorites( final String sincestr, final String maxstr, final String callback, final String errcallback) {
			recInit("twFavorites");
			recParam("sincestr",sincestr); recParam("maxstr",maxstr);
			recParam("callback",callback); recParam("errcallback",errcallback);
			if( mTwitter == null ){
				twitterAuthErrProc(errcallback);
				return;
			}
			final String callbackPage = mCurPagename;

			AsyncTask<Void, Void, List<String>> task = new AsyncTask<Void, Void, List<String>>() {
				@Override
				protected List<String> doInBackground(Void... params) {
					try {
						Paging pg = mkPaging(sincestr,maxstr);
						ResponseList<twitter4j.Status> timeline = mTwitter.getFavorites(pg);

						ArrayList<String> list = new ArrayList<String>();
						for (twitter4j.Status status : timeline) {
						    list.add(status2xml(status));
						}
						return list;
					} catch (TwitterException e) {
						getMomo().setItem("#exception", e.toString());
					}
					return null;
				}
				
				@Override
				protected void onPostExecute(List<String> result) {
		        	postExecute( result, callback, callbackPage, errcallback, "お気に入りの取得に失敗しました。" );
				}
			};
			task.execute();
		}

		public void twLogout(){
			mTwitter = null;
			((HtmlBaseActivity)mContext).twitterLogout();
		}

		public void twUploadImage(String uri, final String callback, final String errcallback) {
			recInit("twUploadImage");
			recParam("uri",uri);
			recParam("callback",callback);
			recParam("errcallback",errcallback);

			final String targetPath = content2path(uri);
			final String callbackPage = mCurPagename;

			Executors.newSingleThreadScheduledExecutor().submit(new Runnable() {
				public void run() {
					File file = new File(targetPath);
		        	ConfigurationBuilder builder = new ConfigurationBuilder();
					builder.setOAuthConsumerKey(TwitterUtils.getConsumerKey());
					builder.setOAuthConsumerSecret(TwitterUtils.getConsumerSecret());
					AccessToken at;
					try {
						at = mTwitter.getOAuthAccessToken();
						builder.setOAuthAccessToken(at.getToken());
						builder.setOAuthAccessTokenSecret(at.getTokenSecret());
						// ここでMediaProviderをTWITTERにする
						builder.setMediaProvider("TWITTER");

						Configuration conf = builder.build();

				//		ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance(MediaProvider.IMG_LY);;
				//		ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance(MediaProvider.LOCKERZ);;
				//		ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance(MediaProvider.MOBYPICTURE);;
				//		ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance(MediaProvider.POSTEROUS);;
				//		ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance(MediaProvider.PLIXI);;
						ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance(MediaProvider.TWIPPLE); //OK
				//		ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance(MediaProvider.TWITGOO);;
				//		ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance(MediaProvider.TWITPIC);;
				//		ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance(MediaProvider.TWITTER);;
				//		ImageUpload imageUpload = new ImageUploadFactory(conf).getInstance(MediaProvider.YFROG);;
						
						String reps = imageUpload.upload(file);	// 
		            	if( callbackPage.equals(mCurPagename)){
		            		goPage("javascript:"+callback+"('"+reps+"')");
		            	}
					} catch (TwitterException e) {
						e.printStackTrace();
						jsCallback(e.toString(), errcallback, "#twitter", callbackPage);
					}
				}
			});
		}

		String content2path(String cstr){
			return ((HtmlBaseActivity)mContext).content2path(cstr);
			/*
			Uri uri = Uri.parse(cstr);
			ContentResolver cr = mContext.getContentResolver();  
	        String[] columns = {MediaStore.Images.Media.DATA };  
	        Cursor c = cr.query(uri, columns, null, null, null);
	        c.moveToFirst();
			String rtn = c.getString(0);
	        return rtn;
	        */
	    }

		
		void jsCallback(String msg, String callback, String rtnitem, String callbackPage){
			if( callbackPage.endsWith(mCurPagename)){
				put_cache(rtnitem, msg);
				go("javascript:"+callback);
			}
		}
		
		void twitterAuthErrProc(String errcallback){
			((HtmlBaseActivity)mContext).twitterLogin();
			jsCallback("Not authorized, try again.", errcallback, "#twitter",mCurPagename);
		}
		
		private Paging mkPaging( String sincestr, String maxstr ){
			Paging pg = new Paging();
			Long sinceid = (long) 0;
			try{
				sinceid = Long.parseLong(sincestr);
            	if( sinceid > 0 )
            		pg.setSinceId(sinceid);
			}catch(NumberFormatException e){}
			Long maxid = (long) 0;
			try{
				maxid = Long.parseLong(maxstr);
            	if( maxid > 0 )
            		pg.setMaxId(maxid - 1);
			}catch(NumberFormatException e){}
			
			return pg;
		}
		
		private void postExecute(List<String> result, String callback, String callbackPage, String errcallback, String errmsg ) {
            if (result != null) {
                StringBuffer twStr = new StringBuffer();
                for (String status : result) {
                	twStr.append(status);
                }
				jsCallback(twStr.toString(), callback, "#twitter", callbackPage);
            } else {
				jsCallback(errmsg, errcallback, "#twitter", callbackPage);
            }
        }
				
		String status2xml(twitter4j.Status status){
			twitter4j.Status st = status;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
			StringBuffer sb = new StringBuffer();
			sb.append("<tweet>");
			Status rt = status.getRetweetedStatus();
			if( rt != null ){
				st = rt;
				sb.append(tagv2str("rtby", status.getUser().getScreenName()));
			}
			sb.append(tagv2str("tid", Long.toString(status.getId())));		// ツイートのID
			sb.append(tagv2str("id", Long.toString(st.getId())));			// ツイートまたはリツイート先のID
			sb.append(tagv2str("scname", st.getUser().getScreenName()));
			sb.append(tagv2str("uid", Long.toString(st.getUser().getId()) ));
			sb.append(tagv2str("fav", st.isFavorited()?"y":"n" ));
			sb.append(tagv2str("rtbyme", status.isRetweeted()?"y":"n" ));
			sb.append(tagv2str("name", st.getUser().getName()));
			sb.append(tagv2str("icon", st.getUser().getProfileImageURL()));
			sb.append(tagv2str("date", sdf.format(st.getCreatedAt()) ));
			sb.append(tagv2str("text", st.getText()));
			sb.append(tagv2str("replyto", Long.toString(st.getInReplyToStatusId()) ));

			URLEntity[] ue = st.getURLEntities();
			sb.append("<ues>");
			for( int n=0; n < ue.length; ++n ){
				sb.append("<ue>");
				sb.append(tagv2str("d", ue[n].getDisplayURL()));
				sb.append(tagv2str("e", ue[n].getExpandedURL()));
				sb.append(tagv2str("u", ue[n].getURL()));
				sb.append("</ue>");
			}
			sb.append("</ues>");
			sb.append("</tweet>");
			return sb.toString();
		}
		String status2xml(UserList status){
			StringBuffer sb = new StringBuffer();
			sb.append("<tweet>");
			sb.append(tagv2str("id", Long.toString(status.getId())));
			sb.append(tagv2str("scname", status.getFullName()));
			sb.append(tagv2str("name", status.getName()));
			sb.append(tagv2str("icon", status.getUser().getProfileImageURL()));
			sb.append(tagv2str("text", status.getDescription()));
			sb.append(tagv2str("count",	String.format("%d-%d", status.getMemberCount(), status.getSubscriberCount()) ));
			sb.append(tagv2str("access", status.isPublic()?"public":"private"));
			sb.append("</tweet>");
			return sb.toString();
		}
		String status2xml(User status){
			StringBuffer sb = new StringBuffer();
			sb.append("<tweet>");
			sb.append(tagv2str("id", Long.toString(status.getId())));
			sb.append(tagv2str("scname", status.getScreenName()));
			sb.append(tagv2str("name", status.getName()));
			sb.append(tagv2str("icon", status.getProfileImageURL()));
			sb.append(tagv2str("text", status.getDescription()));
			sb.append("</tweet>");
			return sb.toString();
		}
		String status2xml(SavedSearch status){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
			StringBuffer sb = new StringBuffer();
			sb.append("<tweet>");
			sb.append(tagv2str("id", Long.toString(status.getId())));
			sb.append(tagv2str("date", sdf.format(status.getCreatedAt())));
			sb.append(tagv2str("name", status.getName()));
			sb.append(tagv2str("query", status.getQuery()));
			sb.append(tagv2str("pos", Integer.toString(status.getPosition()) ));
			sb.append("</tweet>");
			return sb.toString();
		}
		
		String tagv2str(String tag, String val){
			return "<"+tag+">"+val+"</"+tag+">";
		}
		
		void toast(String msg){
			HtmlView.toast(msg);
		}
	}
}
