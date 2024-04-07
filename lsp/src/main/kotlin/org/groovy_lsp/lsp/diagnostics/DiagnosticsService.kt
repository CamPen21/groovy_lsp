package org.groovy_lsp.lsp.diagnostics

import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.groovy_lsp.lsp.state.Document


/* Background service sending notifications to the client
*/
interface DiagnosticsService {

    var client: LanguageClient? 

    fun analyzeDocument(document: Document)

    fun notifyDiagnostics(diagnostics: PublishDiagnosticsParams)
}
