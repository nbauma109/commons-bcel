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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Utility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BCELifierTestCase {

    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String EOL = System.lineSeparator();
    private static final String CLASSPATH = System.getProperty("java.class.path");

    // Canonicalise the javap output so it compares better
    private String canonHashRef(String input) {
        input = input.replaceAll("#\\d+", "#n"); // numbers may vary in length
        input = input.replaceAll(" +", " "); // collapse spaces
        input = input.replaceAll("//.+", ""); // comments may vary
        return input;
    }

    private String exec(final File workDir, final String... args) throws Exception {
        // System.err.println(java.util.Arrays.toString(args));
        final ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(workDir);
        pb.redirectErrorStream(true);
        final Process proc = pb.start();
        try (BufferedInputStream is = new BufferedInputStream(proc.getInputStream())) {
            final byte[] buff = new byte[2048];
            int len;

            final StringBuilder sb = new StringBuilder();
            while ((len = is.read(buff)) != -1) {
                sb.append(new String(buff, 0, len));
            }
            return sb.toString();
        }
    }

    private void testClassOnPath(final String javaClassFileName) throws Exception {
        final PrintStream sysout = System.out;
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            final String internalName = javaClassFileName.replace(".class", "");
            // Get javap of the input class
            final String initial = exec(null, "javap", "-p", "-c", "-cp", CLASSPATH, internalName);
            assertTrue(initial.startsWith("Compiled from"));
            BCELifier.main(new String[] { internalName });
            final String sourceFileContents = new String(out.toByteArray());
            final String sourceFilePath = TMP_DIR + File.separatorChar + javaClassFileName.replace(".class", "Creator.java");
            new File(sourceFilePath).getParentFile().mkdirs();
            Files.write(Paths.get(sourceFilePath), sourceFileContents.getBytes());
            final String compilationResult = exec(null, "javac", "-cp", CLASSPATH, "-d", TMP_DIR, sourceFilePath);
            assertEquals("", compilationResult);
            final String creatorClassName = Utility.pathToPackage(javaClassFileName.replace(".class", "Creator"));
            final String executionResult = exec(new File(TMP_DIR), "java", "-cp", "." + File.pathSeparator + CLASSPATH, creatorClassName);
            assertEquals("", executionResult);
            final String output = exec(null, "javap", "-p", "-c", TMP_DIR + File.separatorChar + Utility.pathToPackage(javaClassFileName));
            assertTrue(output.startsWith("Compiled from"));
            assertEquals(canonHashRef(initial), canonHashRef(output));
        } finally {
            System.setOut(sysout);
        }
    }

    /*
     * Dump a class using "javap" and compare with the same class recreated using BCELifier, "javac", "java" and dumped with
     * "javap" TODO: detect if JDK present and skip test if not
     */
    @ParameterizedTest
    @ValueSource(strings = {
    // @formatter:off
        "Java8Example.class",
        "Java8Example2.class",
        "org/apache/bcel/classfile/ClassFormatException.class"
    // @formatter:on
    })
    public void testJavapCompare(final String pathToClass) throws Exception {
        testClassOnPath(pathToClass);
    }

    @Test
    public void testStart() throws Exception {
        final OutputStream os = new ByteArrayOutputStream();
        final JavaClass javaClass = BCELifier.getJavaClass("Java8Example");
        assertNotNull(javaClass);
        final BCELifier bcelifier = new BCELifier(javaClass, os);
        bcelifier.start();
    }
    
    @Test
    public void testMainNoArg() throws Exception {
        final PrintStream sysout = System.out;
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            BCELifier.main(new String[0]);
            final String outputNoArgs = new String(out.toByteArray());
            assertEquals("Usage: BCELifier className" + EOL + "\tThe class must exist on the classpath" + EOL, outputNoArgs);
        } finally {
            System.setOut(sysout);
        }
    }
}
