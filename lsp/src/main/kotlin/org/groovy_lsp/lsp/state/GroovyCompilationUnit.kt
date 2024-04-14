package org.groovy_lsp.lsp.state


import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.SourceUnit
import java.util.Collections
import java.util.LinkedList

/**
* This class represents aims to have a more iterative way to compile the code
* without having to add all sources for every compilation.
* It exposes some protected properties through a specific API.
* The idea is to replace the source field of the super-class CompilationUnit
* enabling to swap out stale SourceUnits.
*/
class GroovyCompilationUnit: CompilationUnit {

    constructor(): super()

    constructor(seed: GroovyCompilationUnit): super(seed.configuration) {
        setQueuedSources(seed.sources)
    }

    constructor(configuration: CompilerConfiguration): super(configuration)

    /**
    * Allows to set the sources queued for the compilation
    * This is done so that the previous logic can still be leveraged
    * The queued sources get flushed when the compile() method gets called.
    * So we can set what we queue for the next compilation
    * This method should be used before compilation
    */
    fun setQueuedSources(sourceUnits: Map<String, SourceUnit>) {
        this.queuedSources = LinkedList(sourceUnits.values)
    }

    /**
    * Current implementation of CompilationUnit won't allow for an existing
    * source to get added to the queue. This method allows to put the sources
    * To be replaced at the end of the queue.
    * Since the queue then gets flushed into a Map, the last source unit for a
    * given key will be the one to be used for the compilation.
    */
    fun replaceSource(sourceUnit: SourceUnit) {
        this.queuedSources.add(sourceUnit)
    }

}
