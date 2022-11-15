/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.bcel.util;

import org.apache.bcel.classfile.Utility;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BCELifierTestCaseGenerator {

    public static void main(final String[] args) throws Exception {
        final String referenceClassName = args[0];
        final String destinationDirectory = args[1];
        final String[] excludes = Arrays.copyOfRange(args, 2, args.length);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        final CodeSource codeSource = Class.forName(referenceClassName).getProtectionDomain().getCodeSource();
        final URI uri = codeSource.getLocation().toURI();
        final File file = new File(uri);
        try (JarFile jarFile = new JarFile(file)) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().endsWith(".class") && !StringUtils.containsAny(jarEntry.getName(), excludes)) {
                    final String internalName = jarEntry.getName().replace(".class", "");
                    final String className = Utility.pathToPackage(internalName);
                    BCELifier.main(new String[] { className });
                    final Path toPath = Paths.get(destinationDirectory, internalName + "Creator.java");
                    Files.createDirectories(toPath.getParent());
                    Files.write(toPath, out.toByteArray());
                    out.reset();
                    createUnitTest(internalName, destinationDirectory);
                }
            }
        }
    }

    private static void createUnitTest(final String internalName, final String destinationDirectory) throws IOException {
        final String classSimpleName = internalName.substring(internalName.lastIndexOf('/') + 1);
        final Path toPath = Paths.get(destinationDirectory, internalName + "CreatorTestCase.java");
        try (BufferedWriter bw = Files.newBufferedWriter(toPath)) {
            writePackageDeclaration(internalName, bw);
            writeImports(bw);
            writeClass(internalName, classSimpleName, destinationDirectory, bw);
        }
    }

    private static void writeClass(final String internalName, final String classSimpleName, final String destinationDirectory, final BufferedWriter bw) throws IOException {
        bw.write("public class ");
        bw.write(classSimpleName);
        bw.write("CreatorTestCase {");
        bw.newLine();
        bw.newLine();
        bw.write("    @Test");
        bw.newLine();
        bw.write("    public void test");
        bw.write(classSimpleName);
        bw.write("() throws Exception {");
        bw.newLine();
        bw.write("        final PrintStream sysout = System.out;");
        bw.newLine();
        bw.write("        try {");
        bw.newLine();
        bw.write("            final ByteArrayOutputStream out = new ByteArrayOutputStream();");
        bw.newLine();
        bw.write("            System.setOut(new PrintStream(out));");
        bw.newLine();
        bw.write("            final Path path = Paths.get(\"");
        bw.write(destinationDirectory);
        bw.write("/");
        bw.write(internalName);
        bw.write("Creator.java\");");
        bw.newLine();
        bw.write("            final String expected = new String(Files.readAllBytes(path));");
        bw.newLine();
        bw.write("            BCELifier.main(new String[] { \"");
        bw.write(Utility.pathToPackage(internalName));
        bw.write("\" });");
        bw.newLine();
        bw.write("            final String actual = new String(out.toByteArray());");
        bw.newLine();
        bw.write("            assertEquals(expected, actual);");
        bw.newLine();
        bw.write("            ");
        bw.write(classSimpleName);
        bw.write("Creator.main(new String[] {});");
        bw.newLine();
        bw.write("            final String initial = exec(null, \"javap\", \"-p\", \"-c\", \"-cp\", CLASSPATH, \"");
        bw.write(internalName);
        bw.write("\");");
        bw.newLine();
        bw.write("            final String output = exec(null, \"javap\", \"-p\", \"-c\", \"");
        bw.write(Utility.pathToPackage(internalName));
        bw.write(".class\");");
        bw.newLine();
        bw.write("            assertEquals(canonHashRef(initial), canonHashRef(output));");
        bw.newLine();
        bw.write("        } finally {");
        bw.newLine();
        bw.write("            System.setOut(sysout);");
        bw.newLine();
        bw.write("            Files.deleteIfExists(Paths.get(\"");
        bw.write(Utility.pathToPackage(internalName));
        bw.write(".class\"));");
        bw.newLine();
        bw.write("        }");
        bw.newLine();
        bw.write("    }");
        bw.newLine();
        bw.write("}");
    }

    private static void writeImports(final BufferedWriter bw) throws IOException {
        bw.write("import org.apache.bcel.util.BCELifier;");
        bw.newLine();
        bw.write("import org.junit.jupiter.api.Test;");
        bw.newLine();
        bw.newLine();
        bw.write("import java.io.ByteArrayOutputStream;");
        bw.newLine();
        bw.write("import java.io.PrintStream;");
        bw.newLine();
        bw.write("import java.nio.file.Files;");
        bw.newLine();
        bw.write("import java.nio.file.Path;");
        bw.newLine();
        bw.write("import java.nio.file.Paths;");
        bw.newLine();
        bw.newLine();
        bw.write("import static org.apache.bcel.util.BCELifierTestCase.CLASSPATH;");
        bw.newLine();
        bw.write("import static org.apache.bcel.util.BCELifierTestCase.canonHashRef;");
        bw.newLine();
        bw.write("import static org.apache.bcel.util.BCELifierTestCase.exec;");
        bw.newLine();
        bw.write("import static org.junit.jupiter.api.Assertions.assertEquals;");
        bw.newLine();
        bw.newLine();
    }

    private static void writePackageDeclaration(final String internalName, final BufferedWriter bw) throws IOException {
        bw.write("package ");
        bw.write(Utility.pathToPackage(internalName.substring(0, internalName.lastIndexOf('/'))));
        bw.write(";");
        bw.newLine();
        bw.newLine();
    }
}
