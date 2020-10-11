package club.yuanwanji.magickit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.Observable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dingmouren.videowallpaper.VideoWallpaper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    TextView myText;
//    MediaPlayer mMediaPlayer=new MediaPlayer();
//    IntentFilter intentFilter;
    boolean flg=true;
MyBroadcastReceiver receiver;
    // 下载进度条
    private ProgressBar progressBar ;
    // 是否终止下载
    private boolean isInterceptDownload = false;
    //进度条显示数值
    AlertDialog alertDialog3;
    private int progress = 0;
    String code;
    private static final String savePath = Environment.getExternalStorageDirectory().getPath();
    private static final String saveFileName = savePath + "/魔法盒.apk";
    String serveraddress="app.yuanwanji.club";
    String body="软件更新";
    String appurl="";
    private Context mContext;
    private VideoWallpaper mVideoWallpaper = new VideoWallpaper();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    public  static  boolean  isQQClientAvailable(Context context){
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName; if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false; }

    //    private MyBroadcastReceiver mbatteryReceiver;
    SharedPreferences sharedPreferences;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         sharedPreferences=getSharedPreferences("AudioSetting",MODE_PRIVATE);
////        String url= sharedPreferences.getString("path","");
//        if(!url.isEmpty())
//        {
//            mVideoWallpaper.setToWallPaper(this,url);
//        }
        verifyStoragePermissions(MainActivity.this);
        mContext=this;
        Update();
        myText=(TextView)findViewById(R.id.message);
        myText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "";
                //判断QQ是否安装（“*”是需要联系QQ号）
                if (isQQClientAvailable(MainActivity.this)) {
                    //安装了QQ会直接调用QQ，打开手机QQ进行会话
                    str = "mqqwpa://im/chat?chat_type=wpa&uin=481869314&version=1&src_type=web&web_src=oicqzone.com";
                } else {
                    //没有安装QQ会展示网页
                    str = "http://wpa.qq.com/msgrd?v=3&uin=481869314&site=qq&menu=yes";
                }
                Uri uri = Uri.parse(str);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });
//        mbatteryReceiver=new MyBroadcastReceiver();
//        intentFilter=new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
//        registerReceiver(mbatteryReceiver, intentFilter);
        if(flg)
        {
            IntentFilter recevierFilter=new IntentFilter();
            recevierFilter.addAction(Intent.ACTION_SCREEN_ON);
            recevierFilter.addAction(Intent.ACTION_SCREEN_OFF);
            recevierFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
            recevierFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            receiver=new MyBroadcastReceiver();
            receiver.isPlayBatteryAudio=sharedPreferences.getBoolean("battery",true);
            receiver.isPlayScreenAudio=sharedPreferences.getBoolean("screen",true);
            receiver.chargingPath=sharedPreferences.getString("charging","");
            receiver.dischargingPath=sharedPreferences.getString("discharging","");
            receiver.fullPath=sharedPreferences.getString("full","");
            receiver.openScreenPath=sharedPreferences.getString("open","");
            receiver.closeScreenPath=sharedPreferences.getString("close","");
            registerReceiver(receiver, recevierFilter);
        }
        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AlipayUtil.hasInstalledAlipayClient(MainActivity.this)){
                    boolean flg= AlipayUtil.startAlipayClient(MainActivity.this, "FKX03798GCD9KN1C5ND28F");  //第二个参数代表要给被支付的二维码code  可以在用草料二维码在线生成
                    if(flg)
                    {
                        Toast.makeText(MainActivity.this, "感谢您的支持我将会加倍努力！！！", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "没有检测到支付宝客户端", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageButton btn= (ImageButton) findViewById(R.id.cd);
        ImageButton btn2= (ImageButton) findViewById(R.id.bz);
        ImageButton btn3= (ImageButton) findViewById(R.id.sp);
        TextView tv=findViewById(R.id.tv_v);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!code.trim().equals(getVersionName().trim())) {
                        Log.e("VVV", code + "   " + getVersionName());
                        showUpdataDialog();
                    } else {
                        Toast.makeText(MainActivity.this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) { e.printStackTrace();}
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent it=new Intent(MainActivity.this,Cinema.class);
            startActivity(it);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(MainActivity.this,AudioSetting.class);
                startActivity(it);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
           Uri uri = data.getData(); // 获取用户选择文件的URI
            String[] proj = {MediaStore.Images.Media.DATA};
            //好像是android多媒体数据库的封装接口，具体的看Android文档
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            //按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String path = cursor.getString(column_index);
           // SharedPreferences.Editor editor=sharedPreferences.edit();
//            editor.putString("path",path);
//            editor.commit();
            mVideoWallpaper.setToWallPaper(this,path);
            System.out.println(path);
        }
    }
    /**
     * 弹出对话框
     */
    protected void showUpdataDialog() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this) ;
        builer.setTitle("版本升级");
        builer.setMessage(body);

        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("确定", (dialog, which) -> downloadApk());
        //当点取消按钮时不做任何举动
        builer.setNegativeButton("取消", (dialogInterface, i) -> {});
        AlertDialog dialog = builer.create();
        dialog.show();
    }
    /**
     * 下载apk
     */
    @SuppressLint("WrongConstant")
    private void downloadApk(){
        progressBar=new ProgressBar(this,null, android.R.attr.progressBarStyleHorizontal);

        alertDialog3 = new AlertDialog.Builder(MainActivity.this)
                .setTitle("更新中：")//标题
                .setView(progressBar)
                .setIcon(R.mipmap.ic_launcher)//图标
                .create();

        alertDialog3.show();
        //开启另一线程下载
        Thread downLoadThread = new Thread(downApkRunnable);
        downLoadThread.start();
    }
    /**
     * 从服务器下载新版apk的线程
     */
    private Runnable downApkRunnable = new Runnable(){
        @Override
        public void run() {
            String path = android.os.Environment.getExternalStorageState();
            System.out.println(path);
            if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                //如果没有SD卡
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("当前设备无SD卡，数据无法下载");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return;
            }else if(appurl != null){
                try {
                    //服务器上新版apk地址
                    URL url = new URL(appurl);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String apkFile = saveFileName;
                    File ApkFile = new File(apkFile);
                    FileOutputStream fos = new FileOutputStream(ApkFile);
                    int count = 0;
                    byte buf[] = new byte[1024];

                    do{
                        int numRead = is.read(buf);
                        count += numRead;
                        //更新进度条
                        progress = (int) (((float) count / length) * 100);
                        handler.sendEmptyMessage(1);
                        if(numRead <= 0){
                            //下载完成通知安装
                            handler.sendEmptyMessage(0);
                            isInterceptDownload = true;
                            alertDialog3.dismiss();
                            break;
                        }
                        fos.write(buf,0,numRead);
                        //当点击取消时，则停止下载
                    }while(!isInterceptDownload);
                    fos.close();
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("获取服务器版本信息错误，数据无法下载");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }
    };
    /**
     * 声明一个handler来跟进进度条
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // 更新进度情况
                    progressBar.setProgress(progress);
                    break;
                case 0:
                    progressBar.setVisibility(View.INVISIBLE);
                    // 安装apk文件
                    installApk();
                    break;
                default:
                    break;
            }
        };
    };
    /**
     * 安装apk
     */
    private void installApk() {
        Log.i("TAG", "开始执行安装: " + saveFileName);
        File apkFile = new File(saveFileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.w("TAG", "版本大于 N ，开始使用 fileProvider 进行安装");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    mContext
                    , "club.yuanwanji.magickit"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Log.w("TAG", "正常进行安装");
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }
    public String getVersionName() throws Exception
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String version = packInfo.versionName;
        return version;
    }

    /**
     * 获取最新版本信息
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public void Update(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //你的URL
                    String url_s = "http://"+serveraddress+"/versionmfh.txt";
                    URL url = new URL(url_s);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    //设置连接属性。不喜欢的话直接默认也阔以
                    conn.setConnectTimeout(5000);//设置超时
                    conn.setUseCaches(false);//数据不多不用缓存了

                    //这里连接了
                    conn.connect();
                    //这里才真正获取到了数据
                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader input = new InputStreamReader(inputStream);
                    BufferedReader buffer = new BufferedReader(input);
                    if(conn.getResponseCode() == 200){//200意味着返回的是"OK"
                        String inputLine;
                        StringBuffer resultData  = new StringBuffer();//StringBuffer字符串拼接很快
                        while((inputLine = buffer.readLine())!= null){
                            resultData.append(inputLine);
                        }
                        String json = resultData.toString();
                        JSONObject jsonObject = new JSONObject(json);
                        code = jsonObject.getString("code");
                        appurl=jsonObject.getString("url");
                        body=jsonObject.getString("update");
                        if(!code.trim().equals(getVersionName().trim()))
                        {
                            Log.e("VVV",code+"   "+getVersionName());
                            showUpdataDialog();
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

}

