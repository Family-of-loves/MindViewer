package com.example.mindviewer;

import java.util.ArrayList;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.util.Log;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.example.mindviewer.BlueSmirf.BlueSmirfSPP;
import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;


@SuppressLint("ValidFragment")
public class ConnActivity extends Fragment implements OnClickListener, Runnable, Handler.Callback, OnItemSelectedListener {
	
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
	
	/* BlueSmirf */
	static final String TAG = "BlueSmirfDemo";

	// app state
	static BlueSmirfSPP      mSPP;
	boolean           mIsThreadRunning;
	String            mSmirfBluetoothAddress;
	ArrayList<String> mArrayListBluetoothAddress;

	// UI
	TextView     mTextViewStatus;
	Spinner      mSpinnerArduinoDevices;
	@SuppressWarnings("rawtypes")
	ArrayAdapter mArrayAdapterDevices;
	Handler      mHandler;
	
	public ConnActivity(Context context, BlueSmirfSPP mSPP) {
		mContext = context;
		mIsThreadRunning           = false;
		mSmirfBluetoothAddress     = null;
		mArrayListBluetoothAddress = new ArrayList<String>();
		
		this.mSPP                  = mSPP;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_conn, null);
		
		mTextViewStatus         = (TextView) view.findViewById(R.id.section2_status);
		ArrayList<String> items = new ArrayList<String>();
		mSpinnerArduinoDevices  = (Spinner) view.findViewById(R.id.spinner_arduino);
		mArrayAdapterDevices    = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
		mHandler                = new Handler(this);
		mSpinnerArduinoDevices.setOnItemSelectedListener(this);
		mArrayAdapterDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		/* Bluetooth 목록을 스피로 저장한다. */
		mSpinnerArduinoDevices.setAdapter(mArrayAdapterDevices);
		
		
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
					onConnMindset(v);
					toggleConn(true);
				}
				else {
					tgDevice.close();
					toggleConn(false);
				}
			break;
			case R.id.btn_arduino_conn :
				onConnectLink(v);
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
							toggleConn(true);
							tgDevice.start();
						break;
						case TGDevice.STATE_DISCONNECTED:
							Toast.makeText(getActivity(), "Mindset과 연결이 끊겼습니다.", Toast.LENGTH_SHORT).show();
							toggleConn(false);
						break;
						case TGDevice.STATE_NOT_FOUND:
							Toast.makeText(getActivity(), "Mindset을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
							toggleConn(false);
						break;
						case TGDevice.STATE_NOT_PAIRED:
							Toast.makeText(getActivity(), "Mindset이 페어링 되지 않았습니다.", Toast.LENGTH_SHORT).show();
							toggleConn(false);
						break;
						default:
						break;
					}
				break;
				case TGDevice.MSG_POOR_SIGNAL:
					Log.v("HelloEEG", "PoorSignal: " + msg.arg1);
				break;
				
				case TGDevice.MSG_ATTENTION:
					//bWave.setAtt(msg.arg1);
					bWave.setAtt((int) (Math.random() * 100));
					//Log.v("HelloEEG", "Attention: " + msg.arg1);
				break;
				
				case TGDevice.MSG_MEDITATION:
					//bWave.setMed(msg.arg1);
					bWave.setMed((int) (Math.random() * 100));
					//Log.v("HelloEEG", "Meditation: " + bWave.getMed());
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
	public void onConnMindset(View v){
		btAdapter = BluetoothAdapter.getDefaultAdapter();
    	if(btAdapter != null) {
    		bWave = Brainwaves.getInstance();
    		tgDevice = new TGDevice(btAdapter, handler);
    		tgDevice.connect(true);
    		tgDevice.start();
    	}
	}
	
	public void toggleConn(boolean isConnected){
		if(isConnected){
			btn_mindset_conn_state = 1;
			btn_mindset_conn.setText("Disconnect to Mindset");
			section1_status.setText("Connected");
		}else{
			btn_mindset_conn_state = 0;
			btn_mindset_conn.setText("Connect to Mindset");
			section1_status.setText("Disconnected");
		}	
	}
	
	/*
	 * Connection Arduino
	*/
	
	@Override
	public void onResume()
	{
		super.onResume();

		// update the paired device(s)
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		mArrayAdapterDevices.clear();
		mArrayListBluetoothAddress.clear();
		if(devices.size() > 0)
		{
			for(BluetoothDevice device : devices){
				mArrayAdapterDevices.add(device.getName());
				mArrayListBluetoothAddress.add(device.getAddress());
			}

			// request that the user selects a device
			if(mSmirfBluetoothAddress == null){
				mSpinnerArduinoDevices.performClick();
			}
		}
		else{
			mSmirfBluetoothAddress = null;
		}

		UpdateUI();
	}

	@Override
	public void onPause(){
		mSPP.disconnect();
		super.onPause();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}
		
	public void onConnectLink(View view){
		if(mIsThreadRunning == false){
			mIsThreadRunning = true;
			UpdateUI();
			Thread t = new Thread(this);
			t.start();
		}
	}

	public void onDisconnectLink(View view){
		mSPP.disconnect();
	}

	/*
	 * main loop
	 */
	public void run(){
		Looper.prepare();
		mSPP.connect(mSmirfBluetoothAddress);
		while(mSPP.isConnected()){
			mSPP.flush();
			
			if(mSPP.isError()){
				mSPP.disconnect();
			}

			mHandler.sendEmptyMessage(0);

			// wait briefly before sending the next packet
			try { Thread.sleep((long) (1000.0F/30.0F)); }
			catch(InterruptedException e) { Log.e(TAG, e.getMessage());}
		}

		mIsThreadRunning = false;
		mHandler.sendEmptyMessage(0);
	}

	/*
	 * update UI
	 */

	public boolean handleMessage (Message msg){
		// received update request from Bluetooth IO thread
		UpdateUI();
		return true;
	}

	private void UpdateUI(){
		if(mSPP.isConnected()){
			mTextViewStatus.setText("Connected");
		}
		else if(mIsThreadRunning){
			mTextViewStatus.setText("Connecting..");
		}
		else{
			mTextViewStatus.setText("Disconnected");
		}
	}

	/*
	 * Spinner callback
	 */

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
		mSmirfBluetoothAddress = mArrayListBluetoothAddress.get(pos);
		System.out.println(mSmirfBluetoothAddress);
	}

	public void onNothingSelected(AdapterView<?> parent){
		mSmirfBluetoothAddress = null;
	}	
	//Fragment Settings
	@SuppressLint("NewApi")
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    setUserVisibleHint(true);
	}
}