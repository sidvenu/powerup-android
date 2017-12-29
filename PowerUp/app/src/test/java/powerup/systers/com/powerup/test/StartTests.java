package powerup.systers.com.powerup.test;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import powerup.systers.com.R;
import powerup.systers.com.StartActivity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.support.membermodification.MemberModifier.replace;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

/**
 * PreferenceManager class should be prepared for test to be used with BDDMockito.given().willreturn()
 * Also, we prepare all the activities in powerup.systers.com package because of the creation of new
 * objects in anonymous inner classes. See https://stackoverflow.com/a/13482340 for more information
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {StartActivity.class, PreferenceManager.class}, fullyQualifiedNames = "powerup.systers.com.*")
public class StartTests {

    private StartActivity activity;

    @Mock
    private Button newUserButton, startButton, aboutButton;

    @Mock
    private AlertDialog.Builder builder;

    @Mock
    private AlertDialog dialog;

    @Mock
    private SharedPreferences preferences;

    @Mock
    private Resources resources;

    @Mock
    private Window window;

    /**
     * Sets up the StartActivity and the conditions for what to return when functions are called
     * on mock objects, as well as starts up the activity in the end
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all mock objects created in global scope

        activity = spy(StartActivity.class); // instantiate StartActivity for testing

        // we replace the findViewById method with this one so that the mocked Views will get returned
        // upon calling findViewById()
        replace(activity.getClass().getMethod("findViewById", int.class)).with(new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                switch ((int) args[0]) {
                    case R.id.newUserButtonFirstPage:
                        return newUserButton;
                    case R.id.startButtonMain:
                        return startButton;
                    case R.id.aboutButtonMain:
                        return aboutButton;
                }
                return null;
            }
        });

        // if not set, NullPointerException will be thrown as the buttons are mock objects
        doNothing().when(newUserButton).setOnClickListener((View.OnClickListener) any());
        doNothing().when(startButton).setOnClickListener((View.OnClickListener) any());
        doNothing().when(aboutButton).setOnClickListener((View.OnClickListener) any());

        // whenNew specifies to return mock object upon creating a new instance of the AlertDialog.Builder object
        whenNew(AlertDialog.Builder.class).withAnyArguments().thenReturn(builder);
        // Whatever method is called, we return the same mock builder
        when(builder.setTitle((CharSequence) any())).thenReturn(builder);
        when(builder.setMessage((CharSequence) any())).thenReturn(builder);
        when(builder.setPositiveButton((CharSequence) any(), (DialogInterface.OnClickListener) any())).thenReturn(builder);
        when(builder.setNegativeButton((CharSequence) any(), (DialogInterface.OnClickListener) any())).thenReturn(builder);
        when(builder.setTitle((CharSequence) any())).thenReturn(builder);
        when(builder.create()).thenReturn(dialog); // return mock dialog on calling builder.create()

        // returns temp for whatever string resource that is to be obtained
        when(activity.getResources()).thenReturn(resources);
        when(resources.getString(anyInt())).thenReturn("temp");

        // returns mock window when dialog.getWindow() is called since dialog is a mock object
        when(dialog.getWindow()).thenReturn(window);

        // if not set, NullPointerException will be thrown as the objects are mock objects
        doNothing().when(window).setBackgroundDrawable((Drawable) any());
        doNothing().when(dialog).show();

        // mocks a static method (getDefaultSharedPreferences()) from PreferenceManager class
        mockStatic(PreferenceManager.class);
        BDDMockito.given(PreferenceManager.getDefaultSharedPreferences((Context) any()))
                .willReturn(preferences);
        when(preferences.getBoolean(anyString(), anyBoolean())).thenReturn(false);

        // suppresses unnecessary method that need not be called
        suppress(activity.getClass().getMethod("setContentView", int.class));

        activity.onCreate(mock(Bundle.class));
    }

    /**
     * verify if activity.findViewById() is called
     */
    @Test
    public void testCallFindViewById() throws Exception {
        verify(activity).findViewById(R.id.newUserButtonFirstPage);
        verify(activity).findViewById(R.id.startButtonMain);
        verify(activity).findViewById(R.id.aboutButtonMain);
    }

    /**
     * click on the new user button and check if dialog.show() is called
     */
    @Test
    public void testNewUserButtonClick() throws Exception {
        ArgumentCaptor<View.OnClickListener> captor =
                ArgumentCaptor.forClass(View.OnClickListener.class);

        // capture the OnClickListener using ArgumentCaptor
        verify(newUserButton).setOnClickListener(captor.capture());

        captor.getValue().onClick(newUserButton);
        verify(dialog).show();
    }

    /**
     * click on the start button and check if activity.startActivity() is called with an intent
     */
    @Test
    public void testStartButtonClick() throws Exception {
        ArgumentCaptor<View.OnClickListener> captor =
                ArgumentCaptor.forClass(View.OnClickListener.class);

        // capture the OnClickListener using ArgumentCaptor
        verify(aboutButton).setOnClickListener(captor.capture());

        captor.getValue().onClick(newUserButton);
        verify(activity).startActivity((Intent) any());
    }

    /**
     * click on the about button and check if activity.startActivity() is called with an intent
     */
    @Test
    public void testAboutButtonClick() throws Exception {
        ArgumentCaptor<View.OnClickListener> captor =
                ArgumentCaptor.forClass(View.OnClickListener.class);

        // capture the OnClickListener using ArgumentCaptor
        verify(aboutButton).setOnClickListener(captor.capture());

        captor.getValue().onClick(newUserButton);
        verify(activity).startActivity((Intent) any());
    }
}
