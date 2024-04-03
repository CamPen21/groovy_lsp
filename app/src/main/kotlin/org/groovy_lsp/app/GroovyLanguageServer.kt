package org.groovy_lsp.app

import org.groovy_lsp.lsp.Parse
import org.groovy_lsp.lsp.diagnostics.GroovyDiagnosticsService
import org.groovy_lsp.textdocument.GroovyTextDocumentService
import org.groovy_lsp.workspace.GroovyWorkspaceService
import java.util.concurrent.CompletableFuture
import java.util.Scanner
import java.io.InputStream
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.TextDocumentSyncKind.Incremental
import org.eclipse.lsp4j.DiagnosticRegistrationOptions;

class GroovyLanguageServer: LanguageServer, LanguageClientAware {

    private var lspClient: LanguageClient? = null
    private val textDocumentService = GroovyTextDocumentService()
    private val workspaceService = GroovyWorkspaceService()
    private val diagnosticsService = GroovyDiagnosticsService()

    override fun initialize(initializeParams: InitializeParams): CompletableFuture<InitializeResult> {
        val clientName = initializeParams.clientInfo.name;
        val clientVersion = initializeParams.clientInfo.version;
        System.err.println("Client attempting to connect $clientName $clientVersion")
        val serverCapabilities = ServerCapabilities()
        serverCapabilities.setTextDocumentSync(Incremental)
        // For now supporting inter file diagnostics, but no workspace diagnostics
        serverCapabilities.diagnosticProvider = DiagnosticRegistrationOptions(true, false)
        val result = InitializeResult(serverCapabilities)
        val promise = CompletableFuture<InitializeResult>()
        promise.complete(result)
        return promise
    }

    override fun initialized() {
        System.err.println("We are online!")
    }

    override fun shutdown(): CompletableFuture<Any>{
        return CompletableFuture()
    }

    override fun exit() {
        return
    }

    override fun getTextDocumentService(): TextDocumentService? {
        return textDocumentService
    }

    override fun getWorkspaceService(): WorkspaceService {
        return workspaceService
    }
    override fun connect(client: LanguageClient): Unit {
        diagnosticsService.client = client
        lspClient = client
        textDocumentService.client = client
    }

}

fun main() {
    val ls = GroovyLanguageServer()
    val launcher = LSPLauncher.createServerLauncher(ls, System.`in`, System.out)
    val client: LanguageClient = launcher.getRemoteProxy();
    ls.connect(client)
    launcher.startListening().get()
}
