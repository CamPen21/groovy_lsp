package org.groovy_lsp.lsp.state

import org.eclipse.lsp4j.Range
import org.groovy_lsp.lsp.Document

/* StateHandler is an object responsible for handling the state of the server.
*/
interface StateHandler {

    // Get a documents state for latest version
    fun getDocument(uri: String): Document

    // Get a documents state for a version
    fun getDocument(uri: String, version: Int): Document

    // Handle adding a document 
    fun addDocument(uri: String, text: String, version: Int)

    // Handle updating a document upon a full text update
    fun updateDocument(uri: String, text: String, version: Int)

    // Handle updating a document upon a range text update
    fun updateDocument(uri: String, text: String, version: Int, range: Range)
}
