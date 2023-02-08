package cc.imorning.database.utils;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cc.imorning.database.BuildConfig;
import cc.imorning.database.db.MessageDB;

public class MessageDatabaseHelper {

    private static final String TAG = "MessageDatabaseHelper";

    private static volatile MessageDatabaseHelper messageDatabaseHelper;
    private static Map<String, MessageDB> messageDBMap;

    private MessageDatabaseHelper() {
        messageDBMap = new HashMap<>();
    }

    public static MessageDatabaseHelper getInstance() {
        if (messageDatabaseHelper == null) {
            synchronized (MessageDatabaseHelper.class) {
                if (messageDatabaseHelper == null) {
                    messageDatabaseHelper = new MessageDatabaseHelper();
                }
            }
        }
        return messageDatabaseHelper;
    }

    public void addNewInstance(Context context, String user, String me) {

        if (messageDBMap.containsKey(me)) {
            return;
        }
        // MessageDB messageDB = Room.databaseBuilder(
        //         context,
        //         MessageDB.class,
        //         DatabaseHelper.INSTANCE.getMessageDatabase(context, user, me)
        // ).build();
        MessageDB messageDB = MessageDB.Companion.getInstance(context, user, me);
        messageDBMap.put(user, messageDB);
    }

    public MessageDB getMessageDatabaseDao(Context context, String user, String me) {
        if (messageDBMap.get(user) == null) {
            addNewInstance(context, user, me);
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "getMessageDatabaseDao: " + messageDBMap.get(user));
        }
        return messageDBMap.get(user);
    }

}
