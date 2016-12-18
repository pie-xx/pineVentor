package jp.co.aplabs.htmlbase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class PicMonActivity extends Activity 
		implements SurfaceHolder.Callback {
	public static final String FOLDER_DEF = "capfolder";
	private static final String TAG = "ZXingTest";
	public static final String CapFile = "CapFile";
	public static final String TimerValue = "TimerValue";
	     
	private static final int MIN_PREVIEW_PIXCELS = 320 * 240;
	private static final int MAX_PREVIEW_PIXCELS = 800 * 480;

	private Camera myCamera;
	private SurfaceView surfaceView;
	     
	private Boolean hasSurface;    
	private Boolean initialized;
	
	private Point screenPoint;
	private Point previewPoint;

	private Button		mShutterBtn;
	
	private String	resolutionList;
	public String	getResolutionList(){
		return resolutionList;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
         
        hasSurface = false;
        initialized = false;

        bis = null;
        inShutter = false;
        setContentView(R.layout.picmon);

        mShutterBtn = (Button)findViewById(R.id.button_take_picture);
        mShutterBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				takePicture();
			}});

        Intent intent = getIntent();

        int timervalue = intent.getExtras().getInt(TimerValue);
        
        if( timervalue > 0 ) {
			new Timer().schedule(new TimerTask() {
				public void run() {
			    	   takePicture();
				}
			}, timervalue);
        }
	}
	
	public Momo getMomo() {
		return HtmlBaseActivity.mHtmlView.getMomo();
	}

    @SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
        super.onResume();
         
        surfaceView = (SurfaceView)findViewById(R.id.preview_view);
        SurfaceHolder holder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(holder);
        } else {
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onPause() {
    	inPrvShutter = true;
        if (!hasSurface) {
            SurfaceHolder holder = surfaceView.getHolder();
            holder.removeCallback(this);
        }
        closeCamera();

        if( bis != null ){
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bis = null;
		}

        super.onPause();
    }
	// onKeyDown() �ŏ�������ƃs�b�Ƃ����L�[�����łĂ��܂�
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch( event.getKeyCode() ) {
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if( event.getAction()==KeyEvent.ACTION_DOWN )
				takePicture();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	private void takePicture(){
		inShutter = true;
    	myCamera.autoFocus(new Camera.AutoFocusCallback(){
			@Override
			public void onAutoFocus(boolean arg0, Camera arg1) {
				myCamera.takePicture(null, null, new Camera.PictureCallback() {	
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						bis = new ByteArrayInputStream(data);
						LastCapFile = savePic(data);
						inShutter = false;
//						camera.startPreview();
				        Intent intent = new Intent();
				        intent.putExtra(CapFile, LastCapFile );
				        setResult(RESULT_OK, intent);
				        finish();
				    }
				});
			}});
	}

	/** SurfaceHolder.Callback */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }
 
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }
 
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
 
    }

    @SuppressLint({ "SimpleDateFormat" })
	String savePic(byte[] data){
		String savedir = HtmlView.getPineDir();
    	File foutdir = new File(savedir);
    	if( !foutdir.exists() ){
    		foutdir.mkdirs();
    	}
	    String cappath = new SimpleDateFormat("yyyyMMddHHmmssSSS'.jpg'").format(new Date());
    	try {
        	FileOutputStream f = new FileOutputStream( foutdir.getAbsolutePath()+"/"+cappath );
			f.write(data);
			f.close();
			// ContentProviderへの登録
			String[] paths = {foutdir.getAbsolutePath()+"/"+cappath};
			String[] mimeTypes = {"image/jpeg"};
			MediaScannerConnection.scanFile(getApplicationContext(), paths, mimeTypes, null);
			
			return "file://"+foutdir.getAbsolutePath()+"/"+cappath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
    }
    
    boolean inShutter = false;
    boolean inPrvShutter = false;
    public void shutter(){
    	inShutter = true;
        myCamera.autoFocus(null);
    }
    /** devices */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (myCamera != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
            	myCamera.autoFocus(null);
            }
        }
        return true;
    }

    public ByteArrayInputStream bis = null;
    public ByteArrayInputStream prv = null;
    public String LastCapFile = "";

    public String getCap(){
    	if(!inShutter){
    		takePicture();
    	}
    	while(inShutter){
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
    	}
    	return LastCapFile;
    }
    public InputStream getCap2(){
    	if(!inShutter){
    		shutter();
    	}
    	while(inShutter){
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
    	}
    	return rtnIs(bis);
    }
    public InputStream getFocus(){
    	if(!inShutter){
        	myCamera.autoFocus(new Camera.AutoFocusCallback(){
				@Override
				public void onAutoFocus(boolean arg0, Camera arg1) {
					inShutter = false;
				}});
    	}
    	while(inShutter){
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
    	}
    	return rtnIs(prv);
    }
    InputStream rtnIs(InputStream is){
    	if( is != null ){
    		try {
				is.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		return is;
    	}
    	return null;
    }
    public InputStream getPreview(){
    	if(!inPrvShutter){
    		inPrvShutter = true;
  
    		myCamera.setPreviewCallback(new Camera.PreviewCallback() {
					@SuppressLint("NewApi")
					@Override
					public void onPreviewFrame(byte[] data, Camera camera) {
				        if (data == null)
				            return;
				        camera.addCallbackBuffer(data);
					    Camera.Parameters parameters = camera.getParameters();
					    Size size = camera.getParameters().getPreviewSize();
	
					    YuvImage image = new YuvImage(data, parameters.getPreviewFormat(),
					    		size.width, size.height, null);
					    ByteArrayOutputStream out = new ByteArrayOutputStream();
	
					    image.compressToJpeg(
				            new Rect(0, 0, image.getWidth(), image.getHeight()), 40, out);
					    prv = new ByteArrayInputStream(out.toByteArray());
						inPrvShutter = false;
				    }
    			}
    		);

        	while(inPrvShutter){
        		try {
    				Thread.sleep(10);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    				return null;
    			}
        	}
        	myCamera.setPreviewCallback(null);
        }
		return rtnIs(prv);
    }
    /**
     * カメラ�??を�?期化
     * @param holder
     */
    private void initCamera(SurfaceHolder holder) {
        try {
            openCamera(holder);
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }
     
    private void openCamera(SurfaceHolder holder) throws IOException {
        if (myCamera == null) {
            myCamera = Camera.open();
            if (myCamera == null) {
                throw new IOException();
            }
        }
        myCamera.setPreviewDisplay(holder);
         
        if (!initialized) {
            initialized = true;
            initFromCameraParameters(myCamera);
        }
         
        setCameraParameters(myCamera);
        myCamera.startPreview();
    }
     
    /**
     * カメラ�??を�?�?
     */
    private void closeCamera() {
        if (myCamera != null) {
            myCamera.stopPreview();
            myCamera.release();
            myCamera = null;
        }
    }
     
    /**
     * カメラ�??を設�?
     * @param camera
     */
    private void setCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
         
        parameters.setPreviewSize(previewPoint.x, previewPoint.y);
        camera.setParameters(parameters);
    }
     
    /**
     * カメラのプレビューサイズ・画面サイズを設�?
     * @param camera
     */
    @SuppressWarnings("deprecation")
	private void initFromCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        WindowManager manager = (WindowManager)getApplication().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
         
        if (width < height) {
            int tmp = width;
            width = height;
            height = tmp;
        }
        screenPoint = new Point(width, height);
        Log.d(TAG, "screenPoint = " + screenPoint);
        previewPoint = findPreviewPoint(parameters, screenPoint, false);
        Log.d(TAG, "previewPoint = " + previewPoint);
    }
     
    /**
     * �?��なプレビューサイズを設�?
     * @param parameters
     * @param screenPoint
     * @param portrait
     * @return
     */
    private Point findPreviewPoint(Camera.Parameters parameters, Point screenPoint, boolean portrait) {
        Point previewPoint = null;
        int diff = Integer.MAX_VALUE;
        StringBuffer reso = new StringBuffer("<resos>");
         
        for (Camera.Size supportPreviewSize : parameters.getSupportedPreviewSizes()) {
        	reso.append("<reso><width>"+Integer.toString(supportPreviewSize.width)+"</width>");
        	reso.append("<height>"+Integer.toString(supportPreviewSize.height)+"</height></reso>");
        	int pixels=supportPreviewSize.width * supportPreviewSize.height;
            if (pixels < MIN_PREVIEW_PIXCELS || pixels > MAX_PREVIEW_PIXCELS) {
                continue;
            }
             
            int supportedWidth = portrait ? supportPreviewSize.height : supportPreviewSize.width;
            int supportedHeight = portrait ? supportPreviewSize.width : supportPreviewSize.height;
            int newDiff = Math.abs(screenPoint.x * supportedHeight - supportedWidth * screenPoint.y);
             
            if (newDiff == 0) {
                previewPoint = new Point(supportedWidth, supportedHeight);
                break;
            }
             
            if (newDiff < diff) {
                previewPoint = new Point(supportedWidth, supportedHeight);
                diff = newDiff;
            }
        }
        if (previewPoint == null) {
            Camera.Size defaultPreviewSize = parameters.getPreviewSize();
            previewPoint = new Point(defaultPreviewSize.width, defaultPreviewSize.height);
        }

        reso.append("</resos>");
        resolutionList = reso.toString();
        getMomo().setItem("reso", resolutionList);
        try {
	        previewPoint.x = Integer.parseInt(getMomo().getItem("pWidth"));
	        previewPoint.y = Integer.parseInt(getMomo().getItem("pHeight"));
        }catch(NumberFormatException e){
            previewPoint.x = 176; previewPoint.y = 144;        	
        }
        return previewPoint;
    }

    public InputStream Capture(){
    	if(!inShutter){
    		shutter();
    	}
    	while(inShutter){
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
    	}
    	return rtnIs(bis);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
}
