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

import org.wso2.ballerina.core.model.NodeVisitor;
import org.wso2.ballerina.core.model.Position;
import org.wso2.ballerina.core.model.values.BBoolean;
import org.wso2.ballerina.core.model.values.BValueType;

import java.util.function.BiFunction;

import static org.wso2.ballerina.core.model.Operator.AND;

/**
 * {@code AndExpression} represents an boolean AND('&&') expression in Ballerina.
 *
 * @since 0.8.0
 */
public class AndExpression extends BinaryLogicalExpression {

    public static final BiFunction<BValueType, BValueType, BValueType> AND_FUNC =
            (lVal, rVal) -> new BBoolean(lVal.booleanValue() && rVal.booleanValue());

    public AndExpression(Expression lExpr, Expression rExpr, Position location) {
        super(lExpr, AND, rExpr, location);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
