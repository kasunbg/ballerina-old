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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Connector
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaMSF4JServerCodegen", date = "2017-01-27T07:45:46.625Z")
public class Connector   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("annotations")
  private List<Annotation> annotations = new ArrayList<Annotation>();

  @JsonProperty("parameters")
  private List<Parameter> parameters = new ArrayList<Parameter>();

  @JsonProperty("actions")
  private List<Action> actions = new ArrayList<Action>();

  public Connector name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(value = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Connector annotations(List<Annotation> annotations) {
    this.annotations = annotations;
    return this;
  }

  public Connector addAnnotationsItem(Annotation annotationsItem) {
    this.annotations.add(annotationsItem);
    return this;
  }

   /**
   * Get annotations
   * @return annotations
  **/
  @ApiModelProperty(value = "")
  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(List<Annotation> annotations) {
    this.annotations = annotations;
  }

  public Connector parameters(List<Parameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public Connector addParametersItem(Parameter parametersItem) {
    this.parameters.add(parametersItem);
    return this;
  }

   /**
   * Get parameters
   * @return parameters
  **/
  @ApiModelProperty(value = "")
  public List<Parameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<Parameter> parameters) {
    this.parameters = parameters;
  }

  public Connector actions(List<Action> actions) {
    this.actions = actions;
    return this;
  }

  public Connector addActionsItem(Action actionsItem) {
    this.actions.add(actionsItem);
    return this;
  }

   /**
   * Get actions
   * @return actions
  **/
  @ApiModelProperty(value = "")
  public List<Action> getActions() {
    return actions;
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Connector connector = (Connector) o;
    return Objects.equals(this.name, connector.name) &&
        Objects.equals(this.annotations, connector.annotations) &&
        Objects.equals(this.parameters, connector.parameters) &&
        Objects.equals(this.actions, connector.actions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, annotations, parameters, actions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Connector {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    annotations: ").append(toIndentedString(annotations)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("    actions: ").append(toIndentedString(actions)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

