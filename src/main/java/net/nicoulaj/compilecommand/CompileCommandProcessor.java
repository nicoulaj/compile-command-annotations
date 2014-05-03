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

    public static final String COMPILE_COMMAND_FILE_PATH_OPTION = "compile.command.file.output.path";

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

        info("Processing compiler hints annotations");

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
        generateCompileCommandFile(outputPath != null ? outputPath : COMPILE_COMMAND_FILE_PATH_DEFAULT);

        info("Done processing compiler hints annotations");

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

            @Override
            protected String defaultAction(final Element e, final String option) {
                error(element, "@%s is not allowed on a %s", Option.class.getSimpleName(), e.getKind());
                return null;
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

                @Override
                protected String defaultAction(final Element e, final RoundEnvironment roundEnvironment) {
                    error(e, "@%s is not allowed on a %s", clazz.getSimpleName(), e.getKind());
                    return null;
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

    private void generateCompileCommandFile(String path) {
        info("Writing compiler command file at %s", path);
        PrintWriter pw = null;
        try {
            final FileObject file = processingEnv.getFiler().createResource(CLASS_OUTPUT, "", path);
            pw = new PrintWriter(new OutputStreamWriter(file.openOutputStream(), "UTF-8"));
            if (quiet) pw.println("quiet");
            for (String value : lines)
                pw.println(value);
            pw.flush();
        } catch (IOException e) {
            error("Failed writing compiler command file : %s", e);
        } finally {
            if (pw != null)
                pw.close();
        }
    }

    private void info(String msg, Object... args) {
        message(NOTE, msg, args);
    }

    private void info(Element element, String msg, Object... args) {
        message(NOTE, element, msg, args);
    }

    private void warn(String msg, Object... args) {
        message(WARNING, msg, args);
    }

    private void warn(Element element, String msg, Object... args) {
        message(WARNING, element, msg, args);
    }

    private void error(String msg, Object... args) {
        message(ERROR, msg, args);
    }

    private void error(Element element, String msg, Object... args) {
        message(ERROR, element, msg, args);
    }

    private void message(Diagnostic.Kind level, String msg, Object... args) {
        processingEnv.getMessager().printMessage(level, format(msg, args));
    }

    private void message(Diagnostic.Kind level, Element element, String msg, Object... args) {
        processingEnv.getMessager().printMessage(level, format(msg, args), element);
    }
}
