/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.ballerina.core.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.wso2.ballerina.core.exception.BallerinaException;
import org.wso2.ballerina.core.exception.LinkerException;
import org.wso2.ballerina.core.exception.SemanticException;
import org.wso2.ballerina.core.interpreter.SymScope;
import org.wso2.ballerina.core.model.BallerinaFile;
import org.wso2.ballerina.core.model.builder.BLangModelBuilder;
import org.wso2.ballerina.core.parser.antlr4.BLangAntlr4Listener;
import org.wso2.ballerina.core.runtime.internal.BuiltInNativeConstructLoader;
import org.wso2.ballerina.core.runtime.internal.GlobalScopeHolder;
import org.wso2.ballerina.core.semantics.SemanticAnalyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Contains utility methods for executing a Ballerina program
 *
 * @since 0.8.0
 */
public class ParserUtils {

    public static BallerinaFile buildLangModel(Path sourceFilePath) {
        try {
            return buildLangModel(Files.newInputStream(sourceFilePath), getFileName(sourceFilePath));
        } catch (Throwable e) {
            throw new BallerinaException(getFileName(sourceFilePath) + ": " +
                    makeFirstLetterUpperCase(e.getMessage()));
        }
    }

    public static BallerinaFile buildLangModel(InputStream inputStream, String name) {

        try {
            ANTLRInputStream antlrInputStream = new ANTLRInputStream(inputStream);
            // Setting the name of the source file being parsed, to the ANTLR input stream.
            // This is required by the parser-error strategy.
            antlrInputStream.name = name;

            BallerinaLexer ballerinaLexer = new BallerinaLexer(antlrInputStream);
            CommonTokenStream ballerinaToken = new CommonTokenStream(ballerinaLexer);

            BallerinaParser ballerinaParser = new BallerinaParser(ballerinaToken);
            ballerinaParser.setErrorHandler(new BallerinaParserErrorStrategy());

            BLangModelBuilder bLangModelBuilder = new BLangModelBuilder();
            BLangAntlr4Listener ballerinaBaseListener = new BLangAntlr4Listener(bLangModelBuilder);
            ballerinaParser.addParseListener(ballerinaBaseListener);
            ballerinaParser.compilationUnit();
            BallerinaFile balFile = bLangModelBuilder.build();

            BuiltInNativeConstructLoader.loadConstructs();
            SymScope globalScope = GlobalScopeHolder.getInstance().getScope();

            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(balFile, globalScope);
            balFile.accept(semanticAnalyzer);

            return balFile;
        } catch (ParseCancellationException | SemanticException | LinkerException e) {
            throw new BallerinaException(makeFirstLetterUpperCase(e.getMessage()));
        } catch (Throwable e) {
            throw new BallerinaException(name + ": " +
                    makeFirstLetterUpperCase(e.getMessage()));
        }
    }

    private static ANTLRInputStream getAntlrInputStream(Path sourceFilePath) {
        try {
            InputStream inputStream = new FileInputStream(sourceFilePath.toFile());
            return new ANTLRInputStream(inputStream);
        } catch (FileNotFoundException e) {
            throw new BallerinaException("ballerina: no such file or directory '" +
                    getFileName(sourceFilePath) + "'");
        } catch (Throwable e) {
            throw new BallerinaException("error reading file or directory'" +
                    getFileName(sourceFilePath) + "': " + e.getMessage());
        }
    }

    static String getFileName(Path sourceFilePath) {
        Path fileNamePath = sourceFilePath.getFileName();
        return (fileNamePath != null) ? fileNamePath.toString() : sourceFilePath.toString();
    }

    static String makeFirstLetterUpperCase(String s) {
        char c[] = s.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

}
