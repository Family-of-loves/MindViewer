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
import android.widget.LinearLayout;
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
	LinearLayout resultDisp;

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
		resultDisp = (LinearLayout) view.findViewById(R.id.resultDisp);
		resultDisp.setVisibility(LinearLayout.GONE);
		
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

	public synchronized  void startProgressBarThread() {
		if (theProgressBarThread1 == null) {
			theProgressBarThread1 = new Thread(null, backgroundThread1,
					"startProgressBarThread");
			CurrentPosition = 0;
			theProgressBarThread1.start();
		}
	}

	public synchronized  void stopProgressBarThread() {
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

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
			case R.id.brainstart :
				bWave.setScanState(true);
				timeCnt = 0;
				totalAtt = 0;
				totalMed = 0;
			break;
		}	
	}

	public void onSendCmdArduino(String cmd){
		String message = cmd;

		if(mSPP.isConnected()){
			byte[] send = message.getBytes();
			mSPP.write(send, 0, send.length);
		} else {
			Toast.makeText(getActivity(), "아두이노가 연결이 되어있지 않습니다.", Toast.LENGTH_SHORT).show();
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
				start.setText("측정 중 입니다. (" + CurrentPosition + "%)");
				if(CurrentPosition > 20){
					start.setText("조금만 더 측정 해 볼게요. (" + CurrentPosition + "%)");
				}
				if (CurrentPosition > 50){
					start.setText("느낌이 좋아요. (" + CurrentPosition + "%)");
				}
				if (CurrentPosition > 80){
					start.setText("다 끝나가요. 조금만 참아주세요! (" + CurrentPosition + "%)");
				}
				if (CurrentPosition == 100) {
					stopProgressBarThread();
					start.setTextOn("다시 측정하기");
					resultDisp.setVisibility(LinearLayout.VISIBLE);
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

							if(bWave.getSig() == 0){
								totalAtt += bWave.getAtt();
								totalMed += bWave.getMed();
								timeCnt ++;

							}
							/*
							 * PlayList 를 생성하고 노래가 동시에 틀어져야함.
							 */
							System.out.println("mDebug  : " + timeCnt);
							if(timeCnt == sensingTime){

								System.out.println(totalAtt + " / " + totalMed);
								
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
									Toast.makeText(getActivity(), "기분을 알 수 없어요. 다시 측정할게요!", Toast.LENGTH_SHORT).show();
									timeCnt = 0;
									totalAtt = 0;
									totalMed = 0;
									bWave.setScanState(false);

								}
							}
						} 
					}
				});
				try{
					Thread.sleep(900);
				} catch(InterruptedException e){;}
			}
		}
	}
}