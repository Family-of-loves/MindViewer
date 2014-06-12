package com.example.mindviewer;

import com.example.mindviewer.BlueSmirf.BlueSmirfSPP;
import com.example.mindviewer.Widget.CircularProgressBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

@SuppressLint("ValidFragment")
public class ScanActivity extends Fragment  {
	Context mContext;

	Button btn_startBrainScan;
	BlueSmirfSPP mSPP;
	Brainwaves bWave;
	CircularProgressBar cAtt;
	CircularProgressBar cMed;
	ProgressBar bar;
	ToggleButton start;
	
	private volatile Thread theProgressBarThread1;
	public int CurrentPosition;
	
	int appMode = 0;
	int timeCnt = 0;
	int sensingTime = 30;
	int totalAtt;
	int totalMed;
	
	//LinearLayout linearChart;
	
	public ScanActivity(Context context, BlueSmirfSPP mSPP) {
		this.mContext 				= context;
		bWave 						= Brainwaves.getInstance();
		this.mSPP 				   	= mSPP;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_scan, null);
		
		bar = (ProgressBar) view.findViewById(R.id.progressBar1);
		bar.setVisibility(ProgressBar.GONE);

		start = (ToggleButton) view.findViewById(R.id.brainstart);
		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((ToggleButton) v).isChecked()) {
					bar.setVisibility(ProgressBar.VISIBLE);
					bWave.setScanState(true);
					startProgressBarThread();
					PrintThread thread = new PrintThread();
					thread.setDaemon(true);
					thread.start();		
				} else {
					bar.setVisibility(ProgressBar.GONE);
					bWave.setScanState(false);
					stopProgressBarThread();
				}

			}
		});
		
		//
		
		cAtt = (CircularProgressBar) view.findViewById(R.id.circle_attention);
		cAtt.setSubTitle("Attention");
		cMed = (CircularProgressBar) view.findViewById(R.id.circle_meditation);
		cMed.setSubTitle("Meditation");
		
		return view;
	}
	
	public synchronized void startProgressBarThread() {
		if (theProgressBarThread1 == null) {
			theProgressBarThread1 = new Thread(null, backgroundThread1,
					"startProgressBarThread");
			CurrentPosition = 0;
			theProgressBarThread1.start();
		}
	}

	public synchronized void stopProgressBarThread() {
		if (theProgressBarThread1 != null) {
			Thread tmpThread = theProgressBarThread1;
			theProgressBarThread1 = null;
			tmpThread.interrupt();
		}
		bar.setVisibility(ProgressBar.GONE);
		start.setChecked(false);
		start.invalidate();
	}

	
	@SuppressLint("NewApi")
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    setUserVisibleHint(true);
	}
		
	
	
	public void onSendCmdArduino(String cmd){
		String message = cmd;
		
		if(mSPP.isConnected()){
			byte[] send = message.getBytes();
			mSPP.write(send, 0, send.length);
		} else {
			Toast.makeText(getActivity(), "�Ƶ��̳밡 ������ �Ǿ����� �ʽ��ϴ�.", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private Runnable backgroundThread1 = new Runnable() {
		@Override
		public void run() {

			if (Thread.currentThread() == theProgressBarThread1) {
				CurrentPosition = 0;
				final int total = 100;
				while (CurrentPosition < total) {
					try {
						progressBarHandle.sendMessage(progressBarHandle.obtainMessage());
						long time= (long)(total / sensingTime)*100;
						Log.e("err", time+"");
						Thread.sleep(time);
					} catch (final InterruptedException e) {
						return;
					} catch (final Exception e) {
						return;
					}
				}
			}
		}

			
		Handler progressBarHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				CurrentPosition++;
				bar.setProgress(CurrentPosition);
				start.setText("" + CurrentPosition + "%");
				if (CurrentPosition == 100) {
					stopProgressBarThread();
				}

			}
		};

	};
	
	
	Handler mHandler = new Handler();
	
	class PrintThread extends Thread{
		public void run(){
			while(true){
				mHandler.post(new Runnable(){
					public void run() {
						// TODO Auto-generated method stub
						if(bWave.getScanState()){
							cAtt.setTitle(Integer.toString(bWave.getAtt()) + "%");
							cAtt.setProgress(bWave.getAtt());
							cMed.setTitle(Integer.toString(bWave.getMed()) + "%");
							cMed.setProgress(bWave.getMed());
							
							System.out.println("Debug : " + bWave.getSig() + " / " + timeCnt + " / " + sensingTime);
							
							if(bWave.getSig() == 0){
								totalAtt += bWave.getAtt();
								totalMed += bWave.getMed();
								timeCnt ++;
								
							}
							/*
							 * PlayList �� �����ϰ� �뷡�� ���ÿ� Ʋ��������.
							 */
							if(timeCnt == sensingTime){
								if( ((totalAtt/sensingTime) > 80) && ((totalMed/sensingTime) > 80) ){
									onSendCmdArduino("s");
									
								} else if ( ((totalAtt/sensingTime) > 80) && ((totalMed/sensingTime) < 50) ){
									onSendCmdArduino("t");
									
								} else if ( ((totalAtt/sensingTime) < 30) && ((totalMed/sensingTime) > 80) ){
									onSendCmdArduino("a");
									
								} else if ( ((totalAtt/sensingTime) < 50) && ((totalMed/sensingTime) < 50) ){
									onSendCmdArduino("g");
									
								} else if ( ((totalAtt/sensingTime) < 30) && ((totalMed/sensingTime) < 50) ){
									onSendCmdArduino("w");
									
								} else {
									Toast.makeText(getActivity(), "����� �� �� �����. �ٽ� �����ҰԿ�!", Toast.LENGTH_SHORT).show();
									timeCnt = 0;
									
								}
							}
						} 
					}
				});
				try{
					Thread.sleep(1000);
				} catch(InterruptedException e){;}
			}
		}
	}
}

