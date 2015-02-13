package com.archermind.stresstest;

import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WIFIActivity extends Activity implements OnClickListener {

	private String mIpAddress = null;
	private TextView mMaxView;
	private String mPingIpAddrResult = null;
	private TextView mTestTimeTv;
	private Timer mTimer;
	private PowerManager.WakeLock mWakeLock;
	private WifiManager mWifiManager;
	private Button btn_start_test;
	private Button btnMax;
	private Button btnCancle;
	private TextView viewMaxTime;
	private EditText etTime;
	private String TAG = "WIFIActivity";

	private int mAutoTestFlag = 0;
	private boolean isRunning = false;
	protected int mMaxTestCount;
	protected int mCurrentCount;
	private long exitTime = 0;
	private int a;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			switch (paramAnonymousMessage.what) {
			default:
				return;
			case 1:
			}
			WIFIActivity.this.updateTestTimeTV();
			WIFIActivity.this.mTestTimeTv.setVisibility(0);
		}
	};

	private void initData() {
		this.mAutoTestFlag = getIntent().getIntExtra("auto", 0);
		this.mMaxTestCount = getIntent().getIntExtra("max", 0);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_setting);
		initView();
		initData();

		this.mWifiManager = ((WifiManager) getSystemService("wifi"));
		this.mMaxView = ((TextView) findViewById(R.id.maxtime_btn));
		this.mTestTimeTv = ((TextView) findViewById(R.id.maxtime_tv));
		this.mWakeLock = ((PowerManager) getSystemService("power"))
				.newWakeLock(26, "wifitest");
		this.mWakeLock.acquire();
		updateMaxTV();
		if (this.mAutoTestFlag != 0)
			startTest();
	}

	private void initView() {
		btnCancle = (Button) findViewById(R.id.btnCancle);
		btnCancle.setOnClickListener(this);

		btn_start_test = (Button) findViewById(R.id.start_test);
		btn_start_test.setOnClickListener(this);

		btnMax = (Button) findViewById(R.id.maxtime_btn);
		btnMax.setOnClickListener(this);

		viewMaxTime = (TextView) findViewById(R.id.maxtime_tv);
		viewMaxTime.setOnClickListener(this);

		etTime = (EditText) findViewById(R.id.etTime);
		etTime.setOnClickListener(this);
		etTime.setHint("请输入次数");
	}

	@Override
	public void onClick(View v) {
		if (v == btnCancle) {
			if (ifTestCaseRunning()) {
				finish();
			} else {
				ExitApp();
			}
		}

		if (v == btnMax) {
			if (btnMax.getText().toString().trim().equals("SET")) {

			}
		}

		// click the button and start the process
		if (v == btn_start_test) {
			if (btn_start_test.getText().toString().trim().equals("START")) {

				if (!etTime.getText().toString().trim().equals("")) {
					btn_start_test.setText("STOP");
					startTest();
				} else {
					Toast.makeText(WIFIActivity.this, "输入次数为空，请重新输入",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				btn_start_test.setText("START");
				stopTest();
			}
		}
	}

	public void stopTest() {
		etTime.setEnabled(true);
		this.isRunning = false;
		if (this.mTimer != null)
			this.mTimer.cancel();
	}

	public void startTest() {
		unclickTextEdit();
		this.mCurrentCount = 0;
		this.isRunning = true;
		Log.d(TAG, "设定次数为：" + etTime.getText().toString().trim() + "次,测试开始！！！");
		this.mMaxTestCount = Integer.parseInt(etTime.getText().toString()
				.trim());
		this.mTimer = new Timer();
		this.mTimer.schedule(new TimerTask() {
			
			public void run() {
				if ((!WIFIActivity.this.isRunning)
						|| ((WIFIActivity.this.mMaxTestCount != 0) && (WIFIActivity.this.mCurrentCount >= WIFIActivity.this.mMaxTestCount))) {
					WIFIActivity.this.mTimer.cancel();
					Log.d(TAG, "Test Done!!!!!");
					return;
				}
				int i = WIFIActivity.this.mWifiManager.getWifiState();
				if (i == 1) {
					try {
						// thread sleep 3s ,but it's could be changed
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					WIFIActivity.this.mWifiManager.setWifiEnabled(true);
					Log.d(TAG,
							"*********wifistate is closed, try open wifi now!*****");
				}
				while (true) {
					WIFIActivity.this.mHandler.sendEmptyMessage(1);
					int j = WIFIActivity.this.mWifiManager.getWifiState();
					if (j == 3) {
						try {
							// thread sleep 3s ,but it's could be changed
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						WIFIActivity.this.mWifiManager.setWifiEnabled(false);
						Log.d(TAG,
								"*********wifistate is opened, try close wifi now!******");
						WIFIActivity.this.incCurCount();
						return;
					}
				}
			}
		}, 2000L, 8000L);

	
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");
	}

	// 判断当前case是否正在执行
	public boolean ifTestCaseRunning() {
		if (btn_start_test.getText().toString().trim().equals("START")) {
			return true;
		} else {
			return false;
		}
	}
	
	public void unclickTextEdit(){
		if(!ifTestCaseRunning()){
			etTime.setEnabled(false);
		}
	}

	// 监听硬件 BACK键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (ifTestCaseRunning()) {
			finish();
		} else {
			ExitApp();
		}
		return false;
	}

	// 连续两次点击退出Activity并结束case
	public void ExitApp() {
		if ((System.currentTimeMillis() - exitTime) > 1000) {
			Toast.makeText(WIFIActivity.this, "再按一次退出程序，退出后Case将自动停止",
					Toast.LENGTH_LONG).show();
			exitTime = System.currentTimeMillis();
		} else {
			stopTest();
			finish();
		}
	}

	protected void incCurCount() {
		WIFIActivity.this.mCurrentCount++;
	}

	public void updateMaxTV() {
		// super.updateMaxTV();
		// this.mMaxView.setText(getString(R.id.etTime) + this.mMaxTestCount);
	}

	public void updateTestTimeTV() {
		// this.mTestTimeTv.setText(getString(R.id.maxtime_tv)
		// + this.mCurrentCount);
	}

}
