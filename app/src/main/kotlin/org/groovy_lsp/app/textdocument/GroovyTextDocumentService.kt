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
import org.groovy_lsp.lsp.state.HashMapBasedStateHandler


class GroovyTextDocumentService: TextDocumentService {

    var client: LanguageClient? = null

    val documentsHandler = HashMapBasedStateHandler()
    val parser = Parse()

    override fun didOpen(params: DidOpenTextDocumentParams) {
        try {
            documentsHandler.addDocument(
                params.textDocument.uri, 
                params.textDocument.text, 
                params.textDocument.version
            )
            val diagnostics = parser.parseWithErrorPosition(params.textDocument.text)
            System.err.println("${diagnostics.size} Errors found")
            if (diagnostics.isEmpty()) {
                return
            }
            val notificationParams = PublishDiagnosticsParams(params.textDocument.uri, diagnostics, params.textDocument.version)
            client?.publishDiagnostics(notificationParams)
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
            val text = documentsHandler.getDocument(uri).text
            val diagnostics = parser.parseWithErrorPosition(text)
            System.err.println("${diagnostics.size} Errors found")
            if (diagnostics.isEmpty()) {
                return
            }
            val notificationParams = PublishDiagnosticsParams(uri, diagnostics, version)
            client?.publishDiagnostics(notificationParams)
        } catch (e: Exception) {
            System.err.println(e.toString())
        }
    }

    override fun didClose(params: DidCloseTextDocumentParams) {

    }

    override fun didSave(params: DidSaveTextDocumentParams) {

    }
}
