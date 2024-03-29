/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.ballerina.core.model.expressions;

import org.wso2.ballerina.core.model.NodeExecutor;
import org.wso2.ballerina.core.model.Operator;
import org.wso2.ballerina.core.model.Position;
import org.wso2.ballerina.core.model.values.BValue;
import org.wso2.ballerina.core.model.values.BValueType;

import java.util.function.BiFunction;

/**
 * {@code BinaryExpression} represents a binary expression.
 *
 * @see BinaryCompareExpression
 * @see BinaryArithmeticExpression
 * @see BinaryLogicalExpression
 * @see BinaryEqualityExpression
 * @since 0.8.0
 */
public class BinaryExpression extends UnaryExpression {

    protected Expression lExpr;
    protected BiFunction<BValueType, BValueType, BValueType> evalFuncNewNew;

    public BinaryExpression(Expression lExpr, Operator op, Expression rExpr, Position location) {
        super(op, rExpr, location);
        this.lExpr = lExpr;
    }

    public Expression getLExpr() {
        return lExpr;
    }

    public BiFunction<BValueType, BValueType, BValueType> getEvalFunc() {
        return evalFuncNewNew;
    }

    public void setEvalFunc(BiFunction<BValueType, BValueType, BValueType> evalFuncNewNew) {
        this.evalFuncNewNew = evalFuncNewNew;
    }

    public BValue execute(NodeExecutor executor) {
        return executor.visit(this);
    }

    public void setRExpr(Expression rExpr) {
        this.rExpr = rExpr;
    }

}
