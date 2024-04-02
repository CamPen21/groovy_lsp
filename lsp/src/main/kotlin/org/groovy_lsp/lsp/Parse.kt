package org.groovy_lsp.lsp

import groovy.lang.GroovyShell
import groovy.lang.Script
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException;
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.DiagnosticSeverity
import java.util.ArrayList

class Parse {

    fun parseWithErrorPosition(text: String): List<Diagnostic> {
        val shell = GroovyShell()
        try {
            shell.parse(text)
        } catch (e: MultipleCompilationErrorsException) {
            var diagnostics = ArrayList<Diagnostic>()
            e.errorCollector.errors.forEach { error ->
                if (error is SyntaxErrorMessage) {
                    val cause: SyntaxException = error.getCause()
                    val range = Range(Position(cause.startLine-1, cause.startColumn-1),
                    Position(cause.endLine-1, cause.endColumn-1))
                    diagnostics.add(Diagnostic(range, cause.message, DiagnosticSeverity.Error, "Compiler"))
                }
            }
            return diagnostics
        } catch (e: CompilationFailedException) {
            // Catch other compilation failures that might not be MultipleCompilationErrorsException
            return ArrayList()
        } catch (e: Exception) {
            // Catch any other exceptions
            return ArrayList()
        }
        return ArrayList()
    }
}
