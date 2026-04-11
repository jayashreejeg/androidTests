package com.example.walmart.presentation.details

import android.app.Application
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
                    add<android.content.Context>  { ApplicationProvider.getApplicationContext<Application>() }
                    // 👇 THIS is the missing piece (example)
                    // 👇 THIS is the real fix (name depends on repo)
                    add<CountryRestService> {
                        object : CountryRestService {

                            override suspend fun getCountries(): List<CountryResponse> {
                                return List(249) {
                                    CountryResponse(
                                        name = "Country $it",
                                        region = "Region $it",
                                        code = "C$it",
                                        capital = "Capital $it"
                                    )
                                }
                            }
                        }
                    }
                }
            )
        } catch (e: Exception) {
            // Ignore if ServiceProvider is not initialized
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

            val adapter = recyclerView.adapter

            assertNotNull(adapter)

            val count = adapter!!.itemCount

            println("ITEM COUNT = $count")

            assertEquals(249, count)
        }
    }
}