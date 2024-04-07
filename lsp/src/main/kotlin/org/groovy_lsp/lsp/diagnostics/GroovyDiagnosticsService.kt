package org.groovy_lsp.lsp.diagnostics

import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.groovy_lsp.lsp.state.Document
import org.groovy_lsp.lsp.Parse


class GroovyDiagnosticsService(): DiagnosticsService {

    override var client: LanguageClient? = null
    val parser = Parse()


    override fun analyzeDocument(document: Document) {
        val diagnostics = parser.parseWithErrorPosition(document.text)
        val diagnosticsNotification = PublishDiagnosticsParams(document.uri, diagnostics, document.version)
        if (diagnostics.isEmpty()) {
            return notifyDiagnostics(diagnosticsNotification)
        }
        notifyDiagnostics(diagnosticsNotification)
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
