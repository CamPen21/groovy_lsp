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
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.HoverParams
import org.eclipse.lsp4j.Hover
import org.eclipse.lsp4j.MarkupContent
import org.eclipse.lsp4j.MarkupKind
import org.groovy_lsp.lsp.diagnostics.DiagnosticsService
import org.groovy_lsp.lsp.state.HashMapBasedStateHandler
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilationFailedException
import java.util.concurrent.CompletableFuture


class GroovyTextDocumentService(val diagnosticsService: DiagnosticsService): TextDocumentService {

    val documentsHandler = HashMapBasedStateHandler()

    override fun didOpen(params: DidOpenTextDocumentParams) {
        try {
            val uri = params.textDocument.uri
            val version = params.textDocument.version
            val document = documentsHandler.addDocument(
                uri, 
                params.textDocument.text, 
                params.textDocument.version
            )
            diagnosticsService.consumeErrorCollector(document.uri, version, documentsHandler.errorCollector)
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
            diagnosticsService.consumeErrorCollector(uri, version, documentsHandler.errorCollector)
        } catch (e: Exception) {
            System.err.println(e.toString())
        }
    }

    override fun hover(params: HoverParams): CompletableFuture<Hover> {
        System.err.println("Executing in thread:" + Thread.currentThread().getId())
        val uri = params.textDocument.uri
        val position = params.position
        val document = documentsHandler.getDocument(uri)
        val compilationUnit = CompilationUnit()
        compilationUnit.addSource(document.uri, document.text)
        try {
            compilationUnit.compile()
        } catch(e: CompilationFailedException) {
            // Ignoring this for now
        }
        val fClass = compilationUnit.getFirstClassNode()
        val module = fClass.getModule()
        val imports = module.getImports()
        val lastImport = imports.get(imports.size-1)
        val classDeclLine = fClass.getLineNumber()
        System.err.println(classDeclLine)
        val endOfImports = lastImport.getLastLineNumber()
        val docstringStart = document.transformPositionToLinear(endOfImports, 0)
        val docstringEnd = document.transformPositionToLinear(classDeclLine-1, 0)
        val docstring = document.text.substring(docstringStart, docstringEnd).trim()
        val response = Hover(MarkupContent(MarkupKind.PLAINTEXT, docstring))
        val c = CompletableFuture<Hover>()
        c.complete(response)
        return c
    }

    override fun didClose(params: DidCloseTextDocumentParams) {

    }

    override fun didSave(params: DidSaveTextDocumentParams) {

    }
}
