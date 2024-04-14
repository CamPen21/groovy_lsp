package org.groovy_lsp.lsp.diagnostics

import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.groovy_lsp.lsp.state.Document
import org.codehaus.groovy.control.ErrorCollector


/* Background service sending notifications to the client
*/
interface DiagnosticsService {

    var client: LanguageClient? 

    fun consumeErrorCollector(documentUri: String, documentVersion: Int, ec: ErrorCollector)

    fun notifyDiagnostics(diagnostics: PublishDiagnosticsParams)
}
