package powerup.systers.com.powerup.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import powerup.systers.com.AvatarRoomActivity;
import powerup.systers.com.BuildConfig;
import powerup.systers.com.MapActivity;
import powerup.systers.com.R;
import powerup.systers.com.StartActivity;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class LoadButtonTests {

    private StartActivity activity;
    private ActivityController<StartActivity> activityController;

    @Before
    public void setUp() throws Exception {
        activityController = Robolectric.buildActivity(StartActivity.class);
        activity = activityController
                .create()
                .resume()
                .get();
    }

    /**
     * StartActivity can launch two intents on clicking 'Load Game' depending upon whether a game has been
     * created already or not. We click the 'Load Game' button and check if AvatarRoomActivity is launched,
     * and then we edit the preferences to set that we have already created a game. Then we restart the
     * activity, click the 'Load Game' button again and check if MapActivity is launched.
     */
    @Test
    public void clickingLoadLaunchesAppropriateIntent() throws Exception {
        Class Map = MapActivity.class, AvatarRoom = AvatarRoomActivity.class;
        Intent expectedIntentMap = new Intent(activity, Map), expectedIntentAvatarRoom = new Intent(activity, AvatarRoom);

        // click on 'Load Game' and check if AvatarRoomActivity is launched
        activity.findViewById(R.id.startButtonMain).callOnClick();
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        assertTrue(expectedIntentAvatarRoom.filterEquals(actualIntent));

        // edit preferences to set that we have already created a game
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        sharedPreferences.edit().putBoolean(activity.getString(R.string.preferences_has_previously_started), Boolean.TRUE).commit();

        // restart the activity, click on 'Load Game' again and check if MapActivity is launched
        activityController.restart().resume();
        activity.findViewById(R.id.startButtonMain).callOnClick();
        actualIntent = shadowActivity.getNextStartedActivity();

        assertTrue(expectedIntentMap.filterEquals(actualIntent));
    }
}