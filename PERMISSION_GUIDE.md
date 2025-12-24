# Android 敏感权限开发指南

本文档基于项目中的实际问答，总结了 Android 敏感权限（Runtime Permissions）的开发流程与核心 API 语法解释。

## 1. 核心流程概览

在 Android 6.0 (API 23) 及以上版本，使用敏感权限（如相机、联系人、定位等）需要遵循 **“检查 -> 解释 -> 申请 -> 回调”** 的标准流程。

## 2. 常见问题与 API 语法详解

### Q1: 代码中的 `Manifest.permission.CAMERA` 和 XML 里的声明是一样的吗？

**是的，必须一致，缺一不可。**

*   **XML 声明**: 向系统注册应用需要此权限。
    ```xml
    <uses-permission android:name="android.permission.CAMERA" />
    ```
*   **Java 代码**: 引用官方定义的常量字符串。
    ```java
    Manifest.permission.CAMERA  // 实际值是 "android.permission.CAMERA"
    ```
    *如果只在代码里申请而没有在 XML 里声明，权限申请会直接失败。*

---

### Q2: `checkSelfPermission` 语法解释

```java
if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED)
```

*   **`ContextCompat.checkSelfPermission`**: 一个兼容性方法，用于检查权限状态。
    *   **参数 1 (`this`)**: `Context` 对象（通常是当前的 Activity），用于获取应用环境信息。
    *   **参数 2**: 要检查的权限名称字符串。
*   **返回值**:
    *   `PackageManager.PERMISSION_GRANTED` (0): **已授权**。
    *   `PackageManager.PERMISSION_DENIED` (-1): **未授权**。

---

### Q3: `shouldShowRequestPermissionRationale` 语法与作用

```java
if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
    // 弹出 Toast 或 Dialog 解释
}
```

*   **作用**: 判断是否需要向用户解释“为什么需要这个权限”。
*   **返回值逻辑**:
    *   **`true` (需要解释)**: 用户**之前拒绝过**该权限，但**没有**勾选“不再询问”。此时应弹窗（Toast/Dialog）解释用途，提高通过率。
    *   **`false` (无需解释)**:
        1.  第一次申请。
        2.  用户拒绝并勾选了“不再询问”（系统禁止再次弹窗）。
        3.  权限已授权。

---

### Q4: `requestPermissions` 语法解释

```java
ActivityCompat.requestPermissions(
    this,
    new String[]{Manifest.permission.CAMERA},
    CAMERA_PERMISSION_REQUEST_CODE
);
```

*   **参数 1 (`this`)**: `Activity` 实例。系统需要知道申请结束后，把结果回调给哪个页面。
*   **参数 2 (`new String[]{...}`)**: **权限数组**。
    *   因为可以一次性申请多个权限，所以必须传数组。
    *   例如：`new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}`。
*   **参数 3 (`REQUEST_CODE`)**: **请求码**。
    *   **作用**: 一个唯一标识符（整数），用于在回调方法中区分这是哪一次请求。

---

### Q5: 请求码（Request Code）是自己定义的吗？

**是的。**

*   它只要是一个 `int` 类型整数即可（建议使用 0-65535 之间的正整数）。
*   **流程**:
    1.  你申请时传入 `100`。
    2.  用户操作完，系统调用 `onRequestPermissionsResult`。
    3.  系统把 `100` 原样传回来。
    4.  你判断 `if (requestCode == 100)`，确认这是相机的回调，而不是联系人或其他请求的回调。

## 3. 完整代码模板

```java
// 1. 检查权限
if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
    // A. 已有权限 -> 直接做事
    openCamera();
} else {
    // B. 没有权限 -> 判断是否需要解释
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
        // B1. 需要解释 -> 弹窗告知用户，用户点确定后再申请
        Toast.makeText(this, "需要相机拍照", Toast.LENGTH_LONG).show();
        requestCameraPermission(); 
    } else {
        // B2. 不需要解释 -> 直接申请
        requestCameraPermission();
    }
}

// 申请方法
void requestCameraPermission() {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
}

// 回调处理
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == 100) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera(); // 成功
        } else {
            // 失败（可能是拒绝，也可能是拒绝+不再询问）
        }
    }
}
```