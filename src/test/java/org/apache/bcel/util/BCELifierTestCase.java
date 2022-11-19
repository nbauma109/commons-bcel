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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.bcel.classfile.JavaClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BCELifierTestCase {

    private static final String EOL = System.lineSeparator();

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
            String output = sb.toString();
            assertEquals(0, proc.waitFor(), output);
            return output;
        }
    }

    private void testClassOnPath(final String javaClassFileName) throws Exception {
        // Get javap of the input class
        final String initial = exec(null, "javap", "-p", "-c", javaClassFileName);

        final File workDir = new File("target");
        final File infile = new File(javaClassFileName);
        final JavaClass javaClass = BCELifier.getJavaClass(infile.getName().replace(JavaClass.EXTENSION, ""));
        assertNotNull(javaClass);
        final File outfile = new File(workDir, infile.getName().replace(JavaClass.EXTENSION, "Creator.java"));
        try (FileOutputStream fos = new FileOutputStream(outfile)) {
            final BCELifier bcelifier = new BCELifier(javaClass, fos);
            bcelifier.start();
        }
        assertEquals("", exec(workDir, "javac", "-cp", "classes", outfile.getName()));
        assertEquals("", exec(workDir, "java", "-cp", "." + File.pathSeparator + "classes", outfile.getName().replace(".java", "")));
        final String output = exec(workDir, "javap", "-p", "-c", infile.getName());
        assertEquals(canonHashRef(initial), canonHashRef(output));
    }

    /*
     * Dump a class using "javap" and compare with the same class recreated using BCELifier, "javac", "java" and dumped with
     * "javap" TODO: detect if JDK present and skip test if not
     */
    @ParameterizedTest
    @ValueSource(strings = {
    // @formatter:off
        "target/test-classes/Java8Example.class",
        "target/test-classes/Java8Example2.class",
        "target/test-classes/Java4Example.class"
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
