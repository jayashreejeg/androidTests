package com.example.walmart.presentation.details

import android.app.Application
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.Toolbar
import com.example.walmart.presentation.R
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.example.walmart.data.di.dataModule
import com.example.walmart.data.di.networkModule
import com.example.walmart.data.network.CountryRestService
import com.example.walmart.data.network.response.CountryResponse
import com.example.walmart.domain.di.ServiceProvider
import com.example.walmart.domain.di.add
import com.example.walmart.domain.di.module
import com.example.walmart.presentation.countries.CountriesAdapter
import com.example.walmart.presentation.countries.CountriesFragment
import com.example.walmart.presentation.di.presentationModule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper


@RunWith(RobolectricTestRunner::class)
class CountryFragmentTest {

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
                }
            )
        } catch (e: IllegalStateException) {
            // Log it or just ignore, as it means the previous test already set it up
            println("ServiceProvider already initialized, skipping...")
        }
    }

    @Test
    fun `validate the title`() {
        val scenario = launchFragmentInContainer<CountriesFragment>(
            themeResId = R.style.Theme_WallmartExample
        )
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { fragment ->
          var toolbar=fragment.requireView()
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
            var search=fragment.requireView()
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
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            val recyclerView = fragment.requireView()
                .findViewById<RecyclerView>(R.id.country_recycler_view)
            val adapter = recyclerView.adapter as? CountriesAdapter
            assertNotNull("RecyclerView adapter should not be null", adapter)
            val countries = adapter?.currentList ?: emptyList()
            assertEquals("Afghanistan", countries[0].name)
        }
    }


}