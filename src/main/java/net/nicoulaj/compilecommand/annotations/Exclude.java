/*
 * Hotspot compile command annotations - http://compile-command-annotations.nicoulaj.net
 * Copyright © 2014-2019 Hotspot compile command annotations contributors
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
package net.nicoulaj.compilecommand.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Exclude the specified method from just in time compilation.
 *
 * @author <a href="http://github.com/nicoulaj">Julien Nicoulaud</a>
 * @see <a href="http://nicoulaj.github.com/compile-command-annotations">Hotspot compiler hints generator documentation</a>
 * @see <a href="http://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html"><code>CompileCommand</code>/<code>CompileCommandFile</code> documentation</a>
 * @see net.nicoulaj.compilecommand.annotations.DontInline
 * @see net.nicoulaj.compilecommand.annotations.Inline
 */
@Documented
@Retention(SOURCE)
@Target({CONSTRUCTOR, METHOD})
public @interface Exclude {

}
