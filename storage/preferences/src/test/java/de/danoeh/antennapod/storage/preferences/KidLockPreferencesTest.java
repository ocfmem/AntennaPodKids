package de.danoeh.antennapod.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class KidLockPreferencesTest {
    private static final String PASSWORD_KEY = "prefParentalControlPassword";
    private SharedPreferences prefs;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().commit();
        UserPreferences.init(context);
    }

    @Test
    public void passwordIsStoredHashed() {
        UserPreferences.setParentalControlPassword("1234");
        String stored = prefs.getString(PASSWORD_KEY, "");
        assertTrue(stored.startsWith("pbkdf2:"));
        assertFalse(stored.contains("1234"));
        assertTrue(UserPreferences.verifyParentalControlPassword("1234"));
        assertFalse(UserPreferences.verifyParentalControlPassword("1235"));
        assertFalse(UserPreferences.verifyParentalControlPassword(""));
    }

    @Test
    public void samePasswordGetsDifferentSalts() {
        UserPreferences.setParentalControlPassword("1234");
        String first = prefs.getString(PASSWORD_KEY, "");
        UserPreferences.setParentalControlPassword("1234");
        assertNotEquals(first, prefs.getString(PASSWORD_KEY, ""));
    }

    @Test
    public void plainTextPasswordIsMigratedOnSuccess() {
        prefs.edit().putString(PASSWORD_KEY, "4321").commit();
        assertFalse(UserPreferences.verifyParentalControlPassword("0000"));
        assertEquals("4321", prefs.getString(PASSWORD_KEY, ""));

        assertTrue(UserPreferences.verifyParentalControlPassword("4321"));
        assertTrue(prefs.getString(PASSWORD_KEY, "").startsWith("pbkdf2:"));
        assertTrue(UserPreferences.verifyParentalControlPassword("4321"));
    }

    @Test
    public void localKidLockNeedsPasswordAndSwitch() {
        assertFalse(UserPreferences.isKidLockActive());
        prefs.edit().putBoolean(UserPreferences.PREF_KID_LOCK_ENABLED, true).commit();
        assertFalse(UserPreferences.isKidLockActive());

        UserPreferences.setParentalControlPassword("1234");
        assertTrue(UserPreferences.isKidLockActive());
        assertFalse(UserPreferences.isKidLockManaged());

        UserPreferences.clearParentalControlPassword();
        assertFalse(UserPreferences.isKidLockActive());
    }
}
