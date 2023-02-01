package cc.imorning.chat.monitor;

import android.util.Log;

import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Presence;

public class RosterListener {
    private static final String TAG = "RosterListener";

    public static StanzaListener rosterListener = packet -> {
        if (packet instanceof Presence presence) {
            String fromId = presence.getFrom().toString();
            if (presence.getType().equals(Presence.Type.subscribe)) {
                Log.d(TAG, fromId + "processPacket: subscribe");
            } else if (presence.getType().equals(Presence.Type.subscribed)) {
                Log.d(TAG, fromId + "processPacket: 对方同意订阅");
            } else if (presence.getType().equals(Presence.Type.unsubscribe)) {
                Log.d(TAG, fromId + "processPacket: 取消订阅");
            } else if (presence.getType().equals(Presence.Type.unsubscribed)) {
                Log.d(TAG, fromId + "processPacket: 拒绝订阅");
            } else if (presence.getType().equals(Presence.Type.unavailable)) {
                Log.d(TAG, fromId + "processPacket: 离线");
            } else if (presence.getType().equals(Presence.Type.available)) {
                Log.d(TAG, fromId + "processPacket: 上线");
            }
        } else {
            Logger.xml(packet.toXML().toString());
        }
    };
}
