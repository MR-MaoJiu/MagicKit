package club.yuanwanji.magickit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

/**
 * Created by 光头强 on 2016/8/12 0012.
 */
public class AndroidCall {
    public static final int SELECT_PIC_KITKAT = 1;
    public static final int SELECT_PIC = 2;
    private Context context;

    public AndroidCall(Context context) {
        this.context = context;
    }

    /**
     * 打开图库选择图片
     * 此方法供js调用
     */
    @JavascriptInterface
    @SuppressLint("JavascriptInterface")
    public void selectImg(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.KITKAT){
            ((MainActivity)context).startActivityForResult(intent, SELECT_PIC_KITKAT);
        }else{
            ((MainActivity)context).startActivityForResult(intent, SELECT_PIC);
        }
    }
}