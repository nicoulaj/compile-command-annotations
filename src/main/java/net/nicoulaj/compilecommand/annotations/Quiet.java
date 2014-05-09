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
package net.nicoulaj.compilecommand.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Do not print the compile commands on application startup.
 *
 * @author <a href="http://github.com/nicoulaj">Julien Nicoulaud</a>
 * @see <a href="http://nicoulaj.github.com/compile-command-annotations">Hotspot compiler hints generator documentation</a>
 * @see <a href="http://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html"><code>CompileCommand</code>/<code>CompileCommandFile</code> documentation</a>
 */
@Documented
@Retention(SOURCE)
@Target({PACKAGE, TYPE, CONSTRUCTOR, METHOD})
public @interface Quiet {

}
