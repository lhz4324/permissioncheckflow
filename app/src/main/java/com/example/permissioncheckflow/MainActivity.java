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
 * 这个类演示了Android中请求敏感权限（以相机权限为例）的完整流程。
 * 主要包含以下步骤：
 * 1. 检查权限是否已授予。
 * 2. 如果未授予，判断是否需要向用户解释为什么需要该权限。
 * 3. 申请权限。
 * 4. 处理权限申请的结果。
 */
public class MainActivity extends AppCompatActivity {

    // 定义权限请求码，用于在回调中区分不同的请求
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

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
            // 点击按钮时，启动权限检查流程
            checkCameraPermission();
        });
    }

    /**
     * 检查并申请相机权限的核心流程
     */
    private void checkCameraPermission() {
        // 步骤 1: 检查权限是否已经授予
        // ContextCompat.checkSelfPermission 用于检查特定权限的状态
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // 权限已经被授予，可以直接执行相关操作
            openCamera();
        } else {
            // 权限未被授予，进入申请流程

            // 步骤 2: 判断是否需要向用户解释为什么需要该权限
            // ActivityCompat.shouldShowRequestPermissionRationale 如果返回 true，
            // 表示用户之前拒绝过该权限（且没有勾选"不再询问"），此时应该给用户一个解释，说明为什么App需要这个权限。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // 可以在这里弹出一个对话框或者Toast来解释
                // 解释完后，再次调用 requestPermissions
                Toast.makeText(this, "我们需要相机权限来拍照，请授权", Toast.LENGTH_LONG).show();
                
                // 这里的逻辑可以根据需求做的更复杂，比如弹窗点击确定后再申请
                requestCameraPermission();
            } else {
                // 不需要解释（第一次申请，或者用户之前拒绝并勾选了"不再询问"），直接申请
                requestCameraPermission();
            }
        }
    }

    /**
     * 发起权限申请
     */
    private void requestCameraPermission() {
        // 步骤 3: 申请权限
        // 系统会弹出一个标准的权限申请对话框
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    /**
     * 模拟打开相机的操作
     */
    private void openCamera() {
        Toast.makeText(this, "相机权限已获取，正在打开相机...", Toast.LENGTH_SHORT).show();
        // 实际开发中这里会是启动相机Intent的代码
    }

    /**
     * 步骤 4: 处理权限申请的结果
     * 当用户在系统权限对话框中做出选择（允许或拒绝）后，系统会回调这个方法。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // 检查 grantResults 数组是否为空以及第一个结果是否为 GRANTED
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户点击了"允许"
                openCamera();
            } else {
                // 用户点击了"拒绝"
                // 注意：如果用户之前勾选了"不再询问"，这里也会直接回调拒绝。
                // 此时 shouldShowRequestPermissionRationale 会返回 false。
                Toast.makeText(this, "您拒绝了相机权限，无法使用此功能", Toast.LENGTH_SHORT).show();
                
                // 如果是用户勾选了"不再询问"导致无法再次弹出权限框，
                // 通常建议引导用户去系统设置页面手动开启权限。
            }
        }
    }
}
