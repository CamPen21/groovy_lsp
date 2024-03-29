package org.groovy_lsp.textdocument

import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.groovy_lsp.lsp.DocumentsHandler


class GroovyTextDocumentService: TextDocumentService {
    
    val documentsHandler = DocumentsHandler()

    override fun didOpen(params: DidOpenTextDocumentParams) {
        try {
            documentsHandler.addDocument(
                params.textDocument.uri, 
                params.textDocument.text, 
                params.textDocument.version
            )
        } catch (e: Exception) {
            System.err.println(e.toString())
        }
    }

    override fun didChange(params: DidChangeTextDocumentParams) {
        try {
            val version = params.textDocument.version
            val uri = params.textDocument.uri
            params.contentChanges.forEach { change -> 
                when (change.range) {
                    null -> {
                        documentsHandler.updateDocument(uri, change.text, version)
                    }
                    else -> {
                        System.err.println(change.range.toString())
                        System.err.println(change.text)
                        documentsHandler
                        .updateDocument(uri, change.range, change.text, version)
                    }
                }
                
            }
        } catch (e: Exception) {
            System.err.println(e.toString())
        }
    }

    override fun didClose(params: DidCloseTextDocumentParams) {

    }

    override fun didSave(params: DidSaveTextDocumentParams) {

    }
}
