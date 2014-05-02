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
import org.kohsuke.MetaInfServices;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.lang.String.format;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.tools.Diagnostic.Kind.*;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link Processor} that generates a <code>hotspot_compiler</code> file for using with <code>-XX:CompileCommandFile</code>.
 *
 * @author <a href="http://github.com/nicoulaj">Julien Nicoulaud</a>
 * @see <a href="http://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html"><code>CompileCommand</code>/<code>CompileCommandFile</code> documentation</a>
 */
@MetaInfServices(Processor.class)
@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationTypes({"net.nicoulaj.compilecommand.annotations.Break",
                           "net.nicoulaj.compilecommand.annotations.CompileOnly",
                           "net.nicoulaj.compilecommand.annotations.DontInline",
                           "net.nicoulaj.compilecommand.annotations.Exclude",
                           "net.nicoulaj.compilecommand.annotations.Inline",
                           "net.nicoulaj.compilecommand.annotations.Log",
                           "net.nicoulaj.compilecommand.annotations.Option",
                           "net.nicoulaj.compilecommand.annotations.Options",
                           "net.nicoulaj.compilecommand.annotations.Print",
                           "net.nicoulaj.compilecommand.annotations.Quiet"})
@SuppressWarnings("unused")
public final class CompileCommandProcessor extends AbstractProcessor {

    public static final String COMPILE_COMMAND_FILE_PATH_OPTION = "compile.command.file.output.path";

    public static final String COMPILE_COMMAND_FILE_PATH_DEFAULT = "META-INF/hotspot_compiler";

    private final SortedSet<String> lines;

    private boolean quiet = false;

    public CompileCommandProcessor() {
        lines = new TreeSet<>();
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

        if (roundEnv.processingOver())
            generateCompileCommandFile(processingEnv.getOptions().getOrDefault(COMPILE_COMMAND_FILE_PATH_OPTION, COMPILE_COMMAND_FILE_PATH_DEFAULT));

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
            for (Option option : element.getAnnotation(Options.class).value())
                processOption(element, option, roundEnv);
    }

    private void processOption(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Option.class))
            processOption(element, element.getAnnotation(Option.class), roundEnv);
    }

    private void processOption(Element element, Option option, RoundEnvironment roundEnv) {
        lines.add(element.accept(new SimpleElementVisitor8<String, RoundEnvironment>() {

            @Override
            public String visitExecutable(final ExecutableElement e, final RoundEnvironment roundEnvironment) {
                return Option.class.getSimpleName().toLowerCase() + " " + getDescriptor(e) + " " + option.value();
            }

            @Override
            protected String defaultAction(final Element e, final RoundEnvironment roundEnvironment) {
                error(e, "@%s is not allowed on a %s", Option.class.getSimpleName(), e.getKind());
                return null;
            }
        }, roundEnv));
    }

    private void processPrint(RoundEnvironment roundEnv) {
        processSimpleMethodAnnotation(Print.class, roundEnv);
    }

    private void processQuiet(RoundEnvironment roundEnv) {
        if (!roundEnv.getElementsAnnotatedWith(Quiet.class).isEmpty())
            quiet = true;
    }

    private void processSimpleMethodAnnotation(Class<? extends Annotation> clazz, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(clazz))
            lines.add(element.accept(new SimpleElementVisitor8<String, RoundEnvironment>() {

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
        return type.accept(new SimpleTypeVisitor8<String, Void>() {

            @Override
            public String visitPrimitive(final PrimitiveType t, final Void aVoid) {
                switch (t.toString()) {
                case "boolean":
                    return "Z";
                case "short":
                    return "S";
                case "int":
                    return "I";
                case "long":
                    return "J";
                case "float":
                    return "F";
                case "double":
                    return "D";
                case "char":
                    return "C";
                case "byte":
                    return "B";
                }
                return super.visitPrimitive(t, aVoid);
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
        try {
            info("Writing compiler command file at %s", path);
            final FileObject file = processingEnv.getFiler().createResource(CLASS_OUTPUT, "", path);
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(file.openOutputStream(), "UTF-8"))) {
                if (quiet) pw.println("quiet");
                for (String value : lines)
                    pw.println(value);
            }
        } catch (IOException e) {
            error("Failed writing compiler command file : %s", e);
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
