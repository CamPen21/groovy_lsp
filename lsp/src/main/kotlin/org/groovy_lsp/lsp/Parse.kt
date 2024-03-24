package org.groovy_lsp.lsp

import groovy.lang.GroovyShell
import groovy.lang.Script
import java.io.File
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.messages.SyntaxErrorMessage

class Parse {

    fun parseWithErrorPosition(path: String): String {
        val shell = GroovyShell()
        val groovyFile = File(path)
        try {
            shell.parse(groovyFile)
        } catch (e: MultipleCompilationErrorsException) {
            val messageBuilder = StringBuilder("Syntax error(s) found:\n")
            e.errorCollector.errors.forEach { error ->
                if (error is SyntaxErrorMessage) {
                    val line = error.cause.line
                    val column = error.cause.startColumn
                    val message = error.cause.message // 'message' already provides a formatted error message
                    messageBuilder.append("Line $line, Column $column: $message\n")
                }
            }
            return messageBuilder.toString()
        } catch (e: CompilationFailedException) {
            // Catch other compilation failures that might not be MultipleCompilationErrorsException
            return "Compilation failed: ${e.message}"
        } catch (e: Exception) {
            // Catch any other exceptions
            return "An unexpected error occurred: ${e.message}"
        }
        return "No syntax errors found."
    }

    fun parseWithErrorHandling(path: String): String {
        val shell = GroovyShell()
        val groovyFile = File(path)
        try {
            // Attempt to parse/evaluate the Groovy script file.
            shell.parse(groovyFile)
        } catch (e: Exception) {
            // Catch any exceptions thrown during parsing.
            // Return the error message (or any specific part of it).
            return e.message ?: "An error occurred, but no message is available."
        }
        // If no exceptions are caught, return an indication that the file is fine.
        return "No syntax errors found."
    }

    fun parse(path: String): String {
        val shell = GroovyShell()
        val groovyFile = File(path)
        // Directly execute the script.
        val result = shell.evaluate(groovyFile) as? Script
        // Attempt to retrieve a property named 'name' from the script.
        val nameProperty = result?.getProperty("name")?.toString()
        // Return the name property if it exists; otherwise, return an empty string.
        return nameProperty ?: ""
    }

}
