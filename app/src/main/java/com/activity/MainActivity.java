package com.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;
import com.qrcodescan.R;
import com.utils.CodeUtils;
import com.utils.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
	@BindView(R.id.openQrCodeScan)
	Button openQrCodeScan;
	@BindView(R.id.text)
	EditText text;
	@BindView(R.id.CreateQrCode)
	Button CreateQrCode;
	@BindView(R.id.QrCode)
	ImageView QrCode;
	@BindView(R.id.qrCodeText)
	TextView qrCodeText;
	//打开扫描界面请求码
	private int REQUEST_CODE = 0x01;
	//扫描成功返回码
	private int RESULT_OK = 0xA1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
	}

	@OnClick({R.id.openQrCodeScan, R.id.CreateQrCode})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.openQrCodeScan:
				//打开二维码扫描界面
				cameraTask(R.id.openQrCodeScan);
				break;
			case R.id.CreateQrCode:
				//获取输入的文本信息
				String str = text.getText().toString().trim();
				if (!TextUtils.isEmpty(str)) {
					//根据输入的文本生成对应的二维码并且显示出来
//                        Bitmap mBitmap = EncodingHandler.createQRCode(text.getText().toString(), 500);
					//生成二维码
					Bitmap mBitmap = CodeUtils.createImage(str, 400, 400, null);
					if (mBitmap != null) {
						Toast.makeText(this, "二维码生成成功！", Toast.LENGTH_SHORT).show();
						QrCode.setImageBitmap(mBitmap);
					}
				} else {
					Toast.makeText(this, "文本信息不能为空！", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//扫描结果回调
		if (resultCode == RESULT_OK) { //RESULT_OK = -1
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("qr_scan_result");
			//将扫描出的信息显示出来
			qrCodeText.setText(scanResult);
		}
	}

	/**
	 * 请求CAMERA权限码
	 */
	public static final int REQUEST_CAMERA_PERM = 101;


	/**
	 * EsayPermissions接管权限处理逻辑
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		// Forward results to EasyPermissions
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}


	@AfterPermissionGranted(REQUEST_CAMERA_PERM)
	public void cameraTask(int viewId) {
		if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
			// Have permission, do the thing!
			onViewClick(viewId);
		} else {
			// Ask for one permission
			EasyPermissions.requestPermissions(this, "需要请求camera权限", REQUEST_CAMERA_PERM, Manifest.permission.CAMERA);
		}
	}

	/**
	 * 打开摄像头
	 * @param viewId
	 */
	private void onViewClick(int viewId) {
		if (CommonUtil.isCameraCanUse()) {
			Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
			startActivityForResult(intent, REQUEST_CODE);
		} else {
			Toast.makeText(this, "请打开此应用的摄像头权限！", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {
		Toast.makeText(this, "执行onPermissionsGranted()...", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> perms) {
		Toast.makeText(this, "执行onPermissionsDenied()...", Toast.LENGTH_SHORT).show();
		if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
			new AppSettingsDialog.Builder(this, "当前App需要申请camera权限,需要打开设置页面么?")
					.setTitle("权限申请")
					.setPositiveButton("确认")
					.setNegativeButton("取消", null /* click listener */)
					.setRequestCode(REQUEST_CAMERA_PERM)
					.build()
					.show();
		}
	}
}
