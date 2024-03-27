package org.groovy_lsp.app

import org.groovy_lsp.lsp.Parse
import org.groovy_lsp.textdocument.GroovyTextDocumentService
import org.groovy_lsp.workspace.GroovyWorkspaceService
import java.util.concurrent.CompletableFuture;
import java.util.Scanner
import java.io.InputStream
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.jsonrpc.Launcher

class GroovyLanguageServer: LanguageServer, LanguageClientAware {

    override fun initialize(initializeParams: InitializeParams): CompletableFuture<InitializeResult> {
        return CompletableFuture()
    }

    override fun shutdown(): CompletableFuture<Any>{
        return CompletableFuture()
    }

    override fun exit() {
        return
    }

    override fun getTextDocumentService(): TextDocumentService {
        return GroovyTextDocumentService()
    }

    override fun getWorkspaceService(): WorkspaceService {
        return GroovyWorkspaceService()
    }
    override fun connect(client: LanguageClient): Unit {

    }

}

fun main() {
    val ls = GroovyLanguageServer()
    val launcher = LSPLauncher.createServerLauncher(ls, System.`in`, System.out)
    val client: LanguageClient = launcher.getRemoteProxy();
    ls.connect(client)
    launcher.startListening().get()
}
