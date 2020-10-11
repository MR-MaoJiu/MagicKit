package club.yuanwanji.magickit;

import android.app.Application;

/*
* 音频设置
* */
public final  class AudioSetting  extends Application {

    private boolean isPlayBatteryAudio;

    public boolean isPlayBatteryAudio() {
        return isPlayBatteryAudio;
    }

    public void setPlayBatteryAudio(boolean playBatteryAudio) {
        isPlayBatteryAudio = playBatteryAudio;
    }

    public boolean isPlayScreenAudio() {
        return isPlayScreenAudio;
    }

    public void setPlayScreenAudio(boolean playScreenAudio) {
        isPlayScreenAudio = playScreenAudio;
    }

    public String getOpenScreenPath() {
        return openScreenPath;
    }

    public void setOpenScreenPath(String openScreenPath) {
        this.openScreenPath = openScreenPath;
    }

    public String getCloseScreenPath() {
        return closeScreenPath;
    }

    public void setCloseScreenPath(String closeScreenPath) {
        this.closeScreenPath = closeScreenPath;
    }

    public String getChargingPath() {
        return chargingPath;
    }

    public void setChargingPath(String chargingPath) {
        this.chargingPath = chargingPath;
    }

    public String getDischargingPath() {
        return dischargingPath;
    }

    public void setDischargingPath(String dischargingPath) {
        this.dischargingPath = dischargingPath;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    private boolean isPlayScreenAudio;
    private  String openScreenPath;
    private  String closeScreenPath;
    private  String chargingPath;
    private  String dischargingPath;
    private  String fullPath;


}
