package club.yuanwanji.magickit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;
import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.ACTION_SCREEN_ON;
import static android.content.Intent.ACTION_USER_PRESENT;

/**
 * 自定义 广播接收者
 * 继承 android.content.BroadcastReceiver
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private String boot_action ="android.intent.action.BOOT_COMPLETED";
    boolean chargingflg=true;
    boolean dischargingflg=true;
    boolean fullflg=true;
    MediaPlayer mMediaPlayer=new MediaPlayer();
     boolean isPlayBatteryAudio;
     boolean isPlayScreenAudio;
      String openScreenPath;
      String closeScreenPath;
      String chargingPath;
      String dischargingPath;
      String fullPath;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action =intent.getAction();
        int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
        int level = intent.getIntExtra("level", 0);
        int scale = intent.getIntExtra("scale", 0);
        int plugged = intent.getIntExtra("plugged", 0);
        int voltage = intent.getIntExtra("voltage", 0);
        Log.i(TAG, "==========================>");
        if(isPlayScreenAudio)
        {
            switch (action){
                case Intent.ACTION_BOOT_COMPLETED:
                    Log.e("TAG","手机开机了");
                    break;
                case  Intent.ACTION_SHUTDOWN:
                    Log.e("TAG","手机关机了");
                    break;
                case ACTION_SCREEN_ON:
                    Log.e("TAG","亮屏");
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();

                        if(openScreenPath.isEmpty()) {mMediaPlayer = MediaPlayer.create(context, R.raw.hmbb);}
                        else {mMediaPlayer = MediaPlayer.create(context, Uri.parse(openScreenPath));}
                        mMediaPlayer.start();
                    } else {
                        if(openScreenPath.isEmpty()) {mMediaPlayer = MediaPlayer.create(context, R.raw.hmbb);}
                        else {mMediaPlayer = MediaPlayer.create(context, Uri.parse(openScreenPath));}
                        mMediaPlayer.start();
                    }
                    break;
                case ACTION_SCREEN_OFF:
                    Log.e("TAG","息屏");
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        if(closeScreenPath.isEmpty())
                        {
                            mMediaPlayer = MediaPlayer.create(context, R.raw.dy);
                        }else {mMediaPlayer = MediaPlayer.create(context, Uri.parse(closeScreenPath));}

                        mMediaPlayer.start();
                    } else {
                        if(closeScreenPath.isEmpty())
                        {
                            mMediaPlayer = MediaPlayer.create(context, R.raw.dy);
                        }else {mMediaPlayer = MediaPlayer.create(context, Uri.parse(closeScreenPath));}

                        mMediaPlayer.start();
                    }
                    break;
                case ACTION_USER_PRESENT:
                    Log.e("TAG","手机解锁");
                    break;
            }
        }

        String statusString = "";
        if(isPlayBatteryAudio)
        {
            switch (status) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    statusString = "unknown";
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    statusString = "charging";
                    if(chargingflg)
                    {
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                            if(chargingPath.isEmpty())
                            {
                                mMediaPlayer = MediaPlayer.create(context, R.raw.cd);
                            }else {
                                mMediaPlayer = MediaPlayer.create(context, Uri.parse(chargingPath));
                            }

                            mMediaPlayer.start();
                        } else {
                            if(chargingPath.isEmpty())
                            {
                                mMediaPlayer = MediaPlayer.create(context, R.raw.cd);
                            }else {
                                mMediaPlayer = MediaPlayer.create(context, Uri.parse(chargingPath));
                            }
                            mMediaPlayer.start();
                        }
                        chargingflg=false;
                        dischargingflg=true;
                    }
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    statusString = "discharging";
                    if(dischargingflg)
                    {
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                            if(dischargingPath.isEmpty())
                            { mMediaPlayer = MediaPlayer.create(context, R.raw.rr);}else {
                                mMediaPlayer = MediaPlayer.create(context, Uri.parse(dischargingPath));
                            }

                            mMediaPlayer.start();
                        } else {
                            if(dischargingPath.isEmpty())
                            { mMediaPlayer = MediaPlayer.create(context, R.raw.rr);}else {
                                mMediaPlayer = MediaPlayer.create(context, Uri.parse(dischargingPath));
                            }
                            mMediaPlayer.start();
                        }
                        dischargingflg=false;
                        chargingflg=true;
                        fullflg=true;
                    }
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    statusString = "not charging";

                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    statusString = "full";
                    if(fullflg)
                    {
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                            if(fullPath.isEmpty()){ mMediaPlayer = MediaPlayer.create(context, R.raw.tz);}
                            else {
                                mMediaPlayer = MediaPlayer.create(context, Uri.parse(fullPath));
                            }

                            mMediaPlayer.start();
                        } else {
                            if(fullPath.isEmpty()){ mMediaPlayer = MediaPlayer.create(context, R.raw.tz);}
                            else {
                                mMediaPlayer = MediaPlayer.create(context, Uri.parse(fullPath));
                            }
                            mMediaPlayer.start();
                        }
                        fullflg=false;
                        dischargingflg=true;
                    }
                    break;
            }
        }

        String acString = "";

        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                acString = "plugged ac";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                acString = "plugged usb";
                break;
        }

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS ");
        String date = sDateFormat.format(new java.util.Date());

        Log.e(TAG, "battery: date=" + date + ",status " + statusString
                + ",level=" + level +",scale=" + scale
                + ",voltage=" + voltage +",acString=" + acString );

        if(boot_action.equals(action)){
            Log.e("TAG","开机自启");


        }
    }
}