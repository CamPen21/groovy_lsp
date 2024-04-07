package org.groovy_lsp.textdocument

import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler
import org.eclipse.lsp4j.jsonrpc.messages.NotificationMessage
import org.eclipse.lsp4j.jsonrpc.messages.Message
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.groovy_lsp.lsp.Parse
import org.groovy_lsp.lsp.diagnostics.DiagnosticsService
import org.groovy_lsp.lsp.state.HashMapBasedStateHandler


class GroovyTextDocumentService(val diagnosticsService: DiagnosticsService): TextDocumentService {

    val documentsHandler = HashMapBasedStateHandler()
    val parser = Parse()

    override fun didOpen(params: DidOpenTextDocumentParams) {
        try {
            val uri = params.textDocument.uri
            val version = params.textDocument.version
            documentsHandler.addDocument(
                uri, 
                params.textDocument.text, 
                params.textDocument.version
            )
            val document = documentsHandler.getDocument(uri, version)
            if (document == null) {
                return
            }
            diagnosticsService.analyzeDocument(document)
        } catch (e: Exception) {
            System.err.println(e.toString())
        }
    }

    override fun didChange(params: DidChangeTextDocumentParams) {
        try {
            val version = params.textDocument.version
            val uri = params.textDocument.uri
            if (uri == null) {
                return
            }
            params.contentChanges.forEach { change -> 
                when (change.range) {
                    null -> {
                        documentsHandler.updateDocument(uri, change.text, version)
                    }
                    else -> {
                        documentsHandler
                        .updateDocument(uri, change.text, version, change.range)
                    }
                }
                
            }
            val document = documentsHandler.getDocument(uri, version)
            if (document == null) {
                return
            }
            diagnosticsService.analyzeDocument(document)
        } catch (e: Exception) {
            System.err.println(e.toString())
        }
    }

    override fun didClose(params: DidCloseTextDocumentParams) {

    }

    override fun didSave(params: DidSaveTextDocumentParams) {

    }
}
