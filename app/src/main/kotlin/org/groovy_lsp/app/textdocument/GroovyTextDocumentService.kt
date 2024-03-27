package org.groovy_lsp.textdocument

import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams


class GroovyTextDocumentService: TextDocumentService {
    
    override fun didOpen(params: DidOpenTextDocumentParams) {

    }

    override fun didChange(params: DidChangeTextDocumentParams) {

    }

    override fun didClose(params: DidCloseTextDocumentParams) {

    }

    override fun didSave(params: DidSaveTextDocumentParams) {

    }
}
