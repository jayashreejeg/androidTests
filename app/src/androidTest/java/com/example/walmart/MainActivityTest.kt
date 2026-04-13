package com.example.walmart

import android.content.Intent
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.walmart.presentation.main.MainActivity
import com.example.walmart.presentation.R
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.hamcrest.Matcher

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "Wait for $millis milliseconds"
            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }

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
    fun validateCountryViewTest() {
        onView(isRoot()).perform(waitFor(2000))
        onView(withText("Afghanistan, AS"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun searchFunctionalityOnCountryListPage() {
        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(android.widget.EditText::class.java))
            .perform(typeText("Afghanistan"), pressImeActionButton())
        Thread.sleep(2000)
        onView(withText("Afghanistan, AS")).check(matches(isDisplayed()))
    }

    @Test
    fun validateScrollOnCountryListPage() {
        onView(isRoot()).perform(waitFor(2000))
        onView(withId(R.id.country_recycler_view))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(containsString("Chile")))
                )
            )
        onView(withText(containsString("Chile"))).check(matches(isDisplayed()))
      }


    @Test
    fun validateCountryClickOnCountryListPage() {
        onView(isRoot()).perform(waitFor(2000))
        onView(withId(R.id.country_recycler_view))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(containsString("Chile")))
                )
            )
        onView(withText(containsString("Chile"))).check(matches(isDisplayed()))
        onView(withText(containsString("Chile"))).perform(click())
        onView(isRoot()).perform(waitFor(2000))
        onView(withText("Chile, SA")).check(matches(isDisplayed()))
    }



    @Test
    fun validateBackOnCountryDetailsPage() {
        onView(isRoot()).perform(waitFor(2000))
        onView(withId(R.id.country_recycler_view))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(containsString("Chile")))
                )
            )
        onView(withText(containsString("Chile"))).check(matches(isDisplayed()))
        onView(withText(containsString("Chile"))).perform(click())
        onView(isRoot()).perform(waitFor(2000))
        onView(withText("Chile, SA")).check(matches(isDisplayed()))
        onView(
            allOf(
                instanceOf(ImageButton::class.java),
                withParent(withId(R.id.action_bar))
            )
        ).perform(click())
        onView(isRoot()).perform(waitFor(2000))
        onView(withText("Countries List")).check(matches(isDisplayed()))
    }
}
