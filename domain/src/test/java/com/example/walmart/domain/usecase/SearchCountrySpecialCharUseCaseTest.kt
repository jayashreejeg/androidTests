package com.example.walmart.domain.usecase

import com.example.walmart.domain.model.Country
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchCountrySpecialCharUseCaseTest() {
    private lateinit var useCase: SearchCountryUseCase
    private lateinit var sample: List<Country>

    @Before
    fun setUp() {
        useCase = SearchCountryUseCase()
        sample = listOf(
            Country("Afghanistan", "Asia", "AF", "Kabul"),
            Country("Albania", "Europe", "AL", "Tirana"),
            Country("Algeria", "Africa", "DZ", "Algiers"),
            Country("Andorra", "Europe", "AD", "Andorra la Vella"),
            Country("Åland Islands", "Europe", "AX", "Mariehamn"),
            Country("Côte d'Ivoire", "Africa", "CI", "Yamoussoukro")
        )
    }


    @Test
    fun `whitespace-only query should behave like blank`() {
        println("inside the test method")
        val result = useCase(sample, "   ")
        assertEquals(sample, result)
    }

    @Test
    fun `queries containing only punctuation should not match anything`() {
        val result = useCase(sample, "@@@")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `search must not mutate the input list`() {
        val original = sample.toMutableList()
        val snapshot = original.toList()
        useCase(original, "a")
        assertEquals(snapshot, original)
    }

    @Test
    fun `search should behave consistently with large datasets`() {
        val bigList = (1..10_000).map {
            Country("Country$it", if (it % 2 == 0) "Europe" else "Asia", "C$it", "Capital$it")
        }
        val result = useCase(bigList, "Europe")
        assertTrue(result.size in 4800..5200)
    }
}
