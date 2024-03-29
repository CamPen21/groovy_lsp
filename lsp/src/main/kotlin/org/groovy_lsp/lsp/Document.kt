package org.groovy_lsp.lsp;


class Document (var uri: String, contents: String, var version: Int) {

    var lineStartsPositions = ArrayList<Int>()

    var text: String = contents
        set(value) {
            calculateLineBreaks(value)
            field = value
        }

    init {
        calculateLineBreaks(contents)
    }

    private fun calculateLineBreaks(text: String) {
        /* TODO: Make this method more efficient, right now this gets
        * recomputed all the time
        */
        lineStartsPositions.clear()
        // Line 0 always starts at 0
        lineStartsPositions.add(0)
        var index = text.indexOf('\n')
        while (index >= 0) {
            lineStartsPositions.add(index + 1)
            index = text.indexOf('\n', index + 1)
        }
    }

    fun transformPositionToLinear(line: Int, character: Int): Int {
        if (line >= lineStartsPositions.size) {
            return text.length
        }
        var linearPosition = lineStartsPositions[line] + character
        return linearPosition
    }
}
