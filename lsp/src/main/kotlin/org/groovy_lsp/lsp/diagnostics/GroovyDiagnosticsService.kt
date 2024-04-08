package org.groovy_lsp.lsp.diagnostics

import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.DiagnosticSeverity
import org.groovy_lsp.lsp.state.Document
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.control.messages.WarningMessage;
import org.codehaus.groovy.control.messages.Message;
import org.apache.groovy.parser.antlr4.GroovyLangParser
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.Phases
import org.codehaus.groovy.control.CompilationFailedException
import com.groovy_lsp.lsp.analysis.GroovyParser
import java.util.stream.Stream

class GroovyDiagnosticsService(): DiagnosticsService {

    override var client: LanguageClient? = null

    override fun analyzeDocument(document: Document) {
        val compilerConfiguration = CompilerConfiguration()
        compilerConfiguration.warningLevel = WarningMessage.LIKELY_ERRORS
        val compilationUnit = CompilationUnit(compilerConfiguration)
        compilationUnit.addSource(document.uri, document.text)
        var diagnostics = ArrayList<Diagnostic>()
        try {
            compilationUnit.compile(Phases.CLASS_GENERATION)
            System.err.println("Compilation succeeded")
        } catch (e: CompilationFailedException) {
            // Just catch the exception
        }
        val ec = compilationUnit.errorCollector
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
        val diagnosticsNotification = PublishDiagnosticsParams(document.uri, diagnostics, document.version)
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
