package com.groovy_lsp.lsp.analysis

import groovy.lang.GroovyShell
import groovy.lang.Script
import org.codehaus.groovy.control.MultipleCompilationErrorsException


class GroovyParser {

    val shell = GroovyShell()

    fun parseString(contents: String): ParsingResult {
        var result = ParsingResult(null, null)
        try {
            result.script = shell.parse(contents)
        } catch (e: MultipleCompilationErrorsException) {
            result.error = e
        }
        return result
    }

}
