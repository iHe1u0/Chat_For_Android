package cc.imorning.chat.monitor;

import android.util.Log;

import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Presence;

import cc.imorning.chat.BuildConfig;
import cc.imorning.common.CommonApp;
import cc.imorning.database.dao.DataDatabaseDao;
import cc.imorning.database.db.DataDB;

public class RosterListener {
    private static final String TAG = "RosterListener";
    /**
     * listener for roster change
     */
    public static StanzaListener rosterListener = packet -> {

        if (packet instanceof Presence presence) {

            String fromId = presence.getFrom().asBareJid().toString();

            if (presence.getType().equals(Presence.Type.subscribe)) {
                // when get a new subscribe
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, fromId + "请求订阅");
                }
                // Write this request into database

            } else if (presence.getType().equals(Presence.Type.subscribed)) {
                Log.d(TAG, fromId + "同意订阅");
            } else if (presence.getType().equals(Presence.Type.unsubscribe)) {
                Log.d(TAG, fromId + "取消订阅");
            } else if (presence.getType().equals(Presence.Type.unsubscribed)) {
                Log.d(TAG, fromId + "拒绝订阅");
            } else if (presence.getType().equals(Presence.Type.unavailable)) {
                // when I logout
            } else if (presence.getType().equals(Presence.Type.available)) {
                // when I login
            }
        }
    };
    private final DataDatabaseDao databaseDao = DataDB.Companion.getInstance(CommonApp.Companion.getContext(),
            CommonApp.Companion.getXmppTcpConnection().getUser().asEntityBareJidString()).databaseDao();
}
