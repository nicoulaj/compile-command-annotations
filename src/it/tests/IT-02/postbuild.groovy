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
    helper.assertBuildLogContains(": dontinline net/nicoulaj/compilecommand/IT02.<init>")
    helper.assertBuildLogContains(": dontinline net/nicoulaj/compilecommand/IT02.method01")
    helper.assertBuildLogContains(": dontinline net/nicoulaj/compilecommand/IT02.method02")
    helper.assertBuildLogContains(": dontinline net/nicoulaj/compilecommand/IT02.method03")
    helper.assertBuildLogContains(": dontinline net/nicoulaj/compilecommand/IT02.method04")
    helper.assertBuildLogContains(": dontinline net/nicoulaj/compilecommand/IT02.method05")
    helper.assertBuildLogContains(": dontinline net/nicoulaj/compilecommand/IT02.method06")
    helper.assertBuildLogContains(": dontinline net/nicoulaj/compilecommand/IT02.method07")
    helper.assertBuildLogContains(": dontinline net/nicoulaj/compilecommand/IT02.method08")
    helper.assertBuildLogContains(": dontinline net/nicoulaj/compilecommand/IT02.method09")
    helper.assertBuildLogContains(": dontinline net/nicoulaj/compilecommand/IT02.method10")

    // Check PrintInlining traces
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT02::<init> (5 bytes)   disallowed by Compile")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT02::method01 (1 bytes)   disallowed by Compile")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT02::method02 (1 bytes)   disallowed by Compile")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT02::method03 (1 bytes)   disallowed by Compile")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT02::method04 (1 bytes)   disallowed by Compile")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT02::method05 (2 bytes)   disallowed by Compile")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT02::method06 (1 bytes)   disallowed by Compile")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT02::method07 (1 bytes)   disallowed by Compile")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT02::method08 (2 bytes)   disallowed by Compile")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT02::method09 (1 bytes)   disallowed by Compile")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT02::method10 (1 bytes)   disallowed by Compile")

} catch (Exception e) {
    System.err.println(e.getMessage())
    return false;
}
