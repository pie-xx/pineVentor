package jp.co.aplabs.htmlbase;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Momo {
	private static final int IDITEM = 0;
	private static final int TITLEITEM = 1;
	private static final int BODYITEM = 2;
	private static final int MAXver = 3;
	private static SQLiteDatabase momodb;
	private static Context context;

	public static enum ITEMTYPE {None, Db, Asset};

	public Momo( Context _context ){
		context = _context;
		momodb = new MomoHelper( context ).getDB();
	}

	public void put( String title, String body ){
    	ContentValues insData = new ContentValues();
    	insData.put("title", title);
    	insData.put("body", body);
    	momodb.insert("momos", null, insData);

    	Cursor cur = momodb.query("momos", null, "title='"+title+"'", null, null, null, "title, id desc", null);
    	int count = cur.getCount();
    	for( int n=MAXver; n < count; ++n ){
    		cur.moveToPosition(n);
    		remove( cur.getInt( IDITEM ) );	
    	}
	}
	
    public void remove( int id ){
    	momodb.delete("momos", "id="+Integer.toString(id), null);
    }

///////// WebStrage ///////////////////////////////////////////////////////////////////////
    public int length(){
    	Cursor cur = momodb.query(true, "momos", new String[]{"title"}, null, null, null, null, null, null);
    	int rtv = cur.getCount();
    	return rtv;
    }
    
    public String key( int n ){
       	Cursor cur = momodb.query(true, "momos", new String[]{"title"}, null, null, null, null, null, null);
    	String rtv = "";
    	if(cur.moveToPosition(n))
    		rtv = cur.getString(0);
    	return rtv;
    }
    
    public InputStream getBinItem( String key ){
	    AssetManager am = context.getResources().getAssets();
	    InputStream inp;
	    try {
	        inp = am.open(key);
	        return inp;
	    }catch(IOException e) {
	        return null;
	    }
    }
    public ITEMTYPE checkItem( String key ){
		ITEMTYPE rtv = ITEMTYPE.None;
		Cursor cur = momodb.query("momos", null, "title='"+key+"'", null, null, null, "title, id desc", "1");
		if( cur.getCount()!=0 ){
				rtv = ITEMTYPE.Db;
		}else{
			rtv = ITEMTYPE.Asset;
		}
    	return rtv;
    }
    public String getItem( String key ){
		Cursor cur = momodb.query("momos", null, "title='"+key+"'", null, null, null, "title, id desc", "1");
		String rtv = "";
		if( cur.getCount()!=0 ){
			if(cur.moveToFirst()){
				rtv = cur.getString(BODYITEM);
			}
		}else{
			rtv = getAsset(key);
		}
		cur.close();
    	return rtv;
    }
    
    public void setItem( String title, String body ){
    	ContentValues insData = new ContentValues();
    	Cursor cur = momodb.query("momos", null, "title='"+title+"'", null, null, null, "title, id desc", "1");
    	int id=0;
    	if( cur.moveToFirst() ){
    		id = cur.getInt(IDITEM);
    	}
    	insData.put("title", title);
    	insData.put("body", body);
    	if( id > 0 ){
    		momodb.update("momos", insData, "id="+id, null);
    	}else{
    		momodb.insert("momos", null, insData);
    	}
    }

    public String listItem(){
		return listItem(null);
    }
    
    public String listItem( String selection){
		Cursor cur = momodb.query("momos", null, selection, null, null, null, "title, id desc");
		StringBuffer sb = new StringBuffer();
		sb.append("<items>");
		cur.moveToFirst();
		sb.append("<count>"+Integer.toString(cur.getCount())+"</count>");
		for(int n=0; n < cur.getCount(); ++n ){
			sb.append("<item>");
			sb.append("<name>"+cur.getString(TITLEITEM)+"</name>");
			sb.append("<id>"+cur.getString(IDITEM)+"</id>");
			sb.append("</item>");
			cur.moveToNext();
		}
		sb.append("</items>");
		return sb.toString();
    }
   
    public void removeItem( String key ){
    	momodb.delete("momos", "title='"+key+"'", null);
    }
    
    public void clear(){
    	momodb.execSQL("DELETE FROM momos;");
    }
////////////////////////////////////////////////////
	public static String getAsset( String assetfile ){
		try {
			InputStreamReader isr = new InputStreamReader(context.getAssets().open(assetfile), "utf-8");
            int readBytes = 0;
            char[] sBuffer = new char[2048];
            StringWriter sw = new StringWriter( 2048 );
            while ((readBytes = isr.read(sBuffer)) != -1) {
            	sw.write(sBuffer,0,readBytes);
            }
            return sw.toString();
		} catch (IOException e) {
	        return "";
		}
	}
}

class MomoHelper extends SQLiteOpenHelper {
	public static final String TAG = "PCamroid";
	private final static String DB_TABLE = "momos";
	private final static String DB_NAME = "momo.db";
	private final static int DB_VERSION = 1;
	
	public MomoHelper( Context context ){
		super(context, DB_NAME, null, DB_VERSION );
	}

	public SQLiteDatabase getDB(){
		try {
			return getWritableDatabase();
		}catch( SQLiteException e ){
			return getReadableDatabase();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL("CREATE TABLE "+ DB_TABLE +
			" (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, body TEXT)");
		ContentValues insData = new ContentValues();

		try {
			JSONObject defvs = new JSONObject( Momo.getAsset("defvs.json") );
			Iterator<JSONObject> keys = defvs.keys();
			while(keys.hasNext()){
				String key = keys.next().toString();
				String value = defvs.getString(key);
				Log.d(TAG,key+"="+value);
				insData.clear();
				insData.put("title", key );
				insData.put("body", value );
				db.insert(DB_TABLE, null, insData);				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ){
	}
}