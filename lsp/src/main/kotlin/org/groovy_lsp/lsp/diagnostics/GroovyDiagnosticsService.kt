package org.groovy_lsp.lsp.diagnostics

import org.eclipse.lsp4j.services.LanguageClient


class GroovyDiagnosticsService(): DiagnosticsService {

    override var client: LanguageClient? = null

}
