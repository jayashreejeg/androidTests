package com.example.walmart.domain.usecase

import com.example.walmart.domain.model.Country
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SearchCountryAlphaNumUseCaseTest(
    private val queryWithAlphaNumber: String,
    private val expected: List<String>
) {

    private lateinit var useCase: SearchCountryUseCase
    private lateinit var sample: List<Country>

    companion object {
        /**
         * Parameterized cases cover:
         * - blank/whitespace behavior
         * - partial name matches
         * - region matches
         * - unicode/diacritic handling
         * - no-match scenarios
         * - whitespace handling (leading/trailing)
         * Note: Search is accent-sensitive by design. "COTE" should NOT match "Côte".
         */
        @JvmStatic
        @Parameterized.Parameters(name = "query=\"{0}\" → {1}")
        fun cases() = listOf(
            arrayOf("", listOf(
                "Afghanistan", "Albania", "Algeria", "Andorra", "Åland Islands", "Côte d'Ivoire"
            )),
            arrayOf("alb", listOf("Albania")),
            arrayOf("Europe", listOf("Albania", "Andorra", "Åland Islands")),
            arrayOf("xyz", emptyList<String>()),
            arrayOf("åland", listOf("Åland Islands")),
            arrayOf("COTE", emptyList<String>()) ,
            arrayOf(" AFG", emptyList<String>()),
            arrayOf("AFG ", emptyList<String>())
        )
    }


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
    fun `search returns expected results for query`() {
        println("inside the test method with query: \"$queryWithAlphaNumber\"")
        val result = useCase(sample, queryWithAlphaNumber).map { it.name }
        assertEquals(expected, result)
    }

}
