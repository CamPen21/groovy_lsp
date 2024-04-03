package org.groovy_lsp.lsp.state

import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.Position
import org.groovy_lsp.lsp.GroovyDocument

class HashMapBasedStateHandler: StateHandler {

    private val stateHashMap: HashMap<String, Document> = HashMap()

    override fun getDocument(uri: String): Document {
        val document = stateHashMap.get(uri)
        if (document == null) {
            throw NoSuchElementException("Document $uri not found")
        }
        return document
    }

    override fun getDocument(uri: String, version: Int): Document? {
        return getDocument(uri)
    }

    override fun addDocument(uri: String, text: String, version: Int) {
        val document = GroovyDocument(uri, text, version)
        stateHashMap.put(uri, document)
    }

    override fun updateDocument(uri: String, text: String, version: Int) {
        getDocument(uri).update(text)
    }

    override fun updateDocument(uri: String, text: String, version: Int, range: Range) {
        // TODO: Remove this duplicate code when using non-range signature
        getDocument(uri).update(text, range)
    }

}
