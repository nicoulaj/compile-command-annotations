/**
 * Hotspot compile command annotations - http://compile-command-annotations.nicoulaj.net
 * Copyright © 2014-2018 Hotspot compile command annotations contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import net.nicoulaj.compilecommand.ITAssert

try {
    helper = new ITAssert(basedir, localRepositoryPath, context)

    // Check hotspot_compiler file
    helper.assertFileIsNotEmpty("target/classes/META-INF/hotspot_compiler")

    // Check startup traces
    helper.assertBuildLogDoesNotContain("CompilerOracle: unrecognized line")
    helper.assertBuildLogDoesNotContain("CompilerOracle: exclude net/nicoulaj/compilecommand/IT03.<init> ()V")

    // Check PrintInlining/LogCompilation traces
    if (!helper.isJava6()) { // PrintInlining does not seem to produce any output on Java 6 production VM
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT03::<init> (5 bytes)")
        helper.assertBuildLogContains("Compiled method")
    }

} catch (Exception e) {
    System.err.println(e.getMessage())
    return false;
}
