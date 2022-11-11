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
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.junit.jupiter.api.Test;

public class BCELifierTestCase {

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
        // Get javap of the input class
        final String initial = exec(null, "javap", "-p", "-c", javaClassFileName);

        final File workDir = new File("target");
        final File infile = new File(javaClassFileName);
        final JavaClass javaClass = new ClassParser(infile.getPath()).parse();
        assertNotNull(javaClass);
        final String packageName = javaClass.getPackageName().replace('.', File.separatorChar);
        final File outfile = new File(infile.getPath().substring(workDir.getPath().length() + 1).replace(".class", "Creator.java"));
        final String outPath = outfile.getPath();
        try (FileOutputStream fos = new FileOutputStream(new File(workDir, outPath))) {
            final BCELifier bcelifier = new BCELifier(javaClass, fos);
            bcelifier.start();
        }
        exec(workDir, "javac", "-cp", "classes", outPath, "-source", "1.8", "-target", "1.8");
        final String creatorClassName = outPath.substring(outPath.indexOf(packageName)).replace(".java", "").replace(File.separatorChar, '.');
        exec(workDir, "java", "-cp", "." + File.pathSeparator + "classes", creatorClassName);
        final String output = exec(workDir, "javap", "-p", "-c", javaClass.getClassName() + ".class");
        assertEquals(canonHashRef(initial), canonHashRef(output));
    }

    private void testJavapCompare(final File file) throws Exception {
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    testJavapCompare(files[i]);
                }
            }
        } else if (file.isFile() && file.getName().matches("(?<!Creator)\\.class$")) { // exclude *Creator.class to avoid recursive creation of Creator of Creator classes
            testClassOnPath(file.getPath());
        }
    }

    /*
     * Dump a class using "javap" and compare with the same class recreated using BCELifier, "javac", "java" and dumped with
     * "javap" TODO: detect if JDK present and skip test if not
     */
    @Test
    public void testJavapCompare() throws Exception {
        testJavapCompare(new File("target"));
    }

    @Test
    public void testStart() throws Exception {
        final OutputStream os = new ByteArrayOutputStream();
        final JavaClass javaClass = BCELifier.getJavaClass("Java8Example");
        assertNotNull(javaClass);
        final BCELifier bcelifier = new BCELifier(javaClass, os);
        bcelifier.start();
    }

}
