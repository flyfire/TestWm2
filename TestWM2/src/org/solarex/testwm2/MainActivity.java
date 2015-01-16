package org.solarex.testwm2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.solarex.wm2service.IWm2SupportService;
import org.solarex.wm2service.IWm2Callback;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private TextView mTvResult,mTvFailure;
    private Button mBtnBind,mBtnUnbind;
    private IWm2SupportService mService;
    private IWm2Callback mCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.main);
        mTvResult = (TextView)findViewById(R.id.success);
        mTvFailure = (TextView)findViewById(R.id.failure);
        mBtnBind = (Button)findViewById(R.id.bind);
        mBtnUnbind = (Button)findViewById(R.id.unbind);
        mCallback = new CallbackClass();
        mBtnBind.setOnClickListener(this);
        mBtnUnbind.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if(mConn != null){
            Log.d(TAG, "onStop | unbindService");
            this.unbindService(mConn);
            mConn = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if(mConn != null){
            Log.d(TAG, "onDestroy | unbindService");
            this.unbindService(mConn);
            mConn = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bind:
                Log.d(TAG, "onClick bind");
                bindService(new Intent("org.solarex.wm2service.IWm2SupportService"), mConn, Context.BIND_AUTO_CREATE);
                v.setClickable(false);
                break;
            case R.id.unbind:
                Log.d(TAG, "onClick unbind");
                if(mConn != null){
                    this.unbindService(mConn);
                    mConn = null;
                }
                if(mConn == null){
                    v.setClickable(false);
                }
                break;
            default:
                Log.d(TAG, "onClick unknown component clicked");
                break;
        }
    }
    private ServiceConnection mConn = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IWm2SupportService.Stub.asInterface(service);
            Log.d(TAG, "onServiceConnected mService = " + mService);
            try{
                mService.setPackage("solarex");
                mService.setCallback(mCallback);
            }catch(RemoteException e){
                Log.d(TAG, "onServiceConnected exception = " + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            try{
                mService.setCallback(null);
                mService = null;
            }catch(RemoteException e){
                Log.d(TAG, "onSeviceDisconnected exception = " + e.getMessage());
            }
        }
        
    };
    private class CallbackClass extends IWm2Callback.Stub{
        @Override
        public void onSuccess(String result){
            final String tmp = result;
            Log.d(TAG, "IWm2Callback.Stub | onSuccess result = " + result);
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    mTvResult.setText(tmp);
                }
                
            });
        }
        @Override
        public void onFailure(String errorMsg){
            final String tmp = errorMsg;
            Log.d(TAG, "IWm2Callback.Stub | onFailure error = " + errorMsg);
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    mTvFailure.setText(tmp);
                }
                
            });
        }
    }
}
