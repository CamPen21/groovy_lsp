package org.groovy_lsp.lsp.state;

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.Position

class HashMapBasedStateHandlerTest {

    @Test
    fun `State should be correcly maintained`() {
        val initialState = "Line 1\nLine 2\nLine 3" 
        var docsHandler = HashMapBasedStateHandler()
        docsHandler.addDocument("file:///test.file", initialState, 1)
        // Set L to lower case
        val p0 = Position(0, 0)
        val p1 = Position(0, 1)
        docsHandler.updateDocument("file:///test.file", "l", 2, Range(p0, p1))

        // Add another Line
        val p2 = Position(2, 6)
        val p3 = Position(3, 6)
        docsHandler.updateDocument("file:///test.file", "\nLine 4", 3, Range(p2, p3))

        // Add a Single character at the end
        val p4 = Position(3, 6)
        val p5 = Position(3, 7)
        docsHandler.updateDocument("file:///test.file", ";", 4, Range(p4, p5))

        // Add a Single character at the end
        val p6 = Position(3, 6)
        val p7 = Position(3, 7)
        docsHandler.updateDocument("file:///test.file", "", 5, Range(p6, p7))

        // Expected state after all mutations
        val finalState = "line 1\nLine 2\nLine 3\nLine 4"
        assertEquals(finalState, docsHandler.getDocument("file:///test.file").text)
    }

    companion object {
        @JvmStatic
        fun changes() = listOf(
            Arguments.of(0, 0, 0),
            Arguments.of(0, 1, 1),
            Arguments.of(1, 0, 7),
            Arguments.of(2, 5, 19),
        )
    }

}
