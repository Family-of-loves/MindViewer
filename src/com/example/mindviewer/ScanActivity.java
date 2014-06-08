package com.example.mindviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.mindviewer.BlueSmirf.BlueSmirfSPP;
import com.example.mindviewer.Widget.CircularProgressBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

@SuppressLint("ValidFragment")
public class ScanActivity extends Fragment implements OnClickListener {
	Context mContext;

	Button test_arduino;
	BlueSmirfSPP mSPP;
	Brainwaves bWave;
	CircularProgressBar cAtt;
	CircularProgressBar cMed;
	
	//LinearLayout linearChart;
	
	public ScanActivity(Context context, BlueSmirfSPP mSPP) {
		this.mContext 				   	= context;
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
		
		/*linearChart = (LinearLayout) view.findViewById(R.id.linearChart);
		int colerloop[] = { 1, 2, 2, 2, 3, 3, 3, 3, 1, 1 }; 
		int heightLoop[] = { 300, 200, 200, 200, 100, 100, 100, 100, 300, 300 }; 
		for (int j = 0; j < colerloop.length; j++) { 
			drawChart(1, colerloop[j], heightLoop[j], view); 
		}*/

		return view;
	}
	@SuppressLint("NewApi")
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    setUserVisibleHint(true);
	}
	
	/*public void drawChart(int count, int color, int height, View v) { 
		System.out.println(count + color + height); 
		if (color == 3) {
			color = Color.RED; 
		} else if (color == 1) { 
			color = Color.BLUE; 
		} else if (color == 2) { 
			color = Color.GREEN;
		} 
		
		for (int k = 1; k <= count; k++) { 
			View view = new View(v.getContext());
			view.setBackgroundColor(color); 
			view.setLayoutParams(new LinearLayout.LayoutParams(25, height)); 
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams(); 
			params.setMargins(3, 0, 0, 0);	// substitute parameters for left, 
					// top, right, bottom 
			view.setLayoutParams(params); 
			linearChart.addView(view); 
		}
	}*/

	
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

