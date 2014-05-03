/*
 * Copyright 2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import net.nicoulaj.compilecommand.ITAssert

try {
    helper = new ITAssert(basedir, localRepositoryPath, context)

    // Check hotspot_compiler file
    helper.assertFileIsNotEmpty("target/classes/META-INF/hotspot_compiler")

    // Check startup traces
    helper.assertBuildLogContains("CompilerOracle: dontinline net/nicoulaj/compilecommand/IT02.<init> ()V")
    helper.assertBuildLogContains("CompilerOracle: dontinline net/nicoulaj/compilecommand/IT02.method01 ()V")
    helper.assertBuildLogContains("CompilerOracle: dontinline net/nicoulaj/compilecommand/IT02.method02 (Z)V")
    helper.assertBuildLogContains("CompilerOracle: dontinline net/nicoulaj/compilecommand/IT02.method03 ()V")
    helper.assertBuildLogContains("CompilerOracle: dontinline net/nicoulaj/compilecommand/IT02.method04 (Ljava/util/List;)V")
    helper.assertBuildLogContains("CompilerOracle: dontinline net/nicoulaj/compilecommand/IT02.method05 (Ljava/util/List;)D")
    helper.assertBuildLogContains("CompilerOracle: dontinline net/nicoulaj/compilecommand/IT02.method06 ([Ljava/io/Serializable;)V")
    helper.assertBuildLogContains("CompilerOracle: dontinline net/nicoulaj/compilecommand/IT02.method07 (Ljava/util/List;)V")
    helper.assertBuildLogContains("CompilerOracle: dontinline net/nicoulaj/compilecommand/IT02.method08 (Ljava/io/Serializable;)Ljava/io/Serializable;")
    helper.assertBuildLogContains("CompilerOracle: dontinline net/nicoulaj/compilecommand/IT02.method09 (ZSIJFDCB)V")
    helper.assertBuildLogContains("CompilerOracle: dontinline net/nicoulaj/compilecommand/IT02.method10 (Ljava/lang/Boolean;Ljava/lang/Short;Ljava/lang/Integer;Ljava/lang/Long;Ljava/lang/Float;Ljava/lang/Double;Ljava/lang/Character;Ljava/lang/Byte;)V")

    // Check PrintInlining traces
    if (!helper.isJava6()) { // PrintInlining does not seem to produce any output on Java 6 production VM
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
    }

} catch (Exception e) {
    System.err.println(e.getMessage())
    return false;
}
