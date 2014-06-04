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
public class ScanActivity extends Fragment implements OnClickListener {
	Context mContext;

	Button test_arduino;
	private BlueSmirfSPP mSPP;
	
	public ScanActivity(Context context, BlueSmirfSPP mSPP) {
		mContext = context;
		this.mSPP = mSPP;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_scan, null);
		
		test_arduino = (Button) view.findViewById(R.id.test_arduino);
		
		CircularProgressBar cAtt = (CircularProgressBar) view.findViewById(R.id.circle_attention);
		cAtt.setTitle("44");
		cAtt.setSubTitle("Attention");
		cAtt.setProgress(44);
		
		CircularProgressBar cMed = (CircularProgressBar) view.findViewById(R.id.circle_meditation);
		cMed.setTitle("74");
		cMed.setSubTitle("Meditation");
		cMed.setProgress(74);
		
		
		test_arduino.setOnClickListener(this);
		
		
    	return view;
	}
	@SuppressLint("NewApi")
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    setUserVisibleHint(true);
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.test_arduino :
			onToggleLED(v);
		break;
		}	
	}
}
