package com.example.permissioncheckflow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * 这个类演示了Android中请求敏感权限（以相机权限和联系人权限为例）的完整流程。
 * 主要包含以下步骤：
 * 1. 检查权限是否已授予。
 * 2. 如果未授予，判断是否需要向用户解释为什么需要该权限。
 * 3. 申请权限。
 * 4. 处理权限申请的结果。
 */
public class MainActivity extends AppCompatActivity {

    // 定义权限请求码，用于在回调中区分不同的请求
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    // 新增：联系人权限请求码
    private static final int CONTACT_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // 设置系统状态栏边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnRequestPermission = findViewById(R.id.btn_request_permission);
        btnRequestPermission.setOnClickListener(v -> {
            // 点击按钮时，启动相机权限检查流程
            checkCameraPermission();
        });

        // 新增：点击按钮申请联系人权限
        Button btnRequestContactPermission = findViewById(R.id.btn_request_contact_permission);
        btnRequestContactPermission.setOnClickListener(v -> {
            checkContactPermission();
        });
    }

    /**
     * 检查并申请相机权限的核心流程
     */
    private void checkCameraPermission() {
        // 步骤 1: 检查权限是否已经授予
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // 权限已经被授予，可以直接执行相关操作
            openCamera();
        } else {
            // 权限未被授予，进入申请流程

            // 步骤 2: 判断是否需要向用户解释为什么需要该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "我们需要相机权限来拍照，请授权", Toast.LENGTH_LONG).show();
                requestCameraPermission();
            } else {
                // 不需要解释，直接申请
                requestCameraPermission();
            }
        }
    }

    /**
     * 新增：检查并申请联系人权限的核心流程
     * 逻辑与相机权限完全一致，只是权限名称和回调代码不一样
     */
    private void checkContactPermission() {
        // 1. 检查是否已有 READ_CONTACTS 权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            readContacts();
        } else {
            // 2. 检查是否需要解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "我们需要读取联系人以展示好友列表", Toast.LENGTH_LONG).show();
                requestContactPermission();
            } else {
                // 3. 申请权限
                requestContactPermission();
            }
        }
    }

    /**
     * 发起相机权限申请
     */
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    /**
     * 新增：发起联系人权限申请
     */
    private void requestContactPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                CONTACT_PERMISSION_REQUEST_CODE);
    }

    /**
     * 模拟打开相机的操作
     */
    private void openCamera() {
        Toast.makeText(this, "相机权限已获取，正在打开相机...", Toast.LENGTH_SHORT).show();
    }

    /**
     * 新增：模拟读取联系人的操作
     */
    private void readContacts() {
        Toast.makeText(this, "联系人权限已获取，正在读取联系人...", Toast.LENGTH_SHORT).show();
    }

    /**
     * 步骤 4: 处理权限申请的结果
     * 当用户在系统权限对话框中做出选择（允许或拒绝）后，系统会回调这个方法。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 处理相机权限回调
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "您拒绝了相机权限，无法使用此功能", Toast.LENGTH_SHORT).show();
            }
        }
        // 新增：处理联系人权限回调
        else if (requestCode == CONTACT_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户允许读取联系人
                readContacts();
            } else {
                // 用户拒绝读取联系人
                Toast.makeText(this, "您拒绝了联系人权限，无法获取好友列表", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
