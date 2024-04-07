package org.groovy_lsp.lsp.state

import org.eclipse.lsp4j.Range


interface Document {

    var numberOfLines: Int
    var totalRange: Range
    var text: String
    val uri: String
    var version: Int

    fun update(newText: String)

    fun update(newText: String, range: Range)
}
