package com.example.fallsafety;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class TelephonyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getCallState()){
            case TelephonyManager.CALL_STATE_RINGING:

                break;
            case TelephonyManager.CALL_STATE_IDLE:

                AudioManager man= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                man.setSpeakerphoneOn(false);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:



                AudioManager manager= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                manager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                manager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),0);
                manager.setSpeakerphoneOn(true);


                break;

        }
    }
}
