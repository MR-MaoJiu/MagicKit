package club.yuanwanji.magickit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.List;

public class AudioSetting extends AppCompatActivity  implements View.OnClickListener{
    SharedPreferences sharedPreferences;
    Switch switch_screen, switch_battery;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_setting);
        sharedPreferences=getSharedPreferences("AudioSetting",MODE_PRIVATE);
        switch_screen=findViewById(R.id.switch2);
        switch_battery=findViewById(R.id.switch3);
        Button btn1 =(Button) findViewById(R.id.button2);
        btn1.setOnClickListener(this);
        Button btn2 =(Button) findViewById(R.id.button3);
        btn2.setOnClickListener(this);
        Button btn3 =(Button) findViewById(R.id.button4);
        btn3.setOnClickListener(this);
        Button btn4 =(Button) findViewById(R.id.button5);
        btn4.setOnClickListener(this);
        Button btn5 =(Button) findViewById(R.id.button6);
        btn5.setOnClickListener(this);
        Button btn6 =(Button) findViewById(R.id.button7);
        btn6.setOnClickListener(this);
        switch_screen.setChecked(sharedPreferences.getBoolean("screen",true));
        switch_battery.setChecked(sharedPreferences.getBoolean("battery",true));
        switch_screen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("TAG", String.valueOf(isChecked));
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("screen",isChecked);
                editor.commit();
            }
        });
        switch_battery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("TAG", String.valueOf(isChecked));
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("battery",isChecked);
                editor.commit();
            }
        });

    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        switch(v.getId()) {
            case R.id.button2:
                startActivityForResult(intent,1);
                break;
            case R.id.button3:
                startActivityForResult(intent,2);
                break;
            case R.id.button4:
                startActivityForResult(intent,3);
                break;
            case R.id.button5:
                startActivityForResult(intent,4);
                break;
            case R.id.button6:
                startActivityForResult(intent,5);
                break;
            case R.id.button7:
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.clear();
                editor.commit();
                finish();
                break;
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
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
            SharedPreferences.Editor editor = sharedPreferences.edit();


            switch (requestCode) {
                case 1:

                    editor.putString("open", path);
                    editor.commit();
                    Log.e("TAG", path + "=============>1");
                    break;
                case 2:
                    editor.putString("close", path);
                    editor.commit();
                    Log.e("TAG", path + "===========>2");
                    break;
                case 3:
                    editor.putString("charging", path);
                    editor.commit();
                    Log.e("TAG", path + "===========>3");
                    break;
                case 4:
                    editor.putString("discharging", path);
                    editor.commit();
                    Log.e("TAG", path + "===========>4");
                    break;
                case 5:
                    editor.putString("full", path);
                    editor.commit();
                    Log.e("TAG", path + "===========>5");
                    break;

            }

        }
//        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
//
//        }
    }

}