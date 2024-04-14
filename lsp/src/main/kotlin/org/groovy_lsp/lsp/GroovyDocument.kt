package org.groovy_lsp.lsp;

import org.eclipse.lsp4j.Range
import org.groovy_lsp.lsp.state.Document
import org.eclipse.lsp4j.Position
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.ErrorCollector
import groovy.lang.GroovyClassLoader


class GroovyDocument(override val uri: String, contents: String, override var version: Int): Document {

    var lineStartsPositions = ArrayList<Int>()
    override var numberOfLines = 0;
    override var totalRange: Range = Range(Position(0,0), Position(0,0))

    override var text: String = contents
        set(value) {
            calculateLineBreaks(value)
            field = value
        }

    init {
        calculateLineBreaks(contents)
    }

    override fun update(newText: String) {
        update(newText, totalRange)
    }

    override fun update(newText: String, range: Range) {
        val savedLength = text.length - 1
        var originalText = StringBuilder(text)
        val startIndex = transformPositionToLinear(range.start.line, range.start.character)
        val endIndex = transformPositionToLinear(range.end.line, range.end.character)
        if (endIndex <= savedLength) {
            // Text modification is within the existing length
            text = originalText.replace(startIndex, endIndex, newText).toString()
        } else if (startIndex > savedLength){
            text = originalText.append(newText).toString()
        } else {
            // Text modification overflows the existing length
            val unchangedText = originalText.substring(0, Math.min(startIndex, savedLength))
            text = StringBuilder(unchangedText).append(newText).toString()
        }
        version = version
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
        numberOfLines = lineStartsPositions.size
        totalRange.setEnd(Position(numberOfLines, 0))
    }

    override fun transformPositionToLinear(line: Int, character: Int): Int {
        if (line >= lineStartsPositions.size) {
            return text.length
        }
        var linearPosition = lineStartsPositions[line] + character
        return linearPosition
    }

    override fun asSourceUnit(configuration: CompilerConfiguration, loader: GroovyClassLoader, er: ErrorCollector): SourceUnit {
        return SourceUnit(this.uri, this.text, configuration, loader, er)
    }
}
