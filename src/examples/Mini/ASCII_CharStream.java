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
/* Generated By:JavaCC: Do not edit this line. ASCII_CharStream.java Version 0.7pre6 */
package Mini;

import java.io.IOException;

/**
 * An implementation of interface CharStream, where the stream is assumed to contain only ASCII characters (without
 * Unicode processing).
 */
public final class ASCII_CharStream {
    public static final boolean staticFlag = true;
    static int bufsize;
    static int available;
    static int tokenBegin;
    static public int bufpos = -1;
    static private int bufline[];
    static private int bufcolumn[];

    static private int column;
    static private int line = 1;

    static private boolean prevCharIsCR;
    static private boolean prevCharIsLF;

    static private java.io.Reader inputStream;

    static private char[] buffer;
    static private int maxNextCharInd;
    static private int inBuf;

    /**
     * Method to adjust line and column numbers for the start of a token.<BR>
     */
    static public void adjustBeginLineColumn(int newLine, final int newCol) {
        int start = tokenBegin;
        int len;

        if (bufpos >= tokenBegin) {
            len = bufpos - tokenBegin + inBuf + 1;
        } else {
            len = bufsize - tokenBegin + bufpos + 1 + inBuf;
        }

        int i = 0, j = 0, k = 0;
        int nextColDiff = 0, columnDiff = 0;

        while (i < len && bufline[j = start % bufsize] == bufline[k = ++start % bufsize]) {
            bufline[j] = newLine;
            nextColDiff = columnDiff + bufcolumn[k] - bufcolumn[j];
            bufcolumn[j] = newCol + columnDiff;
            columnDiff = nextColDiff;
            i++;
        }

        if (i < len) {
            bufline[j] = newLine++;
            bufcolumn[j] = newCol + columnDiff;

            while (i++ < len) {
                if (bufline[j = start % bufsize] != bufline[++start % bufsize]) {
                    bufline[j] = newLine++;
                } else {
                    bufline[j] = newLine;
                }
            }
        }

        line = bufline[j];
        column = bufcolumn[j];
    }

    static public void backup(final int amount) {

        inBuf += amount;
        if ((bufpos -= amount) < 0) {
            bufpos += bufsize;
        }
    }

    static public char BeginToken() throws IOException {
        tokenBegin = -1;
        final char c = readChar();
        tokenBegin = bufpos;

        return c;
    }

    static public void Done() {
        buffer = null;
        bufline = null;
        bufcolumn = null;
    }

    static private void ExpandBuff(final boolean wrapAround) {
        final char[] newbuffer = new char[bufsize + 2048];
        final int[] newbufline = new int[bufsize + 2048];
        final int[] newbufcolumn = new int[bufsize + 2048];

        try {
            if (wrapAround) {
                System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
                System.arraycopy(buffer, 0, newbuffer, bufsize - tokenBegin, bufpos);
                buffer = newbuffer;

                System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
                System.arraycopy(bufline, 0, newbufline, bufsize - tokenBegin, bufpos);
                bufline = newbufline;

                System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
                System.arraycopy(bufcolumn, 0, newbufcolumn, bufsize - tokenBegin, bufpos);
                bufcolumn = newbufcolumn;

                maxNextCharInd = bufpos += bufsize - tokenBegin;
            } else {
                System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
                buffer = newbuffer;

                System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
                bufline = newbufline;

                System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
                bufcolumn = newbufcolumn;

                maxNextCharInd = bufpos -= tokenBegin;
            }
        } catch (final Throwable t) {
            throw new Error(t.getMessage());
        }

        bufsize += 2048;
        available = bufsize;
        tokenBegin = 0;
    }

    static private void FillBuff() throws IOException {
        if (maxNextCharInd == available) {
            if (available == bufsize) {
                if (tokenBegin > 2048) {
                    bufpos = maxNextCharInd = 0;
                    available = tokenBegin;
                } else if (tokenBegin < 0) {
                    bufpos = maxNextCharInd = 0;
                } else {
                    ExpandBuff(false);
                }
            } else if (available > tokenBegin) {
                available = bufsize;
            } else if (tokenBegin - available < 2048) {
                ExpandBuff(true);
            } else {
                available = tokenBegin;
            }
        }

        int i;
        try {
            if ((i = inputStream.read(buffer, maxNextCharInd, available - maxNextCharInd)) == -1) {
                inputStream.close();
                throw new java.io.IOException();
            }
            maxNextCharInd += i;
        } catch (final java.io.IOException e) {
            --bufpos;
            backup(0);
            if (tokenBegin == -1) {
                tokenBegin = bufpos;
            }
            throw e;
        }
    }

