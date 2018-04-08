/*
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
package net.nicoulaj.compilecommand;

import net.nicoulaj.compilecommand.annotations.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static javax.tools.Diagnostic.Kind.*;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link Processor} that generates a <code>hotspot_compiler</code> file for using with <code>-XX:CompileCommandFile</code>.
 *
 * @author <a href="http://github.com/nicoulaj">Julien Nicoulaud</a>
 * @see <a href="http://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html"><code>CompileCommand</code>/<code>CompileCommandFile</code> documentation</a>
 */
@SuppressWarnings("unused")
public final class CompileCommandProcessor extends AbstractProcessor {

    public static final String COMPILE_COMMAND_FILE_CHARSET_OPTION = "compile.command.file.output.charset";

    public static final String COMPILE_COMMAND_FILE_PATH_OPTION = "compile.command.file.output.path";

    public static final String COMPILE_COMMAND_FILE_CHARSET_DEFAULT = "UTF-8";

    public static final String COMPILE_COMMAND_FILE_PATH_DEFAULT = "META-INF/hotspot_compiler";

    private final SortedSet<String> lines;

    private boolean quiet = false;

    public CompileCommandProcessor() {
        lines = new TreeSet<String>();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(asList(
                Break.class.getName(),
                CompileOnly.class.getName(),
                DontInline.class.getName(),
                Exclude.class.getName(),
                Inline.class.getName(),
                Log.class.getName(),
                Option.class.getName(),
                Options.class.getName(),
                Print.class.getName(),
                Quiet.class.getName()
        ));
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

        message(NOTE, "Processing compiler hints annotations");

        processBreak(roundEnv);
        processCompileOnly(roundEnv);
        processDontInline(roundEnv);
        processExclude(roundEnv);
        processInline(roundEnv);
        processLog(roundEnv);
        processOptions(roundEnv);
        processOption(roundEnv);
        processPrint(roundEnv);
        processQuiet(roundEnv);

        if (!roundEnv.processingOver())
            return true;

        final String outputPath = processingEnv.getOptions().get(COMPILE_COMMAND_FILE_PATH_OPTION);
        final String charset = processingEnv.getOptions().get(COMPILE_COMMAND_FILE_CHARSET_OPTION);
        generateCompileCommandFile(
            outputPath != null ? outputPath : COMPILE_COMMAND_FILE_PATH_DEFAULT,
            charset != null ? charset : COMPILE_COMMAND_FILE_CHARSET_DEFAULT
        );

        message(NOTE, "Done processing compiler hints annotations");

        return true;
    }

    private void processBreak(RoundEnvironment roundEnv) {
        processSimpleMethodAnnotation(Break.class, roundEnv);
    }

    private void processCompileOnly(RoundEnvironment roundEnv) {
        processSimpleMethodAnnotation(CompileOnly.class, roundEnv);
    }

    private void processDontInline(RoundEnvironment roundEnv) {
        processSimpleMethodAnnotation(DontInline.class, roundEnv);
    }

    private void processExclude(RoundEnvironment roundEnv) {
        processSimpleMethodAnnotation(Exclude.class, roundEnv);
    }

    private void processInline(RoundEnvironment roundEnv) {
        processSimpleMethodAnnotation(Inline.class, roundEnv);
    }

    private void processLog(RoundEnvironment roundEnv) {
        processSimpleMethodAnnotation(Log.class, roundEnv);
    }

    private void processOptions(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Options.class))
            for (String option : element.getAnnotation(Options.class).value())
                processOption(element, option, roundEnv);
    }

    private void processOption(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Option.class))
            processOption(element, element.getAnnotation(Option.class).value(), roundEnv);
    }

    private void processOption(final Element element, String option, RoundEnvironment roundEnv) {
        lines.add(element.accept(new SimpleElementVisitor6<String, String>() {

            @Override
            public String visitExecutable(final ExecutableElement e, final String option) {
                return Option.class.getSimpleName().toLowerCase() + " " + getDescriptor(e) + " " + option;
            }

        }, option));
    }

    private void processPrint(RoundEnvironment roundEnv) {
        processSimpleMethodAnnotation(Print.class, roundEnv);
    }

    private void processQuiet(RoundEnvironment roundEnv) {
        if (!roundEnv.getElementsAnnotatedWith(Quiet.class).isEmpty())
            quiet = true;
    }

    private void processSimpleMethodAnnotation(final Class<? extends Annotation> clazz, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(clazz))
            lines.add(element.accept(new SimpleElementVisitor6<String, RoundEnvironment>() {

                @Override
                public String visitExecutable(final ExecutableElement e, final RoundEnvironment roundEnvironment) {
                    return clazz.getSimpleName().toLowerCase() + " " + getDescriptor(e);
                }

            }, roundEnv));
    }

    private String getDescriptor(ExecutableElement element) {
        return processingEnv.getElementUtils().getBinaryName((TypeElement) element.getEnclosingElement())
               + "::" + element.getSimpleName()
               + " " + getSignature(element);
    }

    private String getSignature(ExecutableElement element) {
        final Types types = processingEnv.getTypeUtils();
        final StringBuilder sb = new StringBuilder("(");
        for (VariableElement p : element.getParameters())
            sb.append(getSignature(types.erasure(p.asType())));
        return sb.append(")")
                 .append(getSignature(types.erasure(element.getReturnType())))
                 .toString();
    }

    private String getSignature(TypeMirror type) {
        return type.accept(new SimpleTypeVisitor6<String, Void>() {

            @Override
            public String visitPrimitive(final PrimitiveType t, final Void dummy) {
                final String type = t.toString();
                if ("boolean".equals(type)) return "Z";
                if ("short".equals(type)) return "S";
                if ("int".equals(type)) return "I";
                if ("long".equals(type)) return "J";
                if ("float".equals(type)) return "F";
                if ("double".equals(type)) return "D";
                if ("char".equals(type)) return "C";
                if ("byte".equals(type)) return "B";
                return super.visitPrimitive(t, dummy);
            }

            @Override public String visitNoType(final NoType t, final Void aVoid) {
                return "V";
            }

            @Override
            public String visitArray(final ArrayType t, final Void aVoid) {
                return "[" + getSignature(t.getComponentType());
            }

            @Override
            protected String defaultAction(final TypeMirror e, final Void aVoid) {
                return "L" + e.toString() + ";";
            }
        }, null);
    }

    private void generateCompileCommandFile(String path, String charset) {
        message(NOTE, "Writing compiler command file at %s", path);
        PrintWriter pw = null;
        try {
            final FileObject file = processingEnv.getFiler().createResource(CLASS_OUTPUT, "", path);
            pw = new PrintWriter(new OutputStreamWriter(file.openOutputStream(), charset));
            if (quiet) pw.println("quiet");
            for (String value : lines)
                pw.println(value);
            pw.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed writing compiler command file at " + path, e);
        } finally {
            if (pw != null)
                pw.close();
        }
    }

    private void message(Diagnostic.Kind level, String msg, Object... args) {
        processingEnv.getMessager().printMessage(level, format(msg, args));
    }
}
