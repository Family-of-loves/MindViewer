package com.example.mindviewer;

import com.example.mindviewer.BlueSmirf.BlueSmirfSPP;
import com.example.mindviewer.Widget.CircularProgressBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

@SuppressLint("ValidFragment")
public class ScanActivity extends Fragment  implements OnClickListener{
	Context mContext;

	Button btn_startBrainScan;
	BlueSmirfSPP mSPP;
	Brainwaves bWave;
	CircularProgressBar cAtt;
	CircularProgressBar cMed;
	ProgressBar bar;
	ToggleButton start;
	LinearLayout resultDisp;
	TextView feelResult;
	Button musicStop;
	Button musicRestart;
	private MediaPlayer background;
	
	private volatile Thread theProgressBarThread1;
	public int CurrentPosition;

	int appMode = 0;
	int timeCnt = 0;
	int sensingTime = 30;
	int totalAtt;
	int totalMed;

	//PrintThread thread;
	
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

		feelResult = (TextView)view.findViewById(R.id.feelResult);
		
		bar = (ProgressBar) view.findViewById(R.id.progressBar1);
		bar.setVisibility(ProgressBar.GONE);
		resultDisp = (LinearLayout) view.findViewById(R.id.resultDisp);
		resultDisp.setVisibility(LinearLayout.GONE);
		
		start = (ToggleButton) view.findViewById(R.id.brainstart);
		start.setOnClickListener(this);

		musicStop = (Button) view.findViewById(R.id.musicStop);
		musicStop.setOnClickListener(this);
		musicStop.setVisibility(Button.GONE);
		
		musicRestart = (Button) view.findViewById(R.id.musicRestart);
		musicRestart.setOnClickListener(this);
		musicRestart.setVisibility(Button.GONE);
			

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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
			case R.id.brainstart :
				if (((ToggleButton) v).isChecked()) {
					bar.setVisibility(ProgressBar.VISIBLE);
					resultDisp.setVisibility(LinearLayout.INVISIBLE);
					musicRestart.setVisibility(Button.GONE);
					feelResult.setText("");
					timeCnt = 0;
					totalAtt = 0;
					totalMed = 0;
					bWave.setScanState(true);
					startProgressBarThread();
					
					//thread = new PrintThread();
					//thread.setDaemon(true);
					//thread.start();		
				} else {
					bar.setVisibility(ProgressBar.GONE);
					bWave.setScanState(false);
					stopProgressBarThread();
				}
			break;
			
			case R.id.musicStop:
				background.pause();
				musicStop.setVisibility(Button.GONE);
				musicRestart.setVisibility(Button.VISIBLE);
				break;
				
			case R.id.musicRestart:
				
				background.start();
				background.setLooping(true);
				musicStop.setVisibility(Button.VISIBLE);
				musicRestart.setVisibility(Button.GONE);
				break;	
		}	
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

	Handler mHandler = new Handler();
	private Runnable backgroundThread1 = new Runnable() {
		@Override
		public void run() {

			if (Thread.currentThread() == theProgressBarThread1) {
				CurrentPosition = 0;
				final int total = 100;
				while (CurrentPosition < total) {
					try {
						
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
										 * PlayList �� �����ϰ� �뷡�� ���ÿ� Ʋ��������.
										 */
										System.out.println("mDebug  : " + timeCnt);
										if(timeCnt == sensingTime){

											if(background!=null)
												background.stop();	// �ߺ���� ���� //
											
											System.out.println(totalAtt + " / " + totalMed);
											System.out.println(totalAtt/sensingTime + " / " + totalMed/sensingTime);
											if( ((totalAtt/sensingTime) >= 60) && ((totalAtt/sensingTime) < 90)&&
													((totalMed/sensingTime) >= 40) && ((totalMed/sensingTime) < 90) ){
												onSendCmdArduino("s");
												feelResult.setText("��Ʈ������ ���� �׿����");
												background = MediaPlayer.create(getActivity(),R.raw.stress);
												
											} else if ( ((totalAtt/sensingTime) >= 40) && ((totalAtt/sensingTime) < 90)&&
													((totalMed/sensingTime) >= 1) && ((totalMed/sensingTime) < 40) ){
												onSendCmdArduino("t");
												feelResult.setText("�ʹ� ����ǿ�");
												background = MediaPlayer.create(getActivity(),R.raw.hightention);
												
											} else if ( ((totalAtt/sensingTime) >= 1) && ((totalAtt/sensingTime) < 40)&&
													((totalMed/sensingTime) >= 40) && ((totalMed/sensingTime) < 90) ){
												onSendCmdArduino("a");
												feelResult.setText("������ ���� �ʾƿ�");
												background = MediaPlayer.create(getActivity(),R.raw.needattention);
												
											} else if ( ((totalAtt/sensingTime) >= 40) && ((totalAtt/sensingTime) < 60)&&
													((totalMed/sensingTime) >= 40) && ((totalMed/sensingTime) < 90) ){
												onSendCmdArduino("g");
												feelResult.setText("����ϰ� �ƹ��͵� �ϰ���� �ʾƿ�");
												background = MediaPlayer.create(getActivity(),R.raw.gloomy);
																							
											} else if ( ((totalAtt/sensingTime) >= 1) && ((totalAtt/sensingTime) < 40)&&
													((totalMed/sensingTime) >= 1) && ((totalMed/sensingTime) < 40) ){
												onSendCmdArduino("w");
												feelResult.setText("�ɽ��� ���� ����������");
												background = MediaPlayer.create(getActivity(),R.raw.weaklyphysics);
												
											} else {
												Toast.makeText(getActivity(), "����� �� �� �����. �ٽ� �����ҰԿ�!", Toast.LENGTH_SHORT).show();
												
											}
											
											background.start();
											background.setLooping(true);
											musicStop.setVisibility(Button.VISIBLE);
											musicRestart.setVisibility(Button.GONE);
											
											timeCnt = 0;
											totalAtt = 0;
											totalMed = 0;
											bWave.setScanState(false);
											
											
											//thread.interrupt();
											
											
										}
									} 
								}
							});
							
						
						
						progressBarHandle.sendMessage(progressBarHandle.obtainMessage());
						//long time= (long)(total / sensingTime)*100;
						Thread.sleep(1000);
						}
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
				CurrentPosition=CurrentPosition+3;
				bar.setProgress(CurrentPosition);
				start.setText("���� �� �Դϴ�. (" + CurrentPosition + "%)");
				
				
				
				if(CurrentPosition > 20){
					start.setText("���ݸ� �� ���� �� ���Կ�. (" + CurrentPosition + "%)");
				}
				if (CurrentPosition > 50){
					start.setText("������ ���ƿ�. (" + CurrentPosition + "%)");
				}
				if (CurrentPosition > 80){
					start.setText("�� ��������. ���ݸ� �����ּ���! (" + CurrentPosition + "%)");
				}
				if (CurrentPosition > 100) {
					stopProgressBarThread();
					start.setTextOn("�ٽ� �����ϱ�");
					resultDisp.setVisibility(LinearLayout.VISIBLE);
				}
				

			}
		};

	};


}