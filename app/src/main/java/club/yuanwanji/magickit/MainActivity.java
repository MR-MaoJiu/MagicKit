package club.yuanwanji.magickit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.Observable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dingmouren.videowallpaper.VideoWallpaper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity  {
    TextView myText;
//    MediaPlayer mMediaPlayer=new MediaPlayer();
//    IntentFilter intentFilter;
MyBroadcastReceiver receiver;
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
        myText=(TextView)findViewById(R.id.message);
//        mbatteryReceiver=new MyBroadcastReceiver();
//        intentFilter=new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
//        registerReceiver(mbatteryReceiver, intentFilter);
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
        ImageButton btn= (ImageButton) findViewById(R.id.cd);
        ImageButton btn2= (ImageButton) findViewById(R.id.bz);
        ImageButton btn3= (ImageButton) findViewById(R.id.sp);
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
    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

}

