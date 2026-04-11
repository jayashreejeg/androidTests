package com.example.walmart

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.walmart.presentation.main.MainActivity
import com.example.walmart.presentation.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.sql.DriverManager.println

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario= ActivityScenario.launch(
            Intent(
                ApplicationProvider.getApplicationContext(),
                MainActivity::class.java
            )
        )
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun sampleTest() {
        println("inside sample test")
        Thread.sleep(2000)
        // 1. Verify the Search View is visible on the screen
        onView(withText("Afghanistan, AS"))
            .check(matches(isDisplayed()))
    }
}
