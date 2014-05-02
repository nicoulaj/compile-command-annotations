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
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::<init> (5 bytes)   force inline by CompileOracle")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method01 (1 bytes)   force inline by CompileOracle")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method02 (1 bytes)   force inline by CompileOracle")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method03 (1 bytes)   force inline by CompileOracle")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method04 (1 bytes)   force inline by CompileOracle")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method05 (2 bytes)   force inline by CompileOracle")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method06 (1 bytes)   force inline by CompileOracle")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method07 (1 bytes)   force inline by CompileOracle")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method08 (2 bytes)   force inline by CompileOracle")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method09 (1 bytes)   force inline by CompileOracle")
    helper.assertBuildLogContains("net.nicoulaj.compilecommand.IT01::method10 (1 bytes)   force inline by CompileOracle")

} catch (Exception e) {
    System.err.println(e.getMessage())
    return false;
}
