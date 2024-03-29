/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ballerina.core.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ballerina.core.interpreter.BLangExecutor;
import org.wso2.ballerina.core.interpreter.CallableUnitInfo;
import org.wso2.ballerina.core.interpreter.Context;
import org.wso2.ballerina.core.interpreter.LocalVarLocation;
import org.wso2.ballerina.core.interpreter.RuntimeEnvironment;
import org.wso2.ballerina.core.interpreter.StackFrame;
import org.wso2.ballerina.core.model.BallerinaFile;
import org.wso2.ballerina.core.model.BallerinaFunction;
import org.wso2.ballerina.core.model.Parameter;
import org.wso2.ballerina.core.model.Position;
import org.wso2.ballerina.core.model.Resource;
import org.wso2.ballerina.core.model.Service;
import org.wso2.ballerina.core.model.SymbolName;
import org.wso2.ballerina.core.model.expressions.Expression;
import org.wso2.ballerina.core.model.expressions.FunctionInvocationExpr;
import org.wso2.ballerina.core.model.expressions.ResourceInvocationExpr;
import org.wso2.ballerina.core.model.expressions.VariableRefExpr;
import org.wso2.ballerina.core.model.types.BTypes;
import org.wso2.ballerina.core.model.values.BArray;
import org.wso2.ballerina.core.model.values.BString;
import org.wso2.ballerina.core.model.values.BValue;
import org.wso2.ballerina.core.runtime.errors.handler.ErrorHandlerUtils;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

import static org.wso2.ballerina.core.runtime.Constants.SYSTEM_PROP_BAL_ARGS;

/**
 * {@code BalProgramExecutor} is responsible for executing a BallerinaProgram.
 *
 * @since 0.8.0
 */
public class BalProgramExecutor {

    private static final Logger log = LoggerFactory.getLogger(BalProgramExecutor.class);

    public static void execute(CarbonMessage cMsg, CarbonCallback callback, Resource resource, Service service,
                               Context balContext) {
        SymbolName symbolName = service.getSymbolName();
        balContext.setServiceInfo(
            new CallableUnitInfo(symbolName.getName(), symbolName.getPkgName(), service.getServiceLocation()));

        balContext.setBalCallback(new DefaultBalCallback(callback));

        // Create the interpreter and Execute
        RuntimeEnvironment runtimeEnv = resource.getApplication().getRuntimeEnv();
        BLangExecutor executor = new BLangExecutor(runtimeEnv, balContext);
        new ResourceInvocationExpr(resource).executeMultiReturn(executor);
    }

    /**
     * Execute a BallerinaFunction main function.
     *
     * @param balFile Ballerina main function to be executed in given Ballerina File.
     */
    public static void execute(BallerinaFile balFile) {

        Context bContext = new Context();
        try {
            SymbolName argsName;
            BallerinaFunction mainFunction = (BallerinaFunction) balFile.getMainFunction();
            if (mainFunction != null) {

                // TODO Refactor this logic ASAP
                Parameter[] parameters = mainFunction.getParameters();
                argsName = parameters[0].getName();

//                if (parameters.length == 1 && parameters[0].getType() == BTypes.getArrayType(BTypes.
//                        STRING_TYPE.toString())) {
//                } else {
//                    throw new BallerinaException("Main function does not comply with standard main function in" +
//                            " ballerina");
//                }

                // Read from command line arguments
                String balArgs = System.getProperty(SYSTEM_PROP_BAL_ARGS);
                String[] arguments;

                if (balArgs == null || balArgs.trim().length() == 0) {
                    arguments = new String[0];
                } else {
                    arguments = balArgs.split(";");
                }

                Expression[] exprs = new Expression[1];
                VariableRefExpr variableRefExpr = new VariableRefExpr(argsName);
                LocalVarLocation location = new LocalVarLocation(0);
                variableRefExpr.setMemoryLocation(location);
                variableRefExpr.setType(BTypes.STRING_TYPE);
                exprs[0] = variableRefExpr;

                BArray<BString> arrayArgs = new BArray<>(BString.class);
                for (int i = 0; i < arguments.length; i++) {
                    arrayArgs.add(i, new BString(arguments[i]));
                }
                BValue[] argValues = {arrayArgs};

                // 3) Create a function invocation expression
                Position mainFuncLocation = mainFunction.getLocation();
                FunctionInvocationExpr funcIExpr = new FunctionInvocationExpr(
                        new SymbolName("main", balFile.getPackageName()), exprs);
                funcIExpr.setOffset(1);
                funcIExpr.setCallableUnit(mainFunction);
                funcIExpr.setLocation(mainFuncLocation);

                SymbolName functionSymbolName = funcIExpr.getCallableUnitName();
                CallableUnitInfo functionInfo = new CallableUnitInfo(functionSymbolName.getName(),
                        functionSymbolName.getPkgName(), mainFuncLocation);

                StackFrame currentStackFrame = new StackFrame(argValues, new BValue[0], functionInfo);
                bContext.getControlStack().pushFrame(currentStackFrame);
                RuntimeEnvironment runtimeEnv = RuntimeEnvironment.get(balFile);
                BLangExecutor executor = new BLangExecutor(runtimeEnv, bContext);
                funcIExpr.executeMultiReturn(executor);

                bContext.getControlStack().popFrame();
            }
        } catch (Throwable ex) {
            String errorMsg = ErrorHandlerUtils.getErrorMessage(ex);
            String stacktrace = ErrorHandlerUtils.getMainFuncStackTrace(bContext, ex);
            log.error(errorMsg + "\n" + stacktrace);
        }
    }
}
