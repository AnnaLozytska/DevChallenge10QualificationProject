package devchallenge.android.radiotplayer.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.squareup.otto.Bus;

import devchallenge.android.radiotplayer.event.Event;

public class EventManager {

    private static volatile EventManager instance;
    private static final int MSG_POST_EVENT = 1;

    public static EventManager getInstance(Fragment fragment) {
        return getInstance(fragment.getContext());
    }

    public static EventManager getInstance(Context context) {
        if (instance == null) {
            synchronized (EventManager.class) {
                if (instance == null) {
                    instance = new EventManager();
                }
            }
        }
        return instance;
    }

    private final Bus eventBus = new Bus();

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_POST_EVENT:
                    eventBus.post(msg.obj);
                    break;
            }
        }
    };

    private EventManager() {
    }

    public void postEvent(Event event) {
        if (Utils.isRunningOnMainThread()) {
            eventBus.post(event);
        } else {
            Message.obtain(handler, MSG_POST_EVENT, event).sendToTarget();
        }
    }

    public void registerEventListener(Object object) {
        eventBus.register(object);
    }

    public void unregisterEventListener(Object object) {
        eventBus.unregister(object);
    }

}
