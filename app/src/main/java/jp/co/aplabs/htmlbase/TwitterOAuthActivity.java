package jp.co.aplabs.htmlbase;

import android.R.drawable;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

public class TwitterOAuthActivity extends Activity {
	LinearLayout	mBody;
	Button	mButton;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		mBody = new LinearLayout(this);

		mButton = new Button(this);
		mButton.setText("Twiiter Login");
		mBody.addView(mButton);

		setContentView(mBody);
    }
}
