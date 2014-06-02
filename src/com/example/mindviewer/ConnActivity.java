package com.example.mindviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.bluetooth.BluetoothAdapter;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;


@SuppressLint("ValidFragment")
public class ConnActivity extends Fragment implements OnClickListener {
	
	Context mContext;
	
	TGDevice tgDevice;
	TGEegPower tgEeg;
	BluetoothAdapter btAdapter;
	Brainwaves bWave;
	
	Button btn_mindset_conn;
	Button btn_arduino_conn;
	TextView section1_status;
	TextView section2_status;
	
	int btn_mindset_conn_state = 0;
	public ConnActivity(Context context) {
		mContext = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_conn, null);
		/* TextView Def */
		section1_status = (TextView) view.findViewById(R.id.section1_status);
		
		/* Button Def */
		btn_mindset_conn = (Button) view.findViewById(R.id.btn_mindset_conn);
		btn_arduino_conn = (Button) view.findViewById(R.id.btn_arduino_conn);
		
		/* Button reg listener */
		btn_mindset_conn.setOnClickListener(this);
		btn_arduino_conn.setOnClickListener(this);
		
    	return view;
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.btn_mindset_conn :
			if(btn_mindset_conn_state == 0){
				onConnMindset();
				btn_mindset_conn_state = 1;
				btn_mindset_conn.setText("Disconnect to Mindset");
			}
			else {
				tgDevice.close();
				btn_mindset_conn_state = 0;
				btn_mindset_conn.setText("Connect to Mindset");
			}
		break;
		case R.id.btn_arduino_conn :
			Toast.makeText(getActivity(), "준비중입니다.", Toast.LENGTH_SHORT).show();
		break;
			
		}		
	}
	
	
	@SuppressLint("HandlerLeak")
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case TGDevice.MSG_STATE_CHANGE:
					switch (msg.arg1) {
						case TGDevice.STATE_IDLE:
						break;
						case TGDevice.STATE_CONNECTING:
							Toast.makeText(getActivity(), "Mindset과 연결중입니다.", Toast.LENGTH_SHORT).show();
						break;
						case TGDevice.STATE_CONNECTED:
							Toast.makeText(getActivity(), "Mindset과 연결되었습니다.", Toast.LENGTH_SHORT).show();
							section1_status.setText("Connected");
							tgDevice.start();
						break;
						case TGDevice.STATE_DISCONNECTED:
							Toast.makeText(getActivity(), "Mindset과 연결이 끊겼습니다.", Toast.LENGTH_SHORT).show();
							section1_status.setText("Disconnected");
						break;
						case TGDevice.STATE_NOT_FOUND:
							Toast.makeText(getActivity(), "Mindset을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
						case TGDevice.STATE_NOT_PAIRED:
							Toast.makeText(getActivity(), "Mindset이 페어링 되지 않았습니다.", Toast.LENGTH_SHORT).show();
						default:
						break;
					}
				break;
				case TGDevice.MSG_POOR_SIGNAL:
					Log.v("HelloEEG", "PoorSignal: " + msg.arg1);
				break;
				
				case TGDevice.MSG_ATTENTION:
					bWave.setAtt(msg.arg1);
					Log.v("HelloEEG", "Attention: " + bWave.getAtt());
				break;
				
				case TGDevice.MSG_MEDITATION:
					bWave.setMed(msg.arg1);
					Log.v("HelloEEG", "Meditation: " + bWave.getMed());
				break;
				
				case TGDevice.MSG_EEG_POWER:
					//TGEegPower ep = (TGEegPower)msg.obj            ;
					//Log.v("HelloEEG", "Delta: " + ep.delta);
				
				default:
				break;
			}
		}
	};
	
	/*
	 * Connection Mindset 
	 */
	public void onConnMindset(){
		btAdapter = BluetoothAdapter.getDefaultAdapter();
    	if(btAdapter != null) {
    		bWave = new Brainwaves();
    		tgDevice = new TGDevice(btAdapter, handler);
    		tgDevice.connect(true);
    		tgDevice.start();
    	}    	
	}
		
	//Fragment Settings
	@SuppressLint("NewApi")
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    setUserVisibleHint(true);
	}
}