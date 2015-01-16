
package org.solarex.wm2service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import org.solarex.wm2service.IWm2SupportService;
import org.solarex.wm2service.IWm2Callback;
public class WM2Service extends Service {
    private static final String TAG = "WM2Service";
    private IWm2Callback mCallback;
    private View mPopView;
    WindowManager wm = null;

    WindowManager.LayoutParams wmParams = null;
    private float mTouchStartX;  
    private float mTouchStartY;  
  
    private float x;  
    private float y;

    private String mFocus;
    private Handler mHandler;
    private MyConn mConn;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mPopView = LayoutInflater.from(this).inflate(R.layout.pop_view_layout, null);
        HandlerThread mBgThread = new HandlerThread("IWm2SupportService");
        mBgThread.start();
        mHandler = new Handler(mBgThread.getLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if(wm != null && mPopView != null){
            wm.removeView(mPopView);
            mPopView = null;
        }
        Log.d(TAG, "onDestroy,mPopView = " + mPopView);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind intent = " + intent);
        mConn = new MyConn();
        mHandler.postDelayed(new Runnable(){

            @Override
            public void run() {
                try{
                    Log.d(TAG, "onBind onSuccess mCallback = " + mCallback);
                    if(mCallback != null){
                        mCallback.onSuccess(mFocus+",success");
                    }
                }catch(RemoteException e){
                    Log.d(TAG, "onBind exception = " + e.getMessage());
                }
            }
            
        }, 1000);
        mHandler.postDelayed(new Runnable(){

            @Override
            public void run() {
                try{
                    Log.d(TAG, "onBind onFailue mCallback = " + mCallback);
                    if(mCallback != null){
                        mCallback.onFailure(mFocus+",failure");
                    }
                }catch(RemoteException e){
                    Log.d(TAG, "onBind exception = " + e.getMessage());
                }
            }
            
        }, 2000);
        createView();
        return mConn;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean flag = super.onUnbind(intent);
        Log.d(TAG, "onUnbind");
        if(wm != null && mPopView != null){
            wm.removeView(mPopView);
        }
        return flag;
    }

    private class MyConn extends IWm2SupportService.Stub{
        @Override
        public void setPackage(String packageName){
            if("solarex".equals(packageName)){
                mFocus = "TurnOnTheMusic";
            } else {
                mFocus = "UnknownPackage";
            }
        }

        @Override
        public void setCallback(IWm2Callback callback){
            mCallback = callback;
        }
    }

    public void createView(){
        wm = (WindowManager) getApplicationContext().getSystemService("window");  
        
        wmParams = new WindowManager.LayoutParams();  
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;  
        wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;  
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;  
  
        wmParams.x = 0;  
        wmParams.y = 0;  
  
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
        wmParams.format = PixelFormat.RGBA_8888;  
        Log.d(TAG, "createView wm = " + wm + ", params = " + wmParams);
        wm.addView(mPopView, wmParams);  
  
        mPopView.setOnTouchListener(new OnTouchListener() {  
  
            public boolean onTouch(View v, MotionEvent event) {

                x = event.getRawX();
                y = event.getRawY();
                Log.d(TAG, "createView x = " + x + ",y = " + y);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY() + mPopView.getHeight() / 2;
                        Log.d(TAG, "createView ACTION_DOWN startX = " + mTouchStartX + ",startY = " + mTouchStartY);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "createView ACTION_MOVE");
                        updateViewPosition();
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "createView ACTION_UP");
                        updateViewPosition();
                        mTouchStartX = mTouchStartY = 0;
                        break;
                }
  
                return true;  
            }  
        });  
    }  
  
    private void updateViewPosition() {  
        wmParams.x = (int) (x - mTouchStartX);  
        wmParams.y = (int) (y - mTouchStartY);  
        Log.d(TAG, "updateViewPosition params.x = " + wmParams.x + ",params.y = " + wmParams.y);
        wm.updateViewLayout(mPopView, wmParams);  
    } 
}
