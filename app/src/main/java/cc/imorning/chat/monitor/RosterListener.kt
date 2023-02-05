package cc.imorning.chat.monitor;

import android.util.Log;

import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jxmpp.jid.BareJid;

import cc.imorning.chat.BuildConfig;
import cc.imorning.chat.action.RosterAction;
import cc.imorning.common.CommonApp;
import cc.imorning.common.constant.Config;
import cc.imorning.database.dao.DataDatabaseDao;
import cc.imorning.database.db.DataDB;
import cc.imorning.database.entity.RosterEntity;

public class RosterListener {
    private static final String TAG = "RosterListener";
    private static final DataDatabaseDao databaseDao = DataDB.Companion.getInstance(
            CommonApp.Companion.getContext(),
            CommonApp.Companion.getXmppTcpConnection().getUser().asEntityBareJidString()
    ).databaseDao();
    /**
     * listener for roster change,for more information:
     * <a href="https://www.yashinu.com/shili/show-269932.html">xmpp之添加好友请求报文</a>
     */
    public static StanzaListener rosterListener = packet -> {

        if (packet instanceof Presence presence) {

            BareJid fromId = presence.getFrom().asBareJid();

            switch (presence.getType()) {
                case subscribe:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, fromId.toString() + "请求加为好友");
                    }
                    // Write this request into database
                    RosterEntity roster = new RosterEntity(fromId.toString(),
                            RosterAction.INSTANCE.getNickName(fromId.toString()),
                            Message.Type.normal,
                            Config.DEFAULT_GROUP,
                            RosterPacket.ItemType.from);
                    databaseDao.insertRoster(roster);
                    break;
                case subscribed:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, fromId.asBareJid().toString() + "同意了好友请求");
                    }
                    break;
                case unsubscribe:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, fromId.asBareJid().toString() + "拒绝了好友请求");
                    }
                    break;
                case unavailable:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, fromId.asBareJid().toString() + "下线");
                    }
                    break;
                case available:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, fromId.asBareJid().toString() + "上线");
                    }
                    break;
            }
        }
    };
}
