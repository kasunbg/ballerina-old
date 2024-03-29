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
define(['lodash', 'log', 'event_channel', './abstract-statement-source-gen-visitor', '../../ast/return-statement'], function(_, log, EventChannel, AbstractStatementSourceGenVisitor, ReturnStatement) {

    var ReturnStatementVisitor = function(parent){
        AbstractStatementSourceGenVisitor.call(this, parent);
    };

    ReturnStatementVisitor.prototype = Object.create(AbstractStatementSourceGenVisitor.prototype);
    ReturnStatementVisitor.prototype.constructor = ReturnStatementVisitor;

    ReturnStatementVisitor.prototype.canVisitReturnStatement = function(returnStatement){
        return returnStatement instanceof ReturnStatement;
    };

    ReturnStatementVisitor.prototype.beginVisitReturnStatement = function(returnStatement){
        /**
         * set the configuration start for the reply statement definition language construct
         * If we need to add additional parameters which are dynamically added to the configuration start
         * that particular source generation has to be constructed here
         */
        this.appendSource('return ');
        log.debug('Begin Visit Return Statement Definition');
    };

    ReturnStatementVisitor.prototype.endVisitReturnStatement = function(returnStatement){
        this.appendSource(returnStatement.getReturnExpression() + ";\n");
        this.getParent().appendSource(this.getGeneratedSource());
        log.debug('End Visit Return Statement Definition');
    };

    return ReturnStatementVisitor;
});