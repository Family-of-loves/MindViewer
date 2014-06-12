package com.example.mindviewer;

import com.example.mindviewer.BlueSmirf.BlueSmirfSPP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class ManualActivity extends Fragment implements OnClickListener, RadioGroup.OnCheckedChangeListener {
	Context mContext;
	BlueSmirfSPP mSPP;
	Brainwaves bWave;
	
	String cmd;
	Button startCare;
	Button musicStop;
	private MediaPlayer background;
	
	public ManualActivity(Context context, BlueSmirfSPP mSPP) {
		mContext 	= context;
		bWave 		= Brainwaves.getInstance();
		this.mSPP 	= mSPP;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_manual, null);
		startCare = (Button) view.findViewById(R.id.startCare);
		startCare.setOnClickListener(this);
		
		musicStop = (Button) view.findViewById(R.id.musicStop);
		musicStop.setOnClickListener(this);
		
		RadioGroup feelingLists = (RadioGroup) view.findViewById(R.id.feelingList);
		feelingLists.setOnCheckedChangeListener(this);
				
    	return view;
	}


	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		switch(arg1){
		case R.id.type1:
			cmd = "s";
		break;
		case R.id.type2:
			cmd = "g";
		break;
		case R.id.type3:
			cmd = "t";
		break;
		case R.id.type4:
			cmd = "a";
		break;
		case R.id.type5:
			cmd = "w";
		break;
		}
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.startCare:
			onSendCmdArduino(v, cmd);
			Toast.makeText(getActivity(), "시작합니다. 이제 안정을 취하세요.",
					Toast.LENGTH_SHORT).show();
			
				background = MediaPlayer.create(super.getActivity(),
						R.raw.background);

				background.start();
			
			break;

		case R.id.musicStop:
			background.stop();
			break;
		}
	}
	
	public void onSendCmdArduino(View view, String cmd){
		String message = cmd;
		
		if(mSPP.isConnected()){
			byte[] send = message.getBytes();
			mSPP.write(send, 0, send.length);
		} else {
			Toast.makeText(getActivity(), "아두이노가 연결이 되어있지 않습니다.", Toast.LENGTH_SHORT).show();
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    setUserVisibleHint(true);
	}

}
