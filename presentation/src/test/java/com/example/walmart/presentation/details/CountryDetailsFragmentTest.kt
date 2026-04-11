    package com.example.walmart.presentation.details

    import android.app.Application
    import androidx.appcompat.widget.Toolbar
    import com.example.walmart.presentation.R
    import android.os.Bundle
    import android.widget.TextView
    import androidx.fragment.app.testing.launchFragmentInContainer
    import androidx.lifecycle.Lifecycle
    import androidx.recyclerview.widget.RecyclerView
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
    import org.robolectric.Shadows
    import org.robolectric.Shadows.shadowOf
    import org.robolectric.shadows.ShadowLooper


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
                        add<android.content.Context>  { ApplicationProvider.getApplicationContext<Application>() }
                    }
                )
            } catch (e: Exception) {
                // Ignore if ServiceProvider is not initialized
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
                println("countries title -- "+fragment.getString(R.string.countries_title))
                println("code view -- "+fragment.getText(R.id.code_view))
            }
        }
    }