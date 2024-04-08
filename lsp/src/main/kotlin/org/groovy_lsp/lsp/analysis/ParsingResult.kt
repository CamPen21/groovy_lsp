package com.groovy_lsp.lsp.analysis

import groovy.lang.Script
import org.codehaus.groovy.control.CompilationFailedException


data class ParsingResult(var script: Script?, var error: CompilationFailedException?) {

}
