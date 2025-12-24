# Android 动态权限申请教学示例 (Permission Check Flow)

你好！欢迎来到这个教学项目。

作为一个刚接触 Android 开发的学生，你可能会发现 Android 的权限管理有点复杂。这个项目就是专门为了帮你理清 **Android 6.0+ 动态权限申请** 的标准流程而设计的。

---

## 1. 这个项目做了什么？

这个项目非常直观，它模拟了真实 App 中最常见的场景：
*   **场景一**：用户点击按钮想拍照，App 检查有没有 **相机权限**。
*   **场景二**：用户点击按钮想看好友列表，App 检查有没有 **读取联系人权限**。

通过这两个功能，演示了权限申请的完整生命周期：
1.  **查 (Check)**：先看看有没有证（权限）。
2.  **问 (Ask)**：如果没有，就向用户申请。
3.  **解释 (Explain)**：如果用户之前拒绝过，还要耐心解释为什么需要。
4.  **答 (Result)**：用户点完“允许”或“拒绝”后，App 该怎么处理。

---

## 2. 核心“工具箱”介绍 (Imports / 头文件)

在 Java/Android 开发中，我们不叫“头文件”，而是叫 **Import（导入包）**。你可以把它们理解为我们为了完成任务，从仓库里借来的不同功能的“工具箱”。

以下是本项目 `MainActivity.java` 中用到的核心工具箱及其用途：

### A. 权限相关的核心工具 (必须掌握)

1.  **`android.Manifest`**
    *   **老师通俗解释**：这是 Android 的 **“权限字典”**。
    *   **用途**：所有的权限名字都写在这里面。比如相机权限叫 `Manifest.permission.CAMERA`，联系人叫 `Manifest.permission.READ_CONTACTS`。我们在代码里引用权限时，必须用它，不能手写字符串，容易拼错。

2.  **`androidx.core.content.ContextCompat`**
    *   **老师通俗解释**：这是 **“兼容性检查员”**。
    *   **用途**：主要用到它的 `checkSelfPermission()` 方法。不管用户的手机是 Android 10 还是 Android 14，用它来检查“是否有权限”是最安全、最标准的做法。

3.  **`androidx.core.app.ActivityCompat`**
    *   **老师通俗解释**：这是 **“兼容性办事员”**。
    *   **用途**：
        *   用它的 `requestPermissions()` 方法来 **发起申请**（弹窗）。
        *   用它的 `shouldShowRequestPermissionRationale()` 方法来判断 **是否需要解释**。
        *   它帮我们屏蔽了不同系统版本的差异，确保功能在旧手机上也能跑。

4.  **`android.content.pm.PackageManager`**
    *   **老师通俗解释**：这是 **“裁判”**。
    *   **用途**：它定义了检查结果的标准常量。
        *   `PackageManager.PERMISSION_GRANTED` = **通过/允许** (值为0)
        *   `PackageManager.PERMISSION_DENIED` = **拒绝** (值为-1)
    *   我们在代码里的 `if` 判断中，就是拿检查结果跟这个裁判的标准做比较。

### B. 界面与交互工具 (基础知识)

5.  **`androidx.appcompat.app.AppCompatActivity`**
    *   **用途**：这是所有页面的 **“基类”**（爸爸）。我们的 `MainActivity` 必须继承它，才能拥有显示界面、生命周期等能力。

6.  **`android.widget.Button`**
    *   **用途**：界面上的 **按钮**。我们需要监听它的点击事件来触发权限流程。

7.  **`android.widget.Toast`**
    *   **用途**：**小气泡提示**。在这个 Demo 中，我们用它来充当“解释员”和“反馈员”，告诉用户“为什么需要权限”以及“权限申请成功了”。

---

## 3. 学习建议

作为老师，我建议你按照以下顺序阅读代码：

1.  打开 `AndroidManifest.xml`，看一眼 `<uses-permission ... />` 是怎么声明的。（**切记：这一步不做，代码写出花来也没用！**）
2.  打开 `MainActivity.java`，找到 `checkCameraPermission()` 方法，通读一遍逻辑。
3.  重点理解 `onRequestPermissionsResult` 回调方法，看看系统是怎么把结果还给你的。

祝你学习愉快！如果有不懂的，随时回来复习。