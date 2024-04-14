package org.groovy_lsp.lsp.state

import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.Position
import org.groovy_lsp.lsp.GroovyDocument
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.ErrorCollector
import org.codehaus.groovy.control.Phases
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.ast.CompileUnit
import groovy.lang.GroovyClassLoader
import org.groovy_lsp.lsp.state.GroovyCompilationUnit

class HashMapBasedStateHandler: StateHandler {

    private val stateHashMap: HashMap<String, Document> = HashMap()
    private var classLoader: GroovyClassLoader = GroovyClassLoader()
    var errorCollector: ErrorCollector = ErrorCollector(null)
    var seed = GroovyCompilationUnit()

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

    override fun addDocument(uri: String, text: String, version: Int): Document {
        val document = GroovyDocument(uri, text, version)
        stateHashMap.put(uri, document)
        compileDocument(document)
        return document
    }

    override fun updateDocument(uri: String, text: String, version: Int): Document {
        val document = getDocument(uri)
        document.update(text)
        compileDocument(document)
        return document
    }

    override fun updateDocument(uri: String, text: String, version: Int, range: Range): Document {
        // TODO: Remove this duplicate code when using non-range signature
        val document = getDocument(uri)
        document.update(text, range)
        compileDocument(document)
        return document
    }

    override fun getClassLoader(): GroovyClassLoader {
        return classLoader
    }

    private fun compileDocument(document: Document) {
        var cu = GroovyCompilationUnit(seed)
        try {
            val su = document.asSourceUnit(cu.configuration, cu.classLoader, cu.errorCollector)
            cu.replaceSource(su)
            cu.compile(Phases.CLASS_GENERATION)
            seed = cu
            classLoader = cu.classLoader
        } catch (e: CompilationFailedException) {
            // Nothing needs to be done here for now
            // TODO? Maybe emit a metric
        }
        errorCollector = cu.errorCollector
    }

}
