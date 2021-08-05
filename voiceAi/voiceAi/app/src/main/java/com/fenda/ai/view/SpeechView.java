package com.fenda.ai.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.aispeech.dui.dds.DDS;
import com.aispeech.dui.dds.exceptions.DDSNotInitCompleteException;
import com.fenda.ai.R;

public class SpeechView {


    private WindowManager wManager;
    private TextView tx_input;
    private String input_text;

    private boolean isShow;
    private WindowManager.LayoutParams wmParams;
    private View view;


    public SpeechView(Context mContext){
        getWindowManager(mContext);
    }


    /**
     * @category 实例化WindowManager 初次模拟位置时候使用
     * @param context
     */
    private void getWindowManager(final Context context) {
        wManager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        wmParams.format = PixelFormat.TRANSPARENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
               ;
        wmParams.gravity = Gravity.BOTTOM;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.windowAnimations = R.style.view_anim;


        view = LayoutInflater.from(context).inflate(R.layout.popuplayout,null);
        tx_input = view.findViewById(R.id.tv_input);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("TAG","viewTouch  = "+event.getAction() );
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        wManager.removeView(view);
                        isShow = false;
                        try {
                            DDS.getInstance().getAgent().stopDialog();
                        } catch (DDSNotInitCompleteException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });

    }

    public void showView(final String msg){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (!isShow){
//                    wManager.addView(view,wmParams);
                    tx_input.setText(msg);
                    isShow = true;
                }else {
                    tx_input.setText(msg);
                }
            }
        });

    }

    public void closeView(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (isShow){
//                    wManager.removeView(view);
                }
                isShow = false;
            }
        });

    }




}
