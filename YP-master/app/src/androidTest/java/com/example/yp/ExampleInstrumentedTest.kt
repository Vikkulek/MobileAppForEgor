package com.example.yp

import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class TeacherActivityTest {

    @get:Rule
    var activityRule = ActivityTestRule(TeacherActivity::class.java)

    @Before
    fun setUp() {
    }

    @Test
    fun testInitialUIState() {
        onView(withId(R.id.tableLayout)).check(matches(isDisplayed()))
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(System.currentTimeMillis())
        onView(withId(R.id.textViewDate)).check(matches(withText(currentDate)))
    }

    @Test
    fun testSelectDateButton() {
        onView(withId(R.id.buttonSelectDate)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonSelectDate)).perform(click())

        onView(withText("OK")).check(matches(isDisplayed()))
    }

    @Test
    fun testLoadData() {
        onView(withId(R.id.buttonSelectDate)).perform(click())

        onView(withText("OK")).perform(click())

        onView(withId(R.id.tableLayout)).check(matches(hasMinimumChildCount(1)))
    }
}