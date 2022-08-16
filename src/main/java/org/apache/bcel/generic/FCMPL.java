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
 *
 */
package org.apache.bcel.generic;

/**
 * FCMPL - Compare floats: value1 &lt; value2
 * 
 * <PRE>
 * Stack: ..., value1, value2 -&gt; ..., result
 * </PRE>
 *
 */
public class FCMPL extends Instruction implements TypedInstruction, StackProducer, StackConsumer {

    public FCMPL() {
        super(org.apache.bcel.Const.FCMPL, (short) 1);
    }

    /**
     * Call corresponding visitor method(s). The order is: Call visitor methods of implemented interfaces first, then call
     * methods according to the class hierarchy in descending order, i.e., the most specific visitXXX() call comes last.
     *
     * @param v Visitor object
     */
    @Override
    public void accept(final Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitFCMPL(this);
    }

    /**
     * @return Type.FLOAT
     */
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.FLOAT;
    }
}