    static public int getBeginColumn() {
        return bufcolumn[tokenBegin];
    }

    static public int getBeginLine() {
        return bufline[tokenBegin];
    }

    static public int getEndColumn() {
        return bufcolumn[bufpos];
    }

    static public int getEndLine() {
        return bufline[bufpos];
    }

    static public String GetImage() {
        if (bufpos >= tokenBegin) {
            return new String(buffer, tokenBegin, bufpos - tokenBegin + 1);
        }
        return new String(buffer, tokenBegin, bufsize - tokenBegin) + new String(buffer, 0, bufpos + 1);
    }

    static public char[] GetSuffix(final int len) {
        final char[] ret = new char[len];

        if (bufpos + 1 >= len) {
            System.arraycopy(buffer, bufpos - len + 1, ret, 0, len);
        } else {
            System.arraycopy(buffer, bufsize - (len - bufpos - 1), ret, 0, len - bufpos - 1);
            System.arraycopy(buffer, 0, ret, len - bufpos - 1, bufpos + 1);
        }

        return ret;
    }

    static public char readChar() throws IOException {
        if (inBuf > 0) {
            --inBuf;
            return (char) ((char) 0xff & buffer[bufpos == bufsize - 1 ? (bufpos = 0) : ++bufpos]);
        }

        if (++bufpos >= maxNextCharInd) {
            FillBuff();
        }

        final char c = (char) ((char) 0xff & buffer[bufpos]);

        UpdateLineColumn(c);
        return c;
    }

    static public void ReInit(final java.io.InputStream dstream, final int startline, final int startColumn) {
        ReInit(dstream, startline, startColumn, 4096);
    }

    static public void ReInit(final java.io.InputStream dstream, final int startline, final int startColumn, final int bufferSize) {
        ReInit(new java.io.InputStreamReader(dstream), startline, startColumn, 4096);
    }

    static public void ReInit(final java.io.Reader dstream, final int startline, final int startColumn) {
        ReInit(dstream, startline, startColumn, 4096);
    }

    static public void ReInit(final java.io.Reader dstream, final int startline, final int startColumn, final int bufferSize) {
        inputStream = dstream;
        line = startline;
        column = startColumn - 1;

        if (buffer == null || bufferSize != buffer.length) {
            available = bufsize = bufferSize;
            buffer = new char[bufferSize];
            bufline = new int[bufferSize];
            bufcolumn = new int[bufferSize];
        }
        prevCharIsLF = prevCharIsCR = false;
        tokenBegin = inBuf = maxNextCharInd = 0;
        bufpos = -1;
    }

    static private void UpdateLineColumn(final char c) {
        column++;

        if (prevCharIsLF) {
            prevCharIsLF = false;
            line += column = 1;
        } else if (prevCharIsCR) {
            prevCharIsCR = false;
            if (c == '\n') {
                prevCharIsLF = true;
            } else {
                line += column = 1;
            }
        }

        switch (c) {
        case '\r':
            prevCharIsCR = true;
            break;
        case '\n':
            prevCharIsLF = true;
            break;
        case '\t':
            column--;
            column += 8 - (column & 07);
            break;
        default:
            break;
        }

        bufline[bufpos] = line;
        bufcolumn[bufpos] = column;
    }

    public ASCII_CharStream(final java.io.InputStream dstream, final int startline, final int startColumn) {
        this(dstream, startline, startColumn, 4096);
    }

    public ASCII_CharStream(final java.io.InputStream dstream, final int startline, final int startColumn, final int bufferSize) {
        this(new java.io.InputStreamReader(dstream), startline, startColumn, 4096);
    }

    public ASCII_CharStream(final java.io.Reader dstream, final int startline, final int startColumn) {
        this(dstream, startline, startColumn, 4096);
    }

    public ASCII_CharStream(final java.io.Reader dstream, final int startline, final int startColumn, final int bufferSize) {
        if (inputStream != null) {
            throw new Error("\n   ERROR: Second call to the constructor of a static ASCII_CharStream.  You must\n"
                + "       either use ReInit() or set the JavaCC option STATIC to false\n" + "       during the generation of this class.");
        }
        inputStream = dstream;
        line = startline;
        column = startColumn - 1;

        available = bufsize = bufferSize;
        buffer = new char[bufferSize];
        bufline = new int[bufferSize];
        bufcolumn = new int[bufferSize];
    }

}
