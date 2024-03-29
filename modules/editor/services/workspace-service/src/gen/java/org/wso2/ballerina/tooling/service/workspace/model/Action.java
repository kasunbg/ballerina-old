/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.ballerina.tooling.service.workspace.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Action
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaMSF4JServerCodegen",
                            date = "2017-01-27T07:45:46.625Z")
public class Action {
    @JsonProperty("name")
    private String name = null;

    @JsonProperty("annotations")
    private List<Annotation> annotations = new ArrayList<Annotation>();

    @JsonProperty("parameters")
    private List<Parameter> parameters = new ArrayList<Parameter>();

    @JsonProperty("returnParams")
    private List<Parameter> returnParams = new ArrayList<Parameter>();

    public Action name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     **/
    @ApiModelProperty(value = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Action annotations(List<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    public Action addAnnotationsItem(Annotation annotationsItem) {
        this.annotations.add(annotationsItem);
        return this;
    }

    /**
     * Get annotations
     *
     * @return annotations
     **/
    @ApiModelProperty(value = "")
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public Action parameters(List<Parameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Action addParametersItem(Parameter parametersItem) {
        this.parameters.add(parametersItem);
        return this;
    }

    /**
     * Get parameters
     *
     * @return parameters
     **/
    @ApiModelProperty(value = "")
    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }


    public Action returnParams(List<Parameter> returnParams) {
        this.returnParams = returnParams;
        return this;
    }

    public Action addReturnParamsItem(Parameter returnParamsItem) {
        this.returnParams.add(returnParamsItem);
        return this;
    }

    /**
     * Get returnParams
     *
     * @return returnParams
     **/
    @ApiModelProperty(value = "")
    public List<Parameter> getReturnParams() {
        return returnParams;
    }

    public void setReturnParams(List<Parameter> returnParams) {
        this.returnParams = returnParams;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Action action = (Action) o;
        return Objects.equals(this.name, action.name) &&
                Objects.equals(this.annotations, action.annotations) &&
                Objects.equals(this.parameters, action.parameters) &&
                Objects.equals(this.returnParams, action.returnParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, annotations, parameters, returnParams);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Action {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    annotations: ").append(toIndentedString(annotations)).append("\n");
        sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
        sb.append("    returnParams: ").append(toIndentedString(returnParams)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

