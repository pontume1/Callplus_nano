package com.luncher.santanu.dailer;

/**
 * Created by santanu on 30/12/17.
 */

import com.github.nkzawa.emitter.Emitter;

class SocketEventListener extends Emitter implements Emitter.Listener {
    private String mEvent;
    private Listener mListener;

    public SocketEventListener(String mEvent, Listener mListener) {
        this.mEvent = mEvent;
        this.mListener = mListener;
    }

    @Override
    public void call(Object... objects) {
        if (this.mListener != null) {
            this.mListener.onEventCall(mEvent, objects);
        }
    }

    public interface Listener {
        void onEventCall(String event, Object... objects);
    }

}