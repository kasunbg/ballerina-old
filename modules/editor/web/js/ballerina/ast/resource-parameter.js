/**
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
define(['lodash', './argument'], function (_, Argument) {

    /**
     * AST node for a resource parameter.
     * @param {Object} args - Arguments for creating the resource parameter.
     * @param {string} args.type - Ballerina type of the parameter. Example: string, int.
     * @param {string} args.identifier - Identifier of the parameter.
     * @param {string} args.annotationType - The annotation of the parameter. Example: @PathParam.
     * @param {string} args.annotationText - The text within the annotation.
     * @constructor
     * @augments Argument
     */
    var ResourceParameter = function (args) {
        Argument.call(this, args);
        this.annotationType = _.get(args, "annotationType");
        this.annotationText = _.get(args, "annotationText");
        this.type = "ResourceParameter";
    };

    ResourceParameter.prototype = Object.create(Argument.prototype);
    ResourceParameter.prototype.constructor = ResourceParameter;

    ResourceParameter.prototype.setAnnotationType = function (annotationType) {
        this.setAttribute('annotationType', annotationType);
    };

    ResourceParameter.prototype.getAnnotationType = function () {
        return this.annotationType;
    };

    ResourceParameter.prototype.setAnnotationText = function (annotationText) {
        this.setAttribute('annotationText', annotationText);
    };

    ResourceParameter.prototype.getAnnotationText = function () {
        return this.annotationText;
    };

    /**
     * Gets a string representation of the current parameter.
     * @return {string} - String representation.
     */
    ResourceParameter.prototype.getParameterAsString = function() {
        var paramAsString = !_.isUndefined(this.getAnnotationType()) ? this.getAnnotationType() : "";
        paramAsString += !_.isUndefined(this.getAnnotationText()) && !_.isEmpty(this.getAnnotationText())
            ? "(\"" + this.getAnnotationText() + "\")" : "";
        paramAsString += " " + this.getType() + " ";
        paramAsString += this.getIdentifier();

        return paramAsString;
    };

    /**
     * Gets the supported annotations for a path param.
     * @return {string[]} - The supported annotations for a resource param.
     * @static
     */
    ResourceParameter.getSupportedAnnotations = function() {
        return ["@PathParam", "@QueryParam"/*, "@HeaderParam", "@FormParam", "@Body"*/];
    };

    /**
     * initialize from json
     * @param jsonNode
     */
    ResourceParameter.prototype.initFromJson = function (jsonNode) {
        // TODO : Fix
        this.annotate = jsonNode.annotate;
        Object.getPrototypeOf(this.constructor.prototype).initFromJson.call(this, jsonNode, 0);
    };

    return ResourceParameter;

});