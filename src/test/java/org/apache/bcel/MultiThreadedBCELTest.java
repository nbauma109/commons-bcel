/*
 * Copyright 2023 The Apache Software Foundation.
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

package org.apache.bcel;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.util.ClassLoaderUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MultiThreadedBCELTest {

    private static final String CLASS_EXTENSION = ".class";

    @Test
    @DisplayName("Test multi-threaded parsing of Java classes using BCEL")
    public void testMultiThreadedParsing() throws InterruptedException, IOException, ClassFormatException {
        final String[] classNames = new String[] {"java.lang.Object", "java.util.ArrayList", "java.util.HashMap",
                                                  "java.util.Vector", "java.util.HashSet", "java.util.Hashtable",
                                                  "java.io.File", "java.io.FileInputStream", "java.io.FileOutputStream",
                                                  "java.net.Socket", "java.net.ServerSocket", "java.nio.channels.FileChannel",
                                                  "java.nio.channels.SocketChannel", "java.lang.Thread", "java.util.UUID"};

        final ExecutorService executor = Executors.newFixedThreadPool(15);

        for (final String className : classNames) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String classFilePath = className.replace(".", "/") + CLASS_EXTENSION;
                        final JavaClass javaClass = new ClassParser(ClassLoaderUtils.getResourceAsStream(classFilePath)).parse();
                        assertNotNull(javaClass);
                    } catch (final IOException | ClassFormatException e) {
                        fail("Exception thrown while parsing class: " + className, e);
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
    }
}
