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

package org.wso2.ballerina.core.model;

import org.wso2.ballerina.core.model.statements.BlockStmt;

/**
 * A {@code BallerinaFunction} is an operation that is executed by a {@code Worker}.
 * <p>
 * The structure of a BallerinaFunction is as follows:
 * <p>
 * [FunctionAnnotations]
 * [public] function FunctionName (((TypeName VariableName)[(, TypeName VariableName)*])?)
 * ((TypeName[(, TypeName)*])?) [throws exception] {
 * ConnectionDeclaration;*
 * VariableDeclaration;*
 * WorkerDeclaration;*
 * Statement;+
 * }
 *
 * @since 0.8.0
 */
public class BallerinaFunction extends PositionAwareNode implements Function, Node {

    // TODO: Rename this to BFunction after M1.
    private SymbolName symbolName;
    private String functionName;
    private Position functionLocation;

    private Annotation[] annotations;
    private Parameter[] parameters;
    private ConnectorDcl[] connectorDcls;
    private VariableDcl[] variableDcls;
    private Worker[] workers;
    private Parameter[] returnParams;
    private BlockStmt functionBody;

    private boolean publicFunc;
    private int stackFrameSize;

    public BallerinaFunction(SymbolName name,
                             Position position,
                             Boolean isPublic,
                             Annotation[] annotations,
                             Parameter[] parameters,
                             Parameter[] returnParams,
                             ConnectorDcl[] connectorDcls,
                             VariableDcl[] variableDcls,
                             Worker[] workers,
                             BlockStmt functionBody) {

        this.symbolName = name;
        this.functionName = symbolName.getName();
        this.functionLocation = position;
        this.publicFunc = isPublic;
        this.annotations = annotations;
        this.parameters = parameters;
        this.returnParams = returnParams;
        this.connectorDcls = connectorDcls;
        this.variableDcls = variableDcls;
        this.workers = workers;
        this.functionBody = functionBody;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return symbolName.getName();
    }

    @Override
    public String getFunctionName() {
        return this.functionName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackageName() {
        return symbolName.getPkgName();
    }

    /**
     * Get the function Identifier.
     *
     * @return function identifier
     */
    public SymbolName getSymbolName() {
        return symbolName;
    }

    @Override
    public void setSymbolName(SymbolName symbolName) {
        this.symbolName = symbolName;
    }

    /**
     * Get all the Annotations associated with a BallerinaFunction.
     *
     * @return list of Annotations
     */
    public Annotation[] getAnnotations() {
        return annotations;
    }

    /**
     * Get list of Arguments associated with the function definition.
     *
     * @return list of Arguments
     */
    public Parameter[] getParameters() {
        return parameters;
    }


    /**
     * Get all Connections declared within the BallerinaFunction scope.
     *
     * @return list of all the Connections belongs to a BallerinaFunction
     */
    public ConnectorDcl[] getConnectorDcls() {
        return connectorDcls;
    }


    /**
     * Get all the variableDcls declared in the scope of BallerinaFunction.
     *
     * @return list of all BallerinaFunction scoped variableDcls
     */
    public VariableDcl[] getVariableDcls() {
        return variableDcls;
    }

    /**
     * Get all the Workers associated with a BallerinaFunction.
     *
     * @return list of Workers
     */
    public Worker[] getWorkers() {
        return workers;
    }

    public Parameter[] getReturnParameters() {
        return returnParams;
    }

    /**
     * Check whether function is public, which means function is visible outside the package.
     *
     * @return whether function is public
     */
    public boolean isPublic() {
        return publicFunc;
    }

    /**
     * Mark function as public.
     */
    public void makePublic() {
        publicFunc = true;
    }

    public int getStackFrameSize() {
        return stackFrameSize;
    }


    public void setStackFrameSize(int stackFrameSize) {
        this.stackFrameSize = stackFrameSize;
    }

    @Override
    public BlockStmt getCallableUnitBody() {
        return this.functionBody;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Position getLocation() {
        return functionLocation;
    }
}
