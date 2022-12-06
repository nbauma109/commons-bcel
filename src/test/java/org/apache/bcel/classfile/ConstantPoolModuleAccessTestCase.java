/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.bcel.classfile;

import org.apache.bcel.Const;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code module-info.class} files.
 */
public class ConstantPoolModuleAccessTestCase {

    @Test
    public void testJREModules() throws Exception {
        final Enumeration<URL> moduleURLs = getClass().getClassLoader().getResources("module-info.class");
        while (moduleURLs.hasMoreElements()) {
            final URL url = moduleURLs.nextElement();
            try (InputStream inputStream = url.openStream()) {
                final ClassParser classParser = new ClassParser(inputStream, "module-info.class");
                final JavaClass javaClass = classParser.parse();
                final ConstantPool constantPool = javaClass.getConstantPool();
                final EmptyVisitor visitor = new EmptyVisitor() {
                    @Override
                    public void visitModule(final Module obj) {
                        final String[] usedClassNames = obj.getUsedClassNames(constantPool, true);
                        if (url.getPath().contains("junit-jupiter-engine")) {
                            assertEquals(1, usedClassNames.length);
                            assertEquals("org.junit.jupiter.api.extension.Extension", usedClassNames[0]);
                        } else if (url.getPath().contains("junit-platform-launcher")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("org.junit.platform.engine.TestEngine");
                            expected.add("org.junit.platform.launcher.LauncherDiscoveryListener");
                            expected.add("org.junit.platform.launcher.LauncherSessionListener");
                            expected.add("org.junit.platform.launcher.PostDiscoveryFilter");
                            expected.add("org.junit.platform.launcher.TestExecutionListener");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.rmi/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("java.rmi.server.RMIClassLoaderSpi");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.xml/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("javax.xml.datatype.DatatypeFactory");
                            expected.add("javax.xml.parsers.DocumentBuilderFactory");
                            expected.add("javax.xml.parsers.SAXParserFactory");
                            expected.add("javax.xml.stream.XMLEventFactory");
                            expected.add("javax.xml.stream.XMLInputFactory");
                            expected.add("javax.xml.stream.XMLOutputFactory");
                            expected.add("javax.xml.transform.TransformerFactory");
                            expected.add("javax.xml.validation.SchemaFactory");
                            expected.add("javax.xml.xpath.XPathFactory");
                            expected.add("org.xml.sax.XMLReader");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.datatransfer/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("sun.datatransfer.DesktopDatatransferService");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.desktop/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("java.awt.im.spi.InputMethodDescriptor");
                            expected.add("javax.accessibility.AccessibilityProvider");
                            expected.add("javax.imageio.spi.ImageInputStreamSpi");
                            expected.add("javax.imageio.spi.ImageOutputStreamSpi");
                            expected.add("javax.imageio.spi.ImageReaderSpi");
                            expected.add("javax.imageio.spi.ImageTranscoderSpi");
                            expected.add("javax.imageio.spi.ImageWriterSpi");
                            expected.add("javax.print.PrintServiceLookup");
                            expected.add("javax.print.StreamPrintServiceFactory");
                            expected.add("javax.sound.midi.spi.MidiDeviceProvider");
                            expected.add("javax.sound.midi.spi.MidiFileReader");
                            expected.add("javax.sound.midi.spi.MidiFileWriter");
                            expected.add("javax.sound.midi.spi.SoundbankReader");
                            expected.add("javax.sound.sampled.spi.AudioFileReader");
                            expected.add("javax.sound.sampled.spi.AudioFileWriter");
                            expected.add("javax.sound.sampled.spi.FormatConversionProvider");
                            expected.add("javax.sound.sampled.spi.MixerProvider");
                            expected.add("sun.swing.InteropProvider");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.naming/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("javax.naming.ldap.StartTlsResponse");
                            expected.add("javax.naming.spi.InitialContextFactory");
                            if (javaClass.getMajor() > Const.MAJOR_11) {
                                expected.add("javax.naming.ldap.spi.LdapDnsProvider");
                            }
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.prefs/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("java.util.prefs.PreferencesFactory");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.base/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("java.lang.System$LoggerFinder");
                            expected.add("java.net.ContentHandlerFactory");
                            expected.add("java.net.spi.URLStreamHandlerProvider");
                            expected.add("java.nio.channels.spi.AsynchronousChannelProvider");
                            expected.add("java.nio.channels.spi.SelectorProvider");
                            expected.add("java.nio.charset.spi.CharsetProvider");
                            expected.add("java.nio.file.spi.FileSystemProvider");
                            expected.add("java.nio.file.spi.FileTypeDetector");
                            expected.add("java.security.Provider");
                            expected.add("java.text.spi.BreakIteratorProvider");
                            expected.add("java.text.spi.CollatorProvider");
                            expected.add("java.text.spi.DateFormatProvider");
                            expected.add("java.text.spi.DateFormatSymbolsProvider");
                            expected.add("java.text.spi.DecimalFormatSymbolsProvider");
                            expected.add("java.text.spi.NumberFormatProvider");
                            expected.add("java.time.chrono.AbstractChronology");
                            expected.add("java.time.chrono.Chronology");
                            expected.add("java.time.zone.ZoneRulesProvider");
                            if (javaClass.getMajor() > Const.MAJOR_11) {
                                expected.add("java.util.random.RandomGenerator");
                            }
                            expected.add("java.util.spi.CalendarDataProvider");
                            expected.add("java.util.spi.CalendarNameProvider");
                            expected.add("java.util.spi.CurrencyNameProvider");
                            expected.add("java.util.spi.LocaleNameProvider");
                            expected.add("java.util.spi.ResourceBundleControlProvider");
                            expected.add("java.util.spi.ResourceBundleProvider");
                            expected.add("java.util.spi.TimeZoneNameProvider");
                            expected.add("java.util.spi.ToolProvider");
                            expected.add("javax.security.auth.spi.LoginModule");
                            expected.add("jdk.internal.logger.DefaultLoggerFinder");
                            expected.add("sun.text.spi.JavaTimeDateTimePatternProvider");
                            expected.add("sun.util.locale.provider.LocaleDataMetaInfo");
                            expected.add("sun.util.resources.LocaleData$CommonResourceBundleProvider");
                            expected.add("sun.util.resources.LocaleData$SupplementaryResourceBundleProvider");
                            expected.add("sun.util.spi.CalendarProvider");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.management.agent/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("jdk.internal.agent.spi.AgentProvider");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.management/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("javax.management.remote.JMXConnectorProvider");
                            expected.add("javax.management.remote.JMXConnectorServerProvider");
                            expected.add("sun.management.spi.PlatformMBeanProvider");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.sql/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("java.sql.Driver");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.httpserver/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("com.sun.net.httpserver.spi.HttpServerProvider");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.sql.rowset/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("javax.sql.rowset.RowSetFactory");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.compiler/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("javax.tools.DocumentationTool");
                            expected.add("javax.tools.JavaCompiler");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/java.scripting/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("javax.script.ScriptEngineFactory");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.dynalink/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("jdk.dynalink.linker.GuardingDynamicLinkerExporter");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.jdi/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("com.sun.jdi.connect.Connector");
                            expected.add("com.sun.jdi.connect.spi.TransportService");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.compiler/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("javax.annotation.processing.Processor");
                            expected.add("com.sun.source.util.Plugin");
                            if (javaClass.getMajor() > Const.MAJOR_11) {
                                expected.add("com.sun.tools.doclint.DocLint");
                            }
                            expected.add("com.sun.tools.javac.platform.PlatformProvider");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.jconsole/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("com.sun.tools.jconsole.JConsolePlugin");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.attach/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("com.sun.tools.attach.spi.AttachProvider");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.jshell/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("jdk.jshell.spi.ExecutionControlProvider");
                            expected.add("jdk.internal.editor.spi.BuildInEditorProvider");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.internal.le/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("jdk.internal.org.jline.terminal.spi.JnaSupport");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.jlink/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("jdk.tools.jlink.plugin.Plugin");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.internal.jvmstat/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("sun.jvmstat.monitor.MonitoredHostService");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.jpackage/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("jdk.jpackage.internal.Bundler");
                            expected.add("jdk.jpackage.internal.Bundlers");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.naming.ldap/module-info.class")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("com.sun.jndi.ldap.spi.LdapDnsProvider");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else if (url.getPath().contains("/jdk.jsobject/module-info.class") && javaClass.getMajor() == Const.MAJOR_11) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("jdk.internal.netscape.javascript.spi.JSObjectProvider");
                            assertEquals(expected, Arrays.asList(usedClassNames));
                        } else {
                            assertEquals(0, usedClassNames.length);
                        }
                        super.visitModule(obj);
                    }

                    @Override
                    public void visitModuleExports(final ModuleExports obj) {
                        final String packageName = obj.getPackageName(constantPool);
                        final String[] toModuleNames = obj.getToModuleNames(constantPool);
                        if (url.getPath().contains("junit-platform-commons")) {
                            final List<String> expected = new ArrayList<>();
                            expected.add("org.junit.jupiter.api");
                            expected.add("org.junit.jupiter.engine");
                            expected.add("org.junit.jupiter.migrationsupport");
                            expected.add("org.junit.jupiter.params");
                            expected.add("org.junit.platform.console");
                            expected.add("org.junit.platform.engine");
                            expected.add("org.junit.platform.launcher");
                            expected.add("org.junit.platform.reporting");
                            expected.add("org.junit.platform.runner");
                            expected.add("org.junit.platform.suite.api");
                            switch (packageName) {
                                case "org.junit.platform.commons.util":
                                    expected.add("org.junit.platform.suite.commons");
                                    // fall through
                                case "org.junit.platform.commons.logging":
                                    expected.add("org.junit.platform.suite.engine");
                                    expected.add("org.junit.platform.testkit");
                                    expected.add("org.junit.vintage.engine");
                                    assertEquals(expected, Arrays.asList(toModuleNames));
                                    break;
                                default:
                                    assertEquals(0, toModuleNames.length);
                                    break;
                            }
                        }
                        super.visitModuleExports(obj);
                    }

                    @Override
                    public void visitModuleProvides(final ModuleProvides obj) {
                        final String interfaceName = obj.getInterfaceName(constantPool);
                        final String[] implementationClassNames = obj.getImplementationClassNames(constantPool, true);
                        if (url.getPath().contains("junit-jupiter-engine")) {
                            assertEquals("org.junit.platform.engine.TestEngine", interfaceName);
                            assertEquals(1, implementationClassNames.length);
                            assertEquals("org.junit.jupiter.engine.JupiterTestEngine", implementationClassNames[0]);
                        } else if (url.getPath().contains("junit-platform-launcher")) {
                            assertEquals("org.junit.platform.launcher.TestExecutionListener", interfaceName);
                            assertEquals(1, implementationClassNames.length);
                            assertEquals("org.junit.platform.launcher.listeners.UniqueIdTrackingListener", implementationClassNames[0]);
                        }
                        super.visitModuleProvides(obj);
                    }

                    @Override
                    public void visitModuleOpens(final ModuleOpens obj) {
                        final String packageName = obj.getPackageName(constantPool);
                        final String[] toModuleNames = obj.getToModuleNames(constantPool);
                        if (url.getPath().contains("junit-jupiter-engine")) {
                            assertEquals("org.junit.jupiter.engine.extension", packageName);
                            assertEquals(1, toModuleNames.length);
                            assertEquals("org.junit.platform.commons", toModuleNames[0]);
                        }
                        if (url.getPath().contains("junit-jupiter-api")) {
                            assertEquals("org.junit.jupiter.api.condition", packageName);
                            assertEquals(1, toModuleNames.length);
                            assertEquals("org.junit.platform.commons", toModuleNames[0]);
                        }
                        super.visitModuleOpens(obj);
                    }

                    @Override
                    public void visitModuleRequires(final ModuleRequires obj) {
                        if (url.getPath().contains("junit-jupiter-engine")) {
                            final String moduleName = obj.getModuleName(constantPool);
                            final List<String> expected = new ArrayList<>();
                            expected.add("java.base");
                            expected.add("org.apiguardian.api");
                            expected.add("org.junit.jupiter.api");
                            expected.add("org.junit.platform.commons");
                            expected.add("org.junit.platform.engine");
                            expected.add("org.opentest4j");
                            assertTrue(expected.contains(moduleName));
                        }
                        super.visitModuleRequires(obj);
                    }
                };
                javaClass.accept(new DescendingVisitor(javaClass, visitor));
            }
        }
    }
}