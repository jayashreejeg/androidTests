package com.example.walmart.presentation.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.test.core.app.ApplicationProvider
import com.example.walmart.domain.di.ServiceProvider
import com.example.walmart.domain.di.add
import com.example.walmart.domain.di.module
import com.example.walmart.domain.error.ErrorFormatter
import com.example.walmart.domain.model.Country
import com.example.walmart.domain.provider.DispatcherProvider
import com.example.walmart.domain.repo.CountryRepo
import com.example.walmart.presentation.countries.CountriesViewModel
import com.example.walmart.presentation.countries.CountriesViewModelFactory
import com.example.walmart.presentation.details.CountryDetailsViewModel
import com.example.walmart.presentation.details.CountryDetailsViewModelFactory
import com.example.walmart.presentation.di.presentationModule
import com.example.walmart.data.di.dataModule
import com.example.walmart.data.di.networkModule
import com.example.walmart.domain.di.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    @Before
    fun setup() {
        try {
            ServiceProvider.initialize(
                presentationModule,
                dataModule,
                networkModule,
                module {

                    add<Context> {
                        ApplicationProvider.getApplicationContext<Application>()
                    }

                    add<DispatcherProvider> {
                        object : DispatcherProvider {
                             val io: CoroutineDispatcher = Dispatchers.Unconfined
                             val main: CoroutineDispatcher = Dispatchers.Unconfined
                             val default: CoroutineDispatcher = Dispatchers.Unconfined
                        }
                    }

                    add<ErrorFormatter> {
                        object : ErrorFormatter {
                            override fun getDisplayErrorMessage(throwable: Throwable): String {
                                return throwable.message ?: "Unknown error"
                            }
                        }
                    }

                    add<CountryRepo> {
                        object : CountryRepo {
                            override suspend fun getCountries(): List<Country> =
                                listOf(Country("Afghanistan", "Asia", "AF", "Kabul"))

                            override suspend fun getCountryDetailsByCode(code: String): Country =
                                Country("Afghanistan", "Asia", "AF", "Kabul")
                        }
                    }

                    add(CountriesViewModelFactory::class) {
                        CountriesViewModelFactory(
                            repo = get(),
                            dispatchers = get(),
                            errorFormatter = get()
                        )
                    }

                    add(CountryDetailsViewModelFactory::class) {
                        CountryDetailsViewModelFactory(
                            repo = get(),
                            dispatchers = get(),
                            errorFormatter = get()
                        )
                    }


                }
            )
        } catch (_: IllegalStateException) {}
    }

    @Test
    fun `main activity launches`() {
        val activity = Robolectric.buildActivity(MainActivity::class.java)
            .setup()
            .get()

        assertNotNull(activity)
    }
}
