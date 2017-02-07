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
package org.wso2.ballerina.core.nativeimpl.lang.mock;

import org.wso2.ballerina.core.exception.BallerinaException;
import org.wso2.ballerina.core.interpreter.Context;
import org.wso2.ballerina.core.model.Application;
import org.wso2.ballerina.core.model.BallerinaConnector;
import org.wso2.ballerina.core.model.Connector;
import org.wso2.ballerina.core.model.ConnectorDcl;
import org.wso2.ballerina.core.model.Package;
import org.wso2.ballerina.core.model.Service;
import org.wso2.ballerina.core.model.expressions.BasicLiteral;
import org.wso2.ballerina.core.model.expressions.Expression;
import org.wso2.ballerina.core.model.types.TypeEnum;
import org.wso2.ballerina.core.model.values.BValue;
import org.wso2.ballerina.core.nativeimpl.AbstractNativeFunction;
import org.wso2.ballerina.core.nativeimpl.annotations.Argument;
import org.wso2.ballerina.core.nativeimpl.annotations.BallerinaFunction;
import org.wso2.ballerina.core.nativeimpl.connectors.AbstractNativeConnector;
import org.wso2.ballerina.core.runtime.registry.ApplicationRegistry;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import static java.security.AccessController.doPrivileged;

/**
 * Native function ballerina.lang.mock:startService.
 *
 * @since 0.8.0
 */
@BallerinaFunction(packageName = "ballerina.lang.mock", functionName = "setValue", args = {
        @Argument(name = "mockConnectorPath", type = TypeEnum.STRING),
        @Argument(name = "value", type = TypeEnum.STRING) }, isPublic = true)
public class SetValue extends AbstractNativeFunction {

    public static final String FIELD_NAME_VALUE = "value";
    private static final String COULD_NOT_FIND_MATCHING_CONNECTOR = "Could not find a matching connector for the name ";

    @Override
    public BValue[] execute(Context ctx) {
        //1) split the mockCntrPathString by dot
        //first element is the service name, last element is a primitive

        //Get ApplicationRegistry.getInstance().getApplications();
        //traverse it to find the service

        //via reflection, get the global connector instance as given by names in the path array.
        //keep traversing the path array until the last connector (element - 1).
        //once found, get the primitive that has the name of last element in the path array
        //change that primitive type's value to the `value` user entered.
        //then return

        MockConnectorPath mockCntrPath = parseMockConnectorPath(ctx);

        Collection<Application> applications = ApplicationRegistry.getInstance().getApplications().values();
        Optional<Service> matchingService = applications.stream().map(app -> app.getPackages().values())
                .flatMap(Collection::stream).map(Package::getServices).flatMap(Collection::stream)
                .filter(service -> service.getSymbolName().getName().equals(mockCntrPath.serviceName)).findAny();

        if (!matchingService.isPresent()) {
            // Added for user convenience. Since we are stopping further progression of the program,
            // perf overhead is ignored.
            StringBuilder listOfServices = new StringBuilder();
            applications.stream().map(app -> app.getPackages().values()).flatMap(Collection::stream)
                    .map(Package::getServices).flatMap(Collection::stream)
                    .forEachOrdered(service -> listOfServices.append(service.getSymbolName().getName()).append(", "));

            throw new BallerinaException("No matching service for the name '" + mockCntrPath.serviceName + "' found. "
                    + "Did you mean to include one of these services? " + listOfServices.toString());
        }

        //get the connector inside the service
        ConnectorDcl[] connectorDcls = matchingService.get().getConnectorDcls();
        if (mockCntrPath.connectorNames.size() > 0) {
            String firstConnectorName = mockCntrPath.connectorNames.pop();
            Optional<ConnectorDcl> connectorDcl = Arrays.stream(connectorDcls)
                    .filter(connector -> connector.getVarName().getName().equals(firstConnectorName)).findAny();
            if (!connectorDcl.isPresent()) {
                throw new BallerinaException(COULD_NOT_FIND_MATCHING_CONNECTOR + firstConnectorName);
            }

            ConnectorDcl finalConnectorDcl = getFinalConnector(connectorDcl.get(), mockCntrPath.connectorNames);
            Expression[] argExprs = finalConnectorDcl.getArgExprs();
            validateArgsExprsArray(argExprs, finalConnectorDcl);

            if (argExprs.length == 1) {
                if (argExprs[0] instanceof BasicLiteral) {
                    BValue bValue = ((BasicLiteral) argExprs[0]).getBValue();
                    try {
                        setProperty(bValue, mockCntrPath.terminalVarName, mockCntrPath.mockValue);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        //retrying with the default name
                        try {
                            setProperty(bValue, FIELD_NAME_VALUE, mockCntrPath.mockValue);
                        } catch (IllegalAccessException | NoSuchFieldException e1) {
                            throw new BallerinaException(
                                    "Error while updating the field " + mockCntrPath.terminalVarName + " to "
                                            + mockCntrPath.mockValue + " of service " + mockCntrPath.serviceName);
                        }
                    }
                } else {
                    throw new BallerinaException("TODO"); // todo handle expression types other than literal
                }
            }
        }

        return VOID_RETURN;
    }

    private void validateArgsExprsArray(Expression[] argExprs, ConnectorDcl finalConnectorDcl)
            throws BallerinaException {
        if (argExprs.length == 0) {
            throw new BallerinaException(
                    "The arguments list in the connector " + finalConnectorDcl.getVarName().getName()
                            + "is empty. Cannot update " + finalConnectorDcl.getConnectorName().getName()
                            + " connector's arguments");
        }
    }

    private MockConnectorPath parseMockConnectorPath(Context ctx) {
        String mockCntrPathString = getArgument(ctx, 0).stringValue();
        String mockValue = getArgument(ctx, 1).stringValue();
        String[] mockCntrPathArr = mockCntrPathString.split("\\.");
        if (mockCntrPathArr.length < 2) {
            throw new BallerinaException(
                    "Error in parsing " + mockCntrPathString + ". Syntax - <ServiceName>[.]<ConnectorName>...[.]url");
        }

        LinkedList<String> connectorNamesList = new LinkedList<>(
                Arrays.asList(Arrays.copyOfRange(mockCntrPathArr, 1, mockCntrPathArr.length - 1)));
        return new MockConnectorPath(mockCntrPathArr[0], connectorNamesList,
                mockCntrPathArr[mockCntrPathArr.length - 1], mockValue);
    }

    private ConnectorDcl getFinalConnector(ConnectorDcl connectorDcl, Queue<String> connectorNames) {
        Connector con = connectorDcl.getConnector();
        String connectorNameToLookFor = connectorNames.poll();
        if (connectorNameToLookFor == null) {
            //final connector must be a native connector
            if (con instanceof AbstractNativeConnector) {
                return connectorDcl;
            } else {
                throw new BallerinaException(
                        "This cannot be set as a terminal connector - " + connectorDcl.getVarName().getName() + ".");
            }
        }

        if (con instanceof BallerinaConnector) {
            connectorDcl = Arrays.stream(((BallerinaConnector) con).getConnectorDcls())
                    .filter(connector -> connector.getVarName().getName().equals(connectorNameToLookFor)).findAny()
                    .orElse(null);
            if (connectorDcl == null) {
                throw new BallerinaException(COULD_NOT_FIND_MATCHING_CONNECTOR + connectorNameToLookFor);
            }
        } else if (con instanceof AbstractNativeConnector) {
            //todo use reflection to further traverse the tree
            //1) via reflection, get the list of all global variables that are of type Connector
            //2) verify the name I want is there.
            //3) if it is there, then get that instance and recurse further.
            throw new BallerinaException("TODO");
        }

        return getFinalConnector(connectorDcl, connectorNames);
    }

    public <T> void setProperty(T instance, String fieldName, String value)
            throws IllegalAccessException, NoSuchFieldException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        doPrivileged((PrivilegedAction<Object>) () -> {
            field.setAccessible(true);
            return null;
        });

        if (field.getType() == Character.TYPE) {
            field.set(instance, value.charAt(0));
            return;
        }
        if (field.getType() == Short.TYPE) {
            field.set(instance, Short.parseShort(value));
            return;
        }
        if (field.getType() == Integer.TYPE) {
            field.set(instance, Integer.parseInt(value));
            return;
        }
        if (field.getType() == Long.TYPE) {
            field.set(instance, Long.parseLong(value));
            return;
        }
        if (field.getType() == Double.TYPE) {
            field.set(instance, Double.parseDouble(value));
            return;
        }
        if (field.getType() == Float.TYPE) {
            field.set(instance, Float.parseFloat(value));
            return;
        }
        if (field.getType() == Byte.TYPE) {
            field.set(instance, Byte.parseByte(value));
            return;
        }
        if (field.getType() == Boolean.TYPE) {
            field.set(instance, Boolean.parseBoolean(value));
            return;
        }
        field.set(instance, value);
    }

    static class MockConnectorPath {
        String serviceName;
        LinkedList<String> connectorNames;
        String terminalVarName;
        private String mockValue;

        MockConnectorPath(String serviceName, LinkedList<String> connectorNames, String terminalVarName,
                String mockValue) {
            this.serviceName = serviceName;
            this.connectorNames = connectorNames;
            this.terminalVarName = terminalVarName;
            this.mockValue = mockValue;
        }
    }
}
