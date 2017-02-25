/**
 * Hotspot compile command annotations - http://compile-command-annotations.nicoulaj.net
 * Copyright © 2014-2017 Hotspot compile command annotations contributors
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
    helper.assertBuildLogContains("CompilerOracle: inline net/nicoulaj/compilecommand/IT01.<init> ()V")
    helper.assertBuildLogContains("CompilerOracle: inline net/nicoulaj/compilecommand/IT01.method01 ()V")
    helper.assertBuildLogContains("CompilerOracle: inline net/nicoulaj/compilecommand/IT01.method02 (Z)V")
    helper.assertBuildLogContains("CompilerOracle: inline net/nicoulaj/compilecommand/IT01.method03 ()V")
    helper.assertBuildLogContains("CompilerOracle: inline net/nicoulaj/compilecommand/IT01.method04 (Ljava/util/List;)V")
    helper.assertBuildLogContains("CompilerOracle: inline net/nicoulaj/compilecommand/IT01.method05 (Ljava/util/List;)D")
    helper.assertBuildLogContains("CompilerOracle: inline net/nicoulaj/compilecommand/IT01.method06 ([Ljava/io/Serializable;)V")
    helper.assertBuildLogContains("CompilerOracle: inline net/nicoulaj/compilecommand/IT01.method07 (Ljava/util/List;)V")
    helper.assertBuildLogContains("CompilerOracle: inline net/nicoulaj/compilecommand/IT01.method08 (Ljava/io/Serializable;)Ljava/io/Serializable;")
    helper.assertBuildLogContains("CompilerOracle: inline net/nicoulaj/compilecommand/IT01.method09 (ZSIJFDCB)V")
    helper.assertBuildLogContains("CompilerOracle: inline net/nicoulaj/compilecommand/IT01.method10 (Ljava/lang/Boolean;Ljava/lang/Short;Ljava/lang/Integer;Ljava/lang/Long;Ljava/lang/Float;Ljava/lang/Double;Ljava/lang/Character;Ljava/lang/Byte;)V")

    // Check PrintInlining traces
    if (!helper.isJava6()) { // PrintInlining does not seem to produce any output on Java 6 production VM
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::<init> (5 bytes)   force inline by Compile")
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method01 (1 bytes)   force inline by Compile")
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method02 (1 bytes)   force inline by Compile")
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method03 (1 bytes)   force inline by Compile")
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method04 (1 bytes)   force inline by Compile")
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method05 (2 bytes)   force inline by Compile")
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method06 (1 bytes)   force inline by Compile")
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method07 (1 bytes)   force inline by Compile")
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method08 (2 bytes)   force inline by Compile")
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method09 (1 bytes)   force inline by Compile")
        helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method10 (1 bytes)   force inline by Compile")
    }

} catch (Exception e) {
    System.err.println(e.getMessage())
    return false;
}
