package com.example.walmart.presentation.countries

import android.app.Application
import android.view.View
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.Toolbar
import com.example.walmart.presentation.R
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.walmart.data.di.dataModule
import com.example.walmart.data.di.networkModule
import com.example.walmart.data.network.CountryRestService
import com.example.walmart.data.network.response.CountryResponse
import com.example.walmart.domain.di.ServiceProvider
import com.example.walmart.domain.di.add
import com.example.walmart.domain.di.get
import com.example.walmart.domain.di.module
import com.example.walmart.presentation.di.presentationModule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper


@RunWith(RobolectricTestRunner::class)
class CountryFragmentTest {

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        try {
            ServiceProvider.initialize(
                presentationModule,
                dataModule,
                networkModule,
                module {
                    add<android.content.Context> { ApplicationProvider.getApplicationContext<Application>() }
                    add<CountryRestService> {
                        object : CountryRestService {
                            override suspend fun getCountries(): List<CountryResponse> {
                                return List(249) {
                                    CountryResponse(
                                        name = if (it == 0) "Afghanistan" else "Country $it", // Real name for index 0
                                        region = "Asia",
                                        code = "AF",
                                        capital = "Kabul"
                                    )
                                }
                            }

                    }
                }
                    add(CountriesViewModelFactory::class) {
                        CountriesViewModelFactory(
                            repo = get(),
                            dispatchers = get(),
                            errorFormatter = get()
                        )
                    }
        }
            ) }
        catch (e: IllegalStateException) {
            // Log it or just ignore, as it means the previous test already set it up
            println("ServiceProvider already initialized, skipping...")
        }

        // Navigation controller
        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        ).apply {
            setGraph(R.navigation.main_graph)
        }
    }

    @Test
    fun `validate the title`() {
        val scenario = launchFragmentInContainer<CountriesFragment>(
            themeResId = R.style.Theme_WallmartExample
        )
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { fragment ->
          val toolbar=fragment.requireView()
                .findViewById<Toolbar>(R.id.action_bar)
            assertNotNull(toolbar)
            assertEquals(
                fragment.getString(R.string.countries_title),
                toolbar.title
            )

        }
    }

    @Test
    fun `validate the search is shown and clickable`() {
        val scenario = launchFragmentInContainer<CountriesFragment>(
            themeResId = R.style.Theme_WallmartExample
        )
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { fragment ->
            val search=fragment.requireView()
                .findViewById<ActionMenuItemView>(R.id.action_search)
            assertNotNull(search)
            println(search.isShown)
            search.performClick()
            search.context.resources.getString(R.string.search)?.let {
                println(search.contentDescription)
                assertEquals(it, search.contentDescription)
            }
        }
    }

    @Test
    fun `all countries are displayed in the list`() {
        val scenario = launchFragmentInContainer<CountriesFragment>(
            themeResId = R.style.Theme_WallmartExample
        )
        scenario.moveToState(Lifecycle.State.RESUMED)

        scenario.onFragment { fragment ->
            // Drain ALL pending tasks
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            ShadowLooper.idleMainLooper()


            val recyclerView = fragment.requireView()
                .findViewById<RecyclerView>(R.id.country_recycler_view)

            val adapter = recyclerView.adapter as CountriesAdapter
            val countries = adapter.currentList

            assertTrue("List should not be empty", countries.isNotEmpty())
            println(countries.size)
            assertEquals("Afghanistan", countries[0].name)
        }
    }


    @Test
    fun `clicking a country navigates to details`() {
        val scenario = launchFragmentInContainer<CountriesFragment>(
            themeResId = R.style.Theme_WallmartExample
        )

        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView()
                .findViewById<RecyclerView>(R.id.country_recycler_view)

            // Drain ALL pending tasks
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            ShadowLooper.idleMainLooper()

            // Force RecyclerView to layout children
            recyclerView.measure(
                View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.AT_MOST)
            )
            recyclerView.layout(0, 0, 1080, 1920)
        }

        // Now the list is populated and view holders exist
        onView(withId(R.id.country_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, click()
                )
            )

        assertEquals(
            R.id.countryDetailsFragment,
            navController.currentDestination?.id
        )
    }



}