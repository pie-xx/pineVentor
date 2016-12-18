package jp.co.aplabs.htmlbase;

import jp.co.aplabs.htmlbase.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
/***
 * 
 * 
 * @author pie
 *
 *	<L1>
 *		<id>
 *		<title>
 *		<copy>
 *		<desc>
 *		<L2>
 *			<id>
 *			<title>
 *			<desc>
 *		</L2>
 *		<L2></L2>
 *	</L1>
 *	<L1></L1>
 *
 */
public class TextInputActivity extends Activity   {
	public static final String DefText = "DefText";
	public static final String WndTitle = "WndTitle";
	public static final String RefXML = "RefXML";
	
	String mRefXMLs[];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.text_input);

		final TextView cv = (TextView)findViewById(R.id.textView1);

        Intent intent = getIntent();

        getActionBar().setTitle( intent.getExtras().getString( WndTitle ) );

        final EditText tv = (EditText) findViewById(R.id.editText1);
        String entry = intent.getExtras().getString( DefText );
        tv.setText( entry );
        cv.setText( Integer.toString( entry.length() ));
        
        // 書き込みボタンの設定
        Button enterbtn = (Button)findViewById(R.id.enterbtn);
        enterbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText tv = (EditText) findViewById(R.id.editText1);
		        Intent intent = new Intent();
		        intent.putExtra(DefText, tv.getText().toString() );
		        setResult(RESULT_OK, intent);
		        finish();
			}
		});

        // 編集中の文字数を表示
        tv.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				cv.setText(Integer.toString(s.length()));				
		}});
        
        tv.requestFocus();

        // spinner の設定
        final Spinner spinnerL1 = (Spinner) findViewById(R.id.spinner1);
        final Spinner spinnerL2 = (Spinner) findViewById(R.id.spinner2);
        Button pastebtn = (Button) findViewById(R.id.pastebtn);

        String refXML = intent.getExtras().getString( RefXML );
        if( refXML.isEmpty() ){
        	spinnerL1.setVisibility(View.INVISIBLE);
        	spinnerL2.setVisibility(View.INVISIBLE);
        	pastebtn.setVisibility(View.INVISIBLE);
        }else{
        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	       	final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	        mRefXMLs = refXML.split("<L1>");
        	for( int n=1; n < mRefXMLs.length; ++n ){
    	        adapter.add(tag2str("id", mRefXMLs[n]));
        	}
        	
	        spinnerL1.setAdapter(adapter);
	        spinnerL1.setOnItemSelectedListener( new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					String L2s[] = mRefXMLs[position+1].split("<L2>");
					adapter2.clear();
					for( int n=1; n< L2s.length; ++n){
						String item = tag2str("id",L2s[n]);
						int pos = adapter2.getPosition(item);
						if( pos == -1 ){
							adapter2.add(item);
						}
					}
			        spinnerL2.setAdapter(adapter2);
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
			});
	        
	        pastebtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					String cat = (String) spinnerL1.getSelectedItem();
					if( !cat.isEmpty() ){
						cat = cat + ".";
					}
					String replaceText = cat+spinnerL2.getSelectedItem();
					
					int start = tv.getSelectionStart();
					int end = tv.getSelectionEnd();
					Editable editable = (Editable) tv.getText();
					editable.replace( Math.min( start, end ), Math.max( start, end ), replaceText );
				}}
	        );
        }
	}
	String tag2str(String tagname, String tagstr){
        String[] items = tagstr.split("<"+tagname+">");
        if( items.length < 2 )
        	return "";
        return items[1].split("</"+tagname+">")[0];
	}


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	    menu.clear();
	    menu.add("Intent");
	    menu.add("Back");
	    
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if( item.getTitle().equals("Intent")){
			EditText tv = (EditText) findViewById(R.id.editText1);
	        Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, tv.getText().toString());
			startActivity(intent);
    		return true;
    	}
    	finish();
		return true;
    }

}
