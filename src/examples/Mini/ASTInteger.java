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
/* Generated By:JJTree: Do not edit this line. ASTInteger.java */
/* JJT: 0.3pre1 */

package Mini;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUSH;

/**
 *
 */
public class ASTInteger extends ASTExpr {
    public static Node jjtCreate(final MiniParser p, final int id) {
        return new ASTInteger(p, id);
    }

    private int value;

    // Generated methods
    ASTInteger(final int id) {
        super(id);
    }

    ASTInteger(final MiniParser p, final int id) {
        super(p, id);
    }

    // closeNode, dump inherited from Expr

    /**
     * Fifth pass, produce Java byte code.
     */
    @Override
    public void byte_code(final InstructionList il, final MethodGen method, final ConstantPoolGen cp) {
        il.append(new PUSH(cp, value));
        ASTFunDecl.push();
    }

    /**
     * Fourth pass, produce Java code.
     */
    @Override
    public void code(final StringBuffer buf) {
        ASTFunDecl.push(buf, "" + value);
    }

    /**
     * Second pass Overrides AstExpr.eval()
     * 
     * @return type of expression
     */
    @Override
    public int eval(final int expected) {
        is_simple = true; // (Very) simple expression, always true

        return type = T_INT;
    }

    int getValue() {
        return value;
    }

    void setValue(final int value) {
        this.value = value;
    }

    /**
     * @return identifier and line/column number of appearance
     */
    @Override
    public String toString() {
        return super.toString() + " = " + value;
    }

    /**
     * Overrides ASTExpr.traverse()
     */
    @Override
    public ASTExpr traverse(final Environment env) {
        this.env = env;
        return this; // Nothing to reduce/traverse here
    }
}
