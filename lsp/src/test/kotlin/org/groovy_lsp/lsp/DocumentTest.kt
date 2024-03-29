package org.groovy_lsp.lsp;

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.api.Assertions.assertEquals

class DocumentTest {

    @ParameterizedTest
    @MethodSource("documentContents")
    fun `Document should calculate the number of lines upon intialization correctly`(text: String, lineBreaks: Int) {
        val document = Document("", text, 1)
        assertEquals(lineBreaks, document.lineStartsPositions.size)

    }

    @ParameterizedTest
    @MethodSource("documentContents")
    fun `Document should calculate the number of lines upon update correctly`(text: String, lineBreaks: Int) {
        val document = Document("", "", 1)
        document.text = text
        assertEquals(lineBreaks, document.lineStartsPositions.size)

    }

    @ParameterizedTest
    @MethodSource("documentChange")
    fun `Document should translate from coordinate to linear position`(line: Int, column: Int, expected: Int) {
        val document = Document("", "Line 1\nLine 2\nLine 3", 1)
        assertEquals(expected, document.transformPositionToLinear(line, column))
    }

    companion object {
        @JvmStatic
        fun documentContents() = listOf(
            Arguments.of("", 1),
            Arguments.of("Hello World", 1),
            Arguments.of("Hello \n World", 2),
            Arguments.of("Hello \n Wold \n New line", 3),
        )

        @JvmStatic
        fun documentChange() = listOf(
            Arguments.of(0, 0, 0),
            Arguments.of(0, 1, 1),
            Arguments.of(1, 0, 7),
            Arguments.of(2, 5, 19),
        )
    }
}
