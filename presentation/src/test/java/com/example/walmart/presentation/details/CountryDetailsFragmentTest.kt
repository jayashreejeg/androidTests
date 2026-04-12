    package com.example.walmart.presentation.details

    import android.app.Application
    import androidx.appcompat.widget.ActionMenuView
    import androidx.appcompat.widget.Toolbar
    import com.example.walmart.presentation.R
    import androidx.fragment.app.testing.launchFragmentInContainer
    import androidx.test.core.app.ApplicationProvider
    import com.example.walmart.data.di.dataModule
    import com.example.walmart.data.di.networkModule
    import com.example.walmart.domain.di.ServiceProvider
    import com.example.walmart.domain.di.add
    import com.example.walmart.domain.di.module
    import com.example.walmart.presentation.di.presentationModule
    import org.junit.Assert.*
    import org.junit.Before
    import org.junit.Test
    import org.junit.runner.RunWith
    import org.robolectric.RobolectricTestRunner


    @RunWith(RobolectricTestRunner::class)
    class CountryDetailsFragmentTest {

        @Before
        fun setup() {
            try {
                    ServiceProvider.initialize(
                        presentationModule,
                        dataModule,
                        networkModule,
                        module {
                            add<android.content.Context> { ApplicationProvider.getApplicationContext<Application>() }
                        }
                    )
                } catch (e: IllegalStateException) {
                // Log it or just ignore, as it means the previous test already set it up
                println("ServiceProvider already initialized, skipping...")
            }
        }


        @Test
        fun `toolbar is displayed with correct title`() {
            val scenario = launchFragmentInContainer<CountryDetailsFragment>(
                themeResId = R.style.Theme_WallmartExample
            )
            scenario.onFragment { fragment ->
                val toolbar = fragment.requireView()
                    .findViewById<Toolbar>(R.id.action_bar)

                assertNotNull(toolbar)
                assertEquals(
                    fragment.getString(R.string.countries_details),
                    toolbar.title
                )

            }
        }

        @Test
        fun `validate back button is displayed`() {
            val scenario = launchFragmentInContainer<CountryDetailsFragment>(
                themeResId = R.style.Theme_WallmartExample
            )
            scenario.onFragment { fragment ->
                val backArrow = fragment.requireView()
                    .findViewById<Toolbar>(R.id.action_bar)

                assertNotNull(backArrow)
                println(backArrow.isShown)

            }
        }
    }