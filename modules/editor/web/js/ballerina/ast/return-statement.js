/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
define(['lodash', 'log', './statement'], function (_, log, Statement) {

    /**
     * Class for return statement in ballerina.
     * @param expression zero or many expressions for a return statement.
     * @constructor
     */
    var ReturnStatement = function (args) {
        Statement.call(this);
        this._expression = _.get(args, 'expression') || 'expression';
        this.type = "ReturnStatement";
    };

    ReturnStatement.prototype = Object.create(Statement.prototype);
    ReturnStatement.prototype.constructor = ReturnStatement;

    ReturnStatement.prototype.setReturnExpression = function (expression) {
        if (!_.isNil(expression)) {
            this.setAttribute('_expression', expression);
        } else {
            log.error("Cannot set undefined to the return statement.");
        }
    };

    ReturnStatement.prototype.canBeAChildOf = function (node) {
        return this.getFactory().isFunctionDefinition(node) ||
            this.getFactory().isStatement(node);
    };

    ReturnStatement.prototype.getReturnExpression = function () {
        return this._expression;
    };

    /**
     * initialize from json
     * @param jsonNode
     */
    ReturnStatement.prototype.initFromJson = function (jsonNode) {
        var self = this;
        var expression = "";

        for (var itr = 0; itr < jsonNode.children.length; itr++) {
            var childJsonNode = jsonNode.children[itr];
            //TODO : Need to remove this if/else ladder by delegating expression string calculation to child classes
            if (childJsonNode.type == "basic_literal_expression") {
                if(childJsonNode.basic_literal_type == "string") {
                    // Adding double quotes if it is a string.
                    expression += "\"" + childJsonNode.basic_literal_value + "\"";
                } else {
                    expression += childJsonNode.basic_literal_value;
                }
            } else if (childJsonNode.type == "variable_reference_expression") {
                expression += childJsonNode.variable_reference_name;
            } else {
                var child = self.getFactory().createFromJson(childJsonNode);
                child.initFromJson(childJsonNode);
                expression += child.getExpression();
            }

            if (itr !== jsonNode.children.length - 1) {
                expression += " , ";
            }
        }
        this._expression = expression;
    };

    return ReturnStatement;
});