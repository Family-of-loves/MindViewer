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
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class ScanActivity extends Fragment implements OnClickListener {
	Context mContext;

	Button btn_startBrainScan;
	BlueSmirfSPP mSPP;
	Brainwaves bWave;
	CircularProgressBar cAtt;
	CircularProgressBar cMed;
	
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
		
		btn_startBrainScan = (Button) view.findViewById(R.id.startBrainScan);
		btn_startBrainScan.setOnClickListener(this);
		
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
			case R.id.startBrainScan :
				Toast.makeText(getActivity(), sensingTime + "�ʰ� ������ �����մϴ�.", Toast.LENGTH_SHORT).show();
				bWave.setScanState(true);
				btn_startBrainScan.setEnabled(false);
				btn_startBrainScan.setText("��ĵ �� �Դϴ�...");
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
						} else {
							btn_startBrainScan.setEnabled(true);
							btn_startBrainScan.setText("���� ��ĵ ����!");
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

