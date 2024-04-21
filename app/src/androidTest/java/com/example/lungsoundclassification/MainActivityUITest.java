package com.example.lungsoundclassification;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;

import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import android.content.Intent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.MediaType;
import okhttp3.RequestBody;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        init();
    }

    @After
    public void tearDown() {
        release();
    }


    @Test
    public void testInitialUIState() {
        // Check that the upload button is displayed
        onView(withId(R.id.up_upload_audio_btn)).check(matches(isDisplayed()));
    }

    @Test
    public void testOpenFilePicker() {
        // Open the file picker
        onView(withId(R.id.up_upload_audio_btn)).perform(click());

        // Check that the file picker is open
        intended(hasAction(Intent.ACTION_CHOOSER));
        intended(hasExtra(Intent.EXTRA_INTENT, hasAction(Intent.ACTION_OPEN_DOCUMENT)));
        intended(hasExtra(Intent.EXTRA_TITLE, "Select WAV audio"));
        intended(hasExtra(Intent.EXTRA_INTENT, hasType("audio/x-wav")));
    }

    @Test
    public void testSendWavDataToServer() {
        // Setup the data to be sent
        MediaType mediaType = MediaType.parse("audio/x-wav");
        byte[] wavData = new byte[]{1, 2, 3, 4, 5};
        RequestBody requestBody = RequestBody.create(mediaType, wavData);

        // Send the data to the server
        mActivityRule.getScenario().onActivity(activity -> activity.sendWavDataToServer(requestBody));

        // Check the progress bar is displayed while sending data
        onView(withId(R.id.progress_bar_overlay)).check(matches(isDisplayed()));
    }

}
