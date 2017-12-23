package powerup.systers.com.powerup.test;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;

import powerup.systers.com.AvatarRoomActivity;
import powerup.systers.com.BuildConfig;
import powerup.systers.com.R;
import powerup.systers.com.StartActivity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml",constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class StartTests {

    private StartActivity activity;

    // launches the activity for our use
    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(StartActivity.class)
                .create()
                .resume()
                .get();
    }

    /**
     * The New User button in the opened StartActivity is clicked, and positive button of
     * the opened AlertDialog is pressed. It then checks if the expected and the actual intent
     * to AvatarRoomActivity matches.
     *
     * Note that the AlertDialog should be from the package android.app.AlertDialog. The support
     * library version of AlertDialog is not working and is a known issue:
     * https://github.com/robolectric/robolectric/issues/1944
     */
    @Test
    public void positiveDialogPress() throws Exception {
        Class AvatarRoom = AvatarRoomActivity.class;
        Intent expectedIntent = new Intent(activity, AvatarRoom);

        activity.findViewById(R.id.newUserButtonFirstPage).callOnClick();
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();

        assertTrue(alertDialog.isShowing());
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).callOnClick();

        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivityForResult().intent;

        assertTrue(expectedIntent.filterEquals(actualIntent));
    }

    /**
     * The New User button in the opened StartActivity is clicked, and negative button of
     * the opened AlertDialog is pressed. It then checks if the AlertDialog is closed.
     *
     * Note that the AlertDialog should be from the package android.app.AlertDialog. The support
     * library version of AlertDialog is not working and is a known issue:
     * https://github.com/robolectric/robolectric/issues/1944
     */
    @Test
    public void negativeDialogPress() throws Exception {
        activity.findViewById(R.id.newUserButtonFirstPage).callOnClick();
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();

        assertTrue(alertDialog.isShowing());
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).callOnClick();
        assertFalse(alertDialog.isShowing());
    }
}
