package org.groovy_lsp.lsp;

import org.eclipse.lsp4j.Range
import java.io.File
import kotlin.text.StringBuilder


class DocumentsHandler {

    var documentDirectory: HashMap<String, Document> = HashMap()

    fun addDocument(uri: String, text: String, version: Int) {
        val document = Document(uri, text, version)
        documentDirectory.put(uri, document)
    }

    fun updateDocument(uri: String, text: String, version: Int) {
        val document = documentDirectory.get(uri)
        if (document == null) {
            System.err.println("Can't update a document that's not open")
            return
        }
        document.text = text
        document.version = version
    }

    fun updateDocument(uri: String, range: Range, text: String, version: Int) {
        val document = documentDirectory.get(uri)
        if (document == null) {
            System.err.println("Can't update a document that's not open")
            return
        }
        val savedLength = document.text.length - 1
        var originalText = StringBuilder(document.text)
        val startIndex = document.transformPositionToLinear(range.start.line, range.start.character)
        val endIndex = document.transformPositionToLinear(range.end.line, range.end.character)
        if (endIndex <= savedLength) {
            // Text modification is within the existing length
            document.text = originalText.replace(startIndex, endIndex, text).toString()
        } else if (startIndex > savedLength){
            document.text = originalText.append(text).toString()
        } else {
            // Text modification overflows the existing length
            val unchangedText = originalText.substring(0, Math.min(startIndex, savedLength))
            document.text = StringBuilder(unchangedText).append(text).toString()
        }
        document.version = version
    }
}
