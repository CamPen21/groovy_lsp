package org.groovy_lsp.lsp.diagnostics

import java.util.HashMap
import java.util.stream.Stream
import org.apache.groovy.parser.antlr4.GroovyLangParser
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.control.messages.WarningMessage;
import org.codehaus.groovy.control.ErrorCollector
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.services.LanguageClient

class GroovyDiagnosticsService(): DiagnosticsService {

    override var client: LanguageClient? = null
    val sourceUnits: HashMap<String, SourceUnit> = HashMap()

    override fun consumeErrorCollector(documentUri: String, documentVersion: Int, ec: ErrorCollector) {
        var diagnostics = ArrayList<Diagnostic>()
        val warningMessages = ec.warnings
        val errorMessages = ec.errors
        if (warningMessages != null) {
            val warningDiagnostics = generateDiagnostics(warningMessages)
            System.err.println("${warningDiagnostics.size} Warnings")
            diagnostics.addAll(warningDiagnostics)
        }
        if (errorMessages != null) {
            val errorDiagnostics = generateDiagnostics(errorMessages)
            System.err.println("${errorDiagnostics.size} Errors")
            diagnostics.addAll(errorDiagnostics)
        }
        val diagnosticsNotification = PublishDiagnosticsParams(documentUri, diagnostics, documentVersion)
        notifyDiagnostics(diagnosticsNotification)
    }

    private fun parseSyntaxErrorToDiagnostic(error: SyntaxErrorMessage): Diagnostic {
            val cause = error.getCause()
            val range = Range(Position(cause.startLine-1, cause.startColumn-1),
            Position(cause.endLine-1, cause.endColumn-1))
            return Diagnostic(range, cause.message, DiagnosticSeverity.Error, "Compiler")
    }

    private fun parseWarningToDiagnostic(warning: WarningMessage): Diagnostic {
            val context = warning.context
            val range = Range(Position(context.startLine-1, context.startColumn-1),
            Position(context.root.size(), context.startColumn-1))
            return Diagnostic(range, warning.message, DiagnosticSeverity.Warning, "Compiler")
    }

    private fun <T> generateDiagnostics(errors: List<T>): List<Diagnostic> { 
        var diagnostics = ArrayList<Diagnostic>()
        errors.forEach({ error ->
            when (error) {
                is SyntaxErrorMessage -> diagnostics.add(parseSyntaxErrorToDiagnostic(error))
                is WarningMessage -> diagnostics.add(parseWarningToDiagnostic(error))
            }
            })
        return diagnostics
    }

    override fun notifyDiagnostics(diagnostics: PublishDiagnosticsParams) {
        val client = client
        if (client == null) {
            System.err.println("Can't notify diagnostics. Client hasn't been initialized")
            return
        }
        client.publishDiagnostics(diagnostics)
    }

}
