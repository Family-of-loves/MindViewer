package com.example.mindviewer;

import com.example.mindviewer.BlueSmirf.BlueSmirfSPP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressLint("ValidFragment")
public class ManualActivity extends Fragment implements OnClickListener {
	Context mContext;
	private BlueSmirfSPP mSPP;

	
	Button btn_test;
	public ManualActivity(Context context, BlueSmirfSPP mSPP) {
		mContext = context;
		this.mSPP = mSPP;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_manual, null);
		btn_test = (Button) view.findViewById(R.id.button1);
		btn_test.setOnClickListener(this);
		
		System.out.println("mSPP Addr > " + mSPP.getBluetoothAddress());
		System.out.println("mSPP isConn > " + mSPP.isConnected());
		
    	return view;
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.button1 :
				onToggleLED(v);
			break;
		}
	}
	
	public void onToggleLED(View view){
		System.out.println("mSPP Addr > " + mSPP.getBluetoothAddress());
		System.out.println("mSPP isConn > " + mSPP.isConnected());
		
		String message = "1";
		
		if(mSPP.isConnected())
		{
			byte[] send = message.getBytes();
			
			mSPP.write(send, 0, send.length);
			//mStateLED = 1 - mStateLED;
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    setUserVisibleHint(true);
	}

}
