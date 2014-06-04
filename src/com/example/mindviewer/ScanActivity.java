package com.example.mindviewer;

import com.example.mindviewer.BlueSmirf.BlueSmirfSPP;
import com.example.mindviewer.Widget.CircularProgressBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
	BlueSmirfSPP mSPP;
	Brainwaves bWave;
	CircularProgressBar cAtt;
	CircularProgressBar cMed;
	
	public ScanActivity(Context context, BlueSmirfSPP mSPP) {
		mContext 				   	= context;
		bWave 						= Brainwaves.getInstance();
		this.mSPP 				   	= mSPP;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_scan, null);
		
		PrintThread thread = new PrintThread();
		thread.setDaemon(true);
		thread.start();		
		
		cAtt = (CircularProgressBar) view.findViewById(R.id.circle_attention);
		cAtt.setSubTitle("Attention");
		cMed = (CircularProgressBar) view.findViewById(R.id.circle_meditation);
		cMed.setSubTitle("Meditation");
		
		return view;
	}
	@SuppressLint("NewApi")
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    setUserVisibleHint(true);
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		}	
	}

	Handler mHandler = new Handler();
	
	class PrintThread extends Thread{
		public void run(){
			while(true){
				mHandler.post(new Runnable(){
					public void run() {
						// TODO Auto-generated method stub
						cAtt.setTitle(Integer.toString(bWave.getAtt()) + "%");
						cAtt.setProgress(bWave.getAtt());
						cMed.setTitle(Integer.toString(bWave.getMed()) + "%");
						cMed.setProgress(bWave.getMed());						
					}
				});
				try{
					Thread.sleep(1000);
				} catch(InterruptedException e){;}
			}
		}
	}
}

