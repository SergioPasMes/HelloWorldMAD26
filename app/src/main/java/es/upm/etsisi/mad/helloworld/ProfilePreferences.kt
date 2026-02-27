package es.upm.etsisi.mad.helloworld;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfilePreferences {
    private static final String profilePreferences = "UserSession";

    private static final String keyAlias = "alias";
    private static final String keyAge = "age";
    private static final String keyPicturePath = "profile_picture";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(profilePreferences, Context.MODE_PRIVATE);
    }


    public static String getAlias(Context context) {
        return getPrefs(context).getString(keyAlias, "");
    }

    public static int getAge(Context context) {
        return getPrefs(context).getInt(keyAge, 0);
    }

    public static String getProfilePicture(Context context) {
        return getPrefs(context).getString(keyPicturePath, "");
    }

    public static void setAlias(Context context, String alias) {
        getPrefs(context).edit().putString(keyAlias, alias).apply();
    }

    public static void setAge(Context context, int age) {
        getPrefs(context).edit().putInt(keyAge, age).apply();
    }

    public static void setProfilePicture(Context context, String path) {
        getPrefs(context).edit().putString(keyPicturePath, path).apply();
    }
}
