package org.groovy_lsp.lsp.state

import org.eclipse.lsp4j.Range
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.ErrorCollector
import groovy.lang.GroovyClassLoader


interface Document {

    var numberOfLines: Int
    var totalRange: Range
    var text: String
    val uri: String
    var version: Int

    fun update(newText: String)

    fun update(newText: String, range: Range)

    fun transformPositionToLinear(line: Int, character: Int): Int 

    fun asSourceUnit(configuration: CompilerConfiguration, loader: GroovyClassLoader, er: ErrorCollector): SourceUnit
}
