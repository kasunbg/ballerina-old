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
define(['log', 'lodash', 'require', 'event_channel', './../ast/service-definition', './../ast/function-definition',
        './../ast/type-definition', './../ast/type-converter-definition', './../ast/constant-definition',
        './../ast/struct-definition'],
    function(log, _, require, EventChannel, ServiceDefinition, FunctionDefinition,
             TypeDefinition, TypeConverterDefinition, ConstantDefinition,
             StructDefinition){

        /**
         * @class Package
         * @augments EventChannel
         * @param args {Object} - args.name: name of the package
         * @constructor
         */
        var Package = function(args){
            this.setName(_.get(args, 'name', ''));
            this.addServiceDefinitions(_.get(args, 'serviceDefinitions', []));
            this.addFunctionDefinitions(_.get(args, 'functionDefinitions', []));
            this._connectors = _.get(args, 'connectors', []);
            this.addTypeDefinitions(_.get(args, 'typeDefinitions', []));
            this.addTypeConverterDefinitions(_.get(args, 'typeConverterDefinitions', []));
            this.addConstantDefinitions(_.get(args, 'constantDefinitions', []));
            this.BallerinaEnvFactory = require('./ballerina-env-factory');
        };

        Package.prototype = Object.create(EventChannel.prototype);
        Package.prototype.constructor = Package;

        Package.prototype.setName = function(name){
            if(!_.isNil(name) && _.isString(name)){
                this._name = name;
            } else {
                log.error("Invalid value for package name: ", name);
            }
        };

        Package.prototype.getName = function(){
            return this._name;
        };

        /**
         * Add constant defs
         * @param constantDefinitions - can be an array of constantDefinitions or a single constantDefinition
         * @fires Package#constant-defs-added
         */
        Package.prototype.addConstantDefinitions = function(constantDefinitions){
            var err;
            if(!_.isArray(constantDefinitions) && !(constantDefinitions instanceof  ConstantDefinition)){
                err = "Adding constant def failed. Not an instance of ConstantDefinition" + constantDefinitions;
                log.error(err);
                throw err;
            }
            if(_.isArray(constantDefinitions)){
                if(!_.isEmpty(constantDefinitions)){
                    _.each(constantDefinitions, function(constantDefinition){
                        if(!(constantDefinition instanceof  ConstantDefinition)){
                            err = "Adding constant def failed. Not an instance of ConstantDefinition" + constantDefinition;
                            log.error(err);
                            throw err;
                        }
                    });
                }
            }
            this._constantDefinitions = this._constantDefinitions || [];
            this._constantDefinitions = _.concat(this._constantDefinitions , constantDefinitions);
            /**
             * fired when new constant defs are added to the package.
             * @event Package#constant-defs-added
             * @type {[ConstantDefinition]}
             */
            this.trigger("constant-defs-added", constantDefinitions);
        };

        /**
         * Set constant defs
         *
         * @param constantDefs
         */
        Package.prototype.setConstantDefinitions = function(constantDefs){
            this._constantDefinitions = null;
            this.addConstantDefinitions(constantDefs);
        };

        /**
         *
         * @returns {[ConstantDefinition]}
         */
        Package.prototype.getConstantDefinitions = function() {
            return this._constantDefinitions;
        };

        /**
         * Add type converter defs
         * @param typeConverterDefinitions - can be an array of typeDefinitions or a single typeDefinition
         * @fires Package#type--converter-defs-added
         */
        Package.prototype.addTypeConverterDefinitions = function(typeConverterDefinitions){
            var err;
            if(!_.isArray(typeConverterDefinitions) && !(typeConverterDefinitions instanceof  TypeConverterDefinition)){
                err = "Adding type converter def failed. Not an instance of TypeConverterDefinition" + typeConverterDefinitions;
                log.error(err);
                throw err;
            }
            if(_.isArray(typeConverterDefinitions)){
                if(!_.isEmpty(typeConverterDefinitions)){
                    _.each(typeConverterDefinitions, function(typeConverterDefinition){
                        if(!(typeConverterDefinition instanceof  TypeConverterDefinition)){
                            err = "Adding type converter def failed. Not an instance of TypeConverterDefinition" + typeConverterDefinition;
                            log.error(err);
                            throw err;
                        }
                    });
                }
            }
            this._typeConverterDefinitions = this._typeConverterDefinitions || [];
            this._typeConverterDefinitions = _.concat(this._typeConverterDefinitions , typeConverterDefinitions);
            /**
             * fired when new type converter defs are added to the package.
             * @event Package#type-converter-defs-added
             * @type {[TypeConverterDefinition]}
             */
            this.trigger("type-converter-defs-added", typeConverterDefinitions);
        };

        /**
         * Set type converter defs
         *
         * @param typeConverterDefs
         */
        Package.prototype.setTypeConverterDefinitions = function(typeConverterDefs){
            this._typeConverterDefinitions = null;
            this.addTypeConverterDefinitions(typeConverterDefs);
        };

        /**
         *
         * @returns {[TypeConverterDefinition]}
         */
        Package.prototype.getTypeConverterDefinitions = function() {
            return this._typeConverterDefinitions;
        };

        /**
         * Add type defs
         * @param typeDefinitions - can be an array of typeDefinitions or a single typeDefinition
         * @fires Package#type-defs-added
         */
        Package.prototype.addTypeDefinitions = function(typeDefinitions){
            var err;
            if(!_.isArray(typeDefinitions) && !(typeDefinitions instanceof  TypeDefinition)){
                err = "Adding type def failed. Not an instance of TypeDefinition" + typeDefinitions;
                log.error(err);
                throw err;
            }
            if(_.isArray(typeDefinitions)){
                if(!_.isEmpty(typeDefinitions)){
                    _.each(typeDefinitions, function(typeDefinition){
                        if(!(typeDefinition instanceof  TypeDefinition)){
                            err = "Adding type def failed. Not an instance of TypeDefinition" + typeDefinition;
                            log.error(err);
                            throw err;
                        }
                    });
                }
            }
            this._typeDefinitions = this._typeDefinitions || [];
            this._typeDefinitions = _.concat(this._typeDefinitions , typeDefinitions);
            /**
             * fired when new type defs are added to the package.
             * @event Package#type-defs-added
             * @type {[TypeDefinition]}
             */
            this.trigger("type-defs-added", typeDefinitions);
        };

        /**
         * Set type defs
         *
         * @param typeDefs
         */
        Package.prototype.setTypeDefinitions = function(typeDefs){
            this._typeDefinitions = null;
            this.addTypeDefinitions(typeDefs);
        };

        /**
         *
         * @returns {[TypeDefinition]}
         */
        Package.prototype.getTypeDefinitions = function() {
            return this._typeDefinitions;
        };

        /**
         * Add connectors
         * @param connectors - can be an array of connectors or a single connector
         * @fires Package#connector-defs-added
         */
        Package.prototype.addConnectors = function(connectors){
            var self = this;
            var err;
            if(!_.isArray(connectors) && !(self.BallerinaEnvFactory.isConnector(connectors))){
                err = "Adding connector failed. Not an instance of connector " + connectors;
                log.error(err);
                throw err;
            }
            if(_.isArray(connectors)){
                if(!_.isEmpty(connectors)){
                    _.each(connectors, function(connector){
                        if(!self.BallerinaEnvFactory.isConnector(connector)){
                            err = "Adding connector failed. Not an instance of connector" + connector;
                            log.error(err);
                            throw err;
                        }
                    });
                }
            }
            this._connectors = _.concat(this._connectors , connectors);
            /**
             * fired when new connectors are added to the package.
             * @event Package#connector-defs-added
             * @type {[Connector]}
             */
            this.trigger("connector-defs-added", connectors);
        };

        /**
         * Get all connectors
         * @returns {[Connector]}
         */
        Package.prototype.getConnectors = function() {
            return this._connectors;
        };

        /**
         * Add service defs
         * @param serviceDefinitions - can be an array of serviceDefs or a single serviceDef
         */
        Package.prototype.addServiceDefinitions = function(serviceDefinitions){
            var err;
            if(!_.isArray(serviceDefinitions) && !(serviceDefinitions instanceof  ServiceDefinition)){
                err = "Adding service def failed. Not an instance of ServiceDefinition" + serviceDefinitions;
                log.error(err);
                throw err;
            }
            if(_.isArray(serviceDefinitions)){
                if(!_.isEmpty(serviceDefinitions)){
                    _.each(serviceDefinitions, function(serviceDefinition){
                        if(!(serviceDefinition instanceof  ServiceDefinition)){
                            err = "Adding service def failed. Not an instance of ServiceDefinition" + serviceDefinition;
                            log.error(err);
                            throw err;
                        }
                    });
                }
            }
            this._serviceDefinitions = this._serviceDefinitions || [];
            this._serviceDefinitions = _.concat(this._serviceDefinitions , serviceDefinitions);
            /**
             * fired when new service defs are added to the package.
             * @event Package#service-defs-added
             * @type {[ServiceDefinition]}
             */
            this.trigger("service-defs-added", serviceDefinitions);
        };

        /**
         * Set service defs
         *
         * @param serviceDefs
         */
        Package.prototype.setServiceDefinitions = function(serviceDefs){
            this._serviceDefinitions = null;
            this.addServiceDefinitions(serviceDefs);
        };

        /**
         *
         * @returns {[ServiceDefinition]}
         */
        Package.prototype.getServiceDefinitions = function() {
            return this._serviceDefinitions;
        };


        /**
         * Add function defs
         * @param functionDefinitions - can be an array of functionDefinitions or a single functionDefinition
         */
        Package.prototype.addFunctionDefinitions = function(functionDefinitions){
            var self = this;
            var err;
            if(!_.isArray(functionDefinitions) && !(self.BallerinaEnvFactory.isFunction(functionDefinitions))){
                err = "Adding function def failed. Not an instance of FunctionDefinition" + functionDefinitions;
                log.error(err);
                throw err;
            }
            if(_.isArray(functionDefinitions)){
                if(!_.isEmpty(functionDefinitions)){
                    _.each(functionDefinitions, function(functionDefinition){
                        if(!(functionDefinition instanceof  FunctionDefinition)){
                            err = "Adding funciton def failed. Not an instance of FunctionDefinition" + functionDefinition;
                            log.error(err);
                            throw err;
                        }
                    });
                }
            }
            this._functionDefinitions = this._functionDefinitions || [];
            this._functionDefinitions = _.concat(this._functionDefinitions , functionDefinitions);
            /**
             * fired when new function defs are added to the package.
             * @event Package#function-defs-added
             * @type {[FunctionDefinition]}
             */
            this.trigger("function-defs-added", functionDefinitions);
        };

        /**
         * Set function defs
         *
         * @param functionDefs
         */
        Package.prototype.setFunctionDefinitions = function(functionDefs){
            this._functionDefinitions = null;
            this.addFunctionDefinitions(functionDefs);
        };

        /**
         *
         * @returns {[FunctionDefinition]}
         */
        Package.prototype.getFunctionDefinitions = function() {
            return this._functionDefinitions;
        };

        /**
         * Add struct definition(s) to the package.
         * @param {StructDefinition[]|StructDefinition} structDefinitions - The struct definition(s).
         */
        Package.prototype.addStructDefinitions = function(structDefinitions){
            var err;
            if(!_.isArray(structDefinitions) && !(structDefinitions instanceof StructDefinition)){
                err = "Adding struct def failed. Not an instance of StructDefinition: " + structDefinitions;
                log.error(err);
                throw err;
            }
            if(_.isArray(structDefinitions)){
                if(!_.isEmpty(structDefinitions)){
                    _.each(structDefinitions, function(structDefinition){
                        if(!(structDefinition instanceof  StructDefinition)){
                            err = "Adding struct def failed. Not an instance of StructDefinition: " + structDefinition;
                            log.error(err);
                            throw err;
                        }
                    });
                }
            }
            this._structDefinitions = this._structDefinitions || [];
            this._structDefinitions = _.concat(this._structDefinitions , structDefinitions);
            /**
             * Fired when new struct defs are added to the package.
             * @event Package#struct-defs-added
             * @type {FunctionDefinition}
             */
            this.trigger("struct-defs-added", structDefinitions);
        };

        /**
         * Set struct definitions.
         *
         * @param {StructDefinition[]} structDefinitions
         */
        Package.prototype.setStructDefinitions = function(structDefinitions){
            this._structDefinitions = null;
            this.addStructDefinitions(structDefinitions);
        };

        /**
         * Gets the struct definitions in the package.
         * @return {StructDefinition[]} - The struct definitions.
         */
        Package.prototype.getStructDefinitions = function() {
            return this._structDefinitions;
        };

        Package.prototype.initFromJson = function(jsonNode) {
            var self = this;
            this.setName(jsonNode.name);

            _.each(jsonNode.connectors, function (connectorNode) {
                var connector = self.BallerinaEnvFactory.createConnector();
                connector.initFromJson(connectorNode);
                self.addConnectors(connector);
            });

            _.each(jsonNode.functions, function(functionNode){
                var functionDef = self.BallerinaEnvFactory.createFunction();
                functionDef.initFromJson(functionNode);
                self.addFunctionDefinitions(functionDef);
            });
        };

        return Package;
    });