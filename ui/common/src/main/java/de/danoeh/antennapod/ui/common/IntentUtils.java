package de.danoeh.antennapod.ui.common;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import de.danoeh.antennapod.storage.preferences.UserPreferences;

public abstract class IntentUtils {
    private static final String TAG = "IntentUtils";

    /*
     *  Checks if there is at least one exported activity that can be performed for the intent
     */
    public static boolean isCallable(final Context context, final Intent intent) {
        ResolveInfo info = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return info != null && info.activityInfo.exported;
    }

    public static void sendLocalBroadcast(Context context, String action) {
        context.sendBroadcast(new Intent(action).setPackage(context.getPackageName()));
    }

    public static void openInBrowser(Context context, String url) {
        if (UserPreferences.isKidLockActive()) {
            Toast.makeText(context, R.string.kid_lock_blocked, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.pref_no_browser_found, Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
