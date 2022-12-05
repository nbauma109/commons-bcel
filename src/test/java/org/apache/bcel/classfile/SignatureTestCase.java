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
package org.apache.bcel.classfile;

import org.apache.bcel.AbstractTestCase;
import org.apache.bcel.Repository;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;


public class SignatureTestCase extends AbstractTestCase {

    @Test
    public void testMap() throws Exception {
        final JavaClass javaClass = Repository.lookupClass(Map.class);
        final Signature classSignature = (Signature) findAttribute("Signature", javaClass.getAttributes());
        final String translatedSignature = Signature.translate(classSignature.getSignature());
        assertEquals("<K, V>java.lang.Object", translatedSignature);
        final String methodSignature = javaClass.getMethod(Map.class.getMethod("computeIfAbsent", Object.class, Function.class)).getSignature();
        final String translatedMethodSignature = Signature.translate(methodSignature);
        assertEquals("(java.lang.Object, java.util.function.Function)java.lang.Object", translatedMethodSignature);
    }
}
