/*
 * ====================================================================
 * Hotspot compile command annotations
 * ====================================================================
 * Copyright (C) 2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * ====================================================================
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ====================================================================
 */
import net.nicoulaj.compilecommand.ITAssert

try {
    helper = new ITAssert(basedir, localRepositoryPath, context)

    // Check hotspot_compiler file
    helper.assertFileIsNotEmpty("target/classes/META-INF/hotspot_compiler")

    // Check startup traces
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
