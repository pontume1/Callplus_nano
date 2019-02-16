package com.luncher.bounjour.ringlerr.services;

import android.net.Uri;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;

import com.google.firebase.database.annotations.NotNull;
import com.luncher.bounjour.ringlerr.CallManager;
import com.luncher.bounjour.ringlerr.MyDbHelper;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallService extends InCallService {

    @Override
    public void onCallAdded(@NotNull Call call) {

        String number = getNumber(call);
        String number_s = number.replace(" ", "");
        number_s = "+91"+getLastnCharacters(number_s, 10);
        MyDbHelper myDbHelper = new MyDbHelper(getApplicationContext(), null, 1);

        String block_number = myDbHelper.checkBlockNumber(number_s);
        if(!block_number.equals("null")){
            call.disconnect();
        }else {
            super.onCallAdded(call);
//            call.registerCallback(callCallback);
//            Intent intent = new Intent(this, CallActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//
//            int call_status = call.getState();
//
//            if(call_status == 9){
//                CallManager.get().placeCall(number);
//            }else {
//                CallManager.get().updateCall(call);
//            }
        }

    }

    public void onCallRemoved(@NotNull Call call) {
        super.onCallRemoved(call);
        call.unregisterCallback(callCallback);
        CallManager.get().updateCall(null);
    }

    // Copy-pasted from built-in phone app
    String getNumber(Call call) {
        if (call == null) {
            return null;
        }
        if (call.getDetails().getGatewayInfo() != null) {
            return call.getDetails().getGatewayInfo()
                    .getOriginalAddress().getSchemeSpecificPart();
        }
        Uri handle = getHandle(call);
        return handle == null ? null : handle.getSchemeSpecificPart();
    }

    Uri getHandle(Call call) {
        return call == null ? null : call.getDetails().getHandle();
    }

    public String getLastnCharacters(String inputString,
                                     int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }

    private Call.Callback callCallback = new Call.Callback() {

        @Override
        public void onStateChanged(Call call, int state) {
            CallManager.get().updateCall(call);
        }
    };
}
