package com.example.walmart.presentation.countries

import com.example.walmart.domain.error.ErrorFormatter
import com.example.walmart.domain.model.Country
import com.example.walmart.domain.provider.DispatcherProvider
import com.example.walmart.domain.repo.CountryRepo
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountriesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepo = mockk<CountryRepo>()
    private val mockErrorFormatter = mockk<ErrorFormatter>()
    private val mockDispatcherProvider = mockk<DispatcherProvider>()

    private lateinit var viewModel: CountriesViewModel

    private val sampleCountries = listOf(
        Country("Afghanistan", "Asia", "AF", "Kabul"),
        Country("Albania", "Europe", "AL", "Tirana"),
        Country("India", "Asia", "IN", "New Delhi"),
        Country("Côte d'Ivoire", "Africa", "CI", "Yamoussoukro")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockDispatcherProvider.io() } returns testDispatcher
        coEvery { mockRepo.getCountries() } returns sampleCountries
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search should filter items after debounce period`() = runTest {
        viewModel = CountriesViewModel(mockRepo, mockDispatcherProvider, mockErrorFormatter)
        advanceUntilIdle()
        assertEquals(4, viewModel.state.value.items.size)
        viewModel.search("Ind")
        assertEquals(4, viewModel.state.value.items.size)
        testScheduler.advanceTimeBy(201)
        val filteredItems = viewModel.state.value.items
        assertEquals(1, filteredItems.size)
        assertEquals("India", filteredItems[0].name)
    }

    @Test
    fun `search with code`() = runTest {
        viewModel = CountriesViewModel(mockRepo, mockDispatcherProvider, mockErrorFormatter)
        advanceUntilIdle()
        viewModel.search("AL")
        testScheduler.advanceTimeBy(201)
        assertEquals(1, viewModel.state.value.items.size)
    }

    @Test
    fun `search with wildchar`() = runTest {
        viewModel = CountriesViewModel(mockRepo, mockDispatcherProvider, mockErrorFormatter)
        advanceUntilIdle()
        viewModel.search("**")
        testScheduler.advanceTimeBy(201)
        assertEquals(0, viewModel.state.value.items.size)
    }

    @Test
    fun `search with specialchar`() = runTest {
        viewModel = CountriesViewModel(mockRepo, mockDispatcherProvider, mockErrorFormatter)
        advanceUntilIdle()
        viewModel.search("Côte")
        testScheduler.advanceTimeBy(201)
        assertEquals(1, viewModel.state.value.items.size)
        viewModel.search("$$$")
        testScheduler.advanceTimeBy(201)
        assertEquals(0, viewModel.state.value.items.size)
    }

    @Test
    fun `search with empty query should return all original items`() = runTest {
        viewModel = CountriesViewModel(mockRepo, mockDispatcherProvider, mockErrorFormatter)
        advanceUntilIdle()
        viewModel.search("")
        testScheduler.advanceTimeBy(201)
        assertEquals(4, viewModel.state.value.items.size)
    }

    @Test
    fun `search with name`() = runTest {
        viewModel = CountriesViewModel(mockRepo, mockDispatcherProvider, mockErrorFormatter)
        advanceUntilIdle()
        viewModel.search("Albania")
        testScheduler.advanceTimeBy(201)
        assertEquals(1, viewModel.state.value.items.size)
    }

    @Test
    fun `search with partial match`() = runTest {
        viewModel = CountriesViewModel(mockRepo, mockDispatcherProvider, mockErrorFormatter)
        advanceUntilIdle()
        viewModel.search("I")
        testScheduler.advanceTimeBy(201)
        assertEquals(4, viewModel.state.value.items.size)
    }

    @Test
    fun `multiple search calls within debounce should only trigger last query`() = runTest {
        viewModel = CountriesViewModel(mockRepo, mockDispatcherProvider, mockErrorFormatter)
        advanceUntilIdle()
        viewModel.search("A")
        testScheduler.advanceTimeBy(100)
        viewModel.search("Ind")
        testScheduler.advanceTimeBy(201)
        assertEquals(1, viewModel.state.value.items.size)
        assertEquals("India", viewModel.state.value.items[0].name)
    }
}