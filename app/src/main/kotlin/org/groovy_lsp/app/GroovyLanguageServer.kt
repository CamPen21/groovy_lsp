package org.groovy_lsp.app

import org.groovy_lsp.lsp.Parse
import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult

class GroovyLanguageServer: LanguageServer {

    override fun initialize(initializeParams: InitializeParams!): CompletableFuture<InitializeResult> {
        return CompletableFuture()
    }

    override fun shutdown(): CompletableFuture<> {
        return CompletableFuture()
    }

    override fun exit() {
        return
    }

    override fun getTextDocumentService(): TextDocumentService {
        return 
    }

    override fun getWorkspaceService(): WorkspaceService {
        return 
    }

}

fun main() {
    println(Parse().parseWithErrorPosition("..."))
}
