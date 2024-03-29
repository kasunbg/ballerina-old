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
define(['lodash', 'event_channel', './ballerina-env-factory'], function(_, EventChannel, BallerinaEnvFactory){

    var instance;
    /**
     * @class BallerinaEnvironment
     * @augments EventChannel
     * @param args {Object} - args.package {[PackageDefinition]} list of packages
     * @constructor
     */
    var BallerinaEnvironment = function(args) {
        this._packages = _.get(args, 'packages', []);
        this._types = _.get(args, 'types', ['message', 'connection', 'string', 'boolean', 'int', 'double', 'float', 'long', 'exception', 'json', 'xml', 'map', 'string[]', 'int[]']);
        this.initializePackages();
    };

    BallerinaEnvironment.prototype = Object.create(EventChannel.prototype);
    BallerinaEnvironment.prototype.constructor = BallerinaEnvironment;

    /**
     * Given a name, finds relevant package
     * @param packageName {String} name of the package to find
     * @return {Package}
     */
    BallerinaEnvironment.prototype.findPackage = function (packageName) {
        return _.find(this._packages, function(packageInstance){
            return _.isEqual(packageInstance.getName(), packageName);
        });
    };

    /**
     * Add a package to env
     * @param packageInstance {Package}
     * @fires BallerinaEnvironment#new-package-added
     */
    BallerinaEnvironment.prototype.addPackage = function(packageInstance) {
       if(!(packageInstance instanceof Package)){
            var err = packageInstance + " is not an instance of Package.";
            log.error(err);
            throw err;
       }
       this._packages.push(packageInstance);
        /**
         * @Event BallerinaEnvironment#new-package-added
         */
       this.trigger("new-package-added", packageInstance);
    };

    /**
     * @return {[Package]}
     */
    BallerinaEnvironment.prototype.getPackages = function() {
        return this._packages;
    };

    /**
     * get available types for this environment
     * @returns {*}
     */
    BallerinaEnvironment.prototype.getTypes = function () {
      return this._types;
    };

    /**
     * Initialize packages from BALLERINA_HOME and/or Ballerina Repo
     */
    BallerinaEnvironment.prototype.initializePackages = function () {

        var self = this;

        //TODO : invoke backend service to get packages
        var packagesJson = [{
            name: "ballerina.net.http",
            connectors: [{
                name: "HTTPConnector",
                annotations: [
                    {
                        name: "Description",
                        value: ""
                    }
                ],
                parameters: [
                    {
                        name: "message",
                        type: "message"
                    },
                    {
                        name: "reasonPhrase",
                        type: "string"
                    }
                ],
                actions: [
                    {
                        name: "get",
                        annotations: [
                            {
                                name: "Description",
                                value: ""
                            }
                        ],
                        parameters: [
                            {
                                name: "h",
                                type: "HttpConnector"
                            },
                            {
                                name: "path",
                                type: "string"
                            },
                            {
                                name: "m",
                                type: "message"
                            }
                        ],
                        returnParams: [
                            {
                                type: "message"
                            }
                        ]
                    },
                    {
                        name: "put",
                        annotations: [
                            {
                                name: "Description",
                                value: ""
                            }
                        ],
                        parameters: [
                            {
                                name: "h",
                                type: "HttpConnector"
                            },
                            {
                                name: "path",
                                type: "string"
                            },
                            {
                                name: "m",
                                type: "message"
                            }
                        ],
                        returnParams: [
                            {
                                type: "message"
                            }
                        ]
                    },
                    {
                        name: "post",
                        annotations: [
                            {
                                name: "Description",
                                value: ""
                            }
                        ],
                        parameters: [
                            {
                                name: "h",
                                type: "HttpConnector"
                            },
                            {
                                name: "path",
                                type: "string"
                            },
                            {
                                name: "m",
                                type: "message"
                            }
                        ],
                        returnParams: [
                            {
                                type: "message"
                            }
                        ]
                    },
                    {
                        name: "delete",
                        annotations: [
                            {
                                name: "Description",
                                value: ""
                            }
                        ],
                        parameters: [
                            {
                                name: "h",
                                type: "HttpConnector"
                            },
                            {
                                name: "path",
                                type: "string"
                            },
                            {
                                name: "m",
                                type: "message"
                            }
                        ],
                        returnParams: [
                            {
                                type: "message"
                            }
                        ]
                    }
                ]
            }],
            functions: [
                {
                    name: "setStatusCode",
                    annotations: [
                        {
                            name: "Description",
                            value: ""
                        },
                        {
                            name: "Param",
                            value: "message"
                        },
                        {
                            name: "Param",
                            value: "status code"
                        }
                    ],
                    parameters: [
                        {
                            name: "message",
                            type: "message"
                        },
                        {
                            name: "statusCode",
                            type: "int"
                        }
                    ],
                    returnParams: [
                        {
                            type: "int"
                        }
                    ]
                },
                {
                    name: "getStatusCode",
                    annotations: [
                        {
                            name: "Description",
                            value: ""
                        },
                        {
                            name: "Param",
                            value: "message"
                        },
                        {
                            name: "Return",
                            value: "status code"
                        }
                    ],
                    parameters: [
                        {
                            name: "message",
                            type: "message"
                        }
                    ],
                    returnParams: [
                        {
                            type: "int"
                        }
                    ]
                }
            ],
            structs: []
        }];

        _.each(packagesJson, function (packageNode) {
            var package = BallerinaEnvFactory.createPackage();
            package.initFromJson(packageNode);
            self._packages.push(package);
        });
    };

    BallerinaEnvironment.prototype.searchPackage = function(query, exclude){
        var search_text = query;
        var exclude_packages = exclude;
        var result = _.filter(this._packages, function (package) {
            var existing = _.filter(exclude_packages, function (ex) {
                return package.getName() == ex;
            });
            return (existing.length == 0) && (_.includes(package.getName().toUpperCase(), search_text.toUpperCase()));
        });
        return result;
    };

    return (instance = (instance || new BallerinaEnvironment()));
});