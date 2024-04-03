package org.groovy_lsp.lsp.diagnostics

import org.eclipse.lsp4j.services.LanguageClient


/* Background service sending notifications to the client
*/
interface DiagnosticsService {

    var client: LanguageClient? 

}
