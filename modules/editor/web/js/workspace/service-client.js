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
define(['log', 'lodash', 'jquery', 'event_channel', './file', 'alerts'],
    function(log, _, $, EventChannel, File, alerts ) {

        /**
         * @class ServiceClient
         * @param {Object} args
         * @constructor
         */
        var ServiceClient = function (args) {
            this.application = _.get(args, 'application');
        };

        ServiceClient.prototype = Object.create(EventChannel.prototype);
        ServiceClient.prototype.constructor = EventChannel;

        /**
         * validate source
         * @param ServiceClient
         */
        ServiceClient.prototype.parse = function (source) {
            var content = { "content": source };
            var data = {};
            $.ajax({
                type: "POST",
                context: this,
                url: _.get(this.application, 'config.services.parser.endpoint'),
                data: JSON.stringify(content),
                contentType: "application/json; charset=utf-8",
                async: false,
                dataType: "json",
                success: function (response) {
                    data = response;
                },
                error: function(xhr, textStatus, errorThrown){
                    data = {"error":true, "message":"Unable to render design view due to parser errors."};
                }
            });
            return data;
        };

        /**
         * read content of a file
         * @param {String} filePath file path
         */
        ServiceClient.prototype.readFileContent = function (filePath) {
            var data = {};
            $.ajax({
                type: "POST",
                context: this,
                url: _.get(this.application, 'config.services.workspace.endpoint') + "/read",
                data: filePath,
                contentType: "text/plain; charset=utf-8",
                async: false,
                success: function (response) {
                    data = response;
                },
                error: function(xhr, textStatus, errorThrown){
                    data = {"error":true, "message":"Unable to read file " + filePath};
                }
            });
            return data;
        };

        ServiceClient.prototype.readFile = function (filePath) {
           var fileData = this.readFileContent(filePath),
               pathArray = _.split(filePath, this.application.getPathSeperator()),
               fileName = _.last(pathArray),
               folderPath = _.join(_.take(pathArray, pathArray.length -1), this.application.getPathSeperator());

            return new File({
                name: fileName,
                path: folderPath,
                content: fileData.content,
                isPersisted: true,
                isDirty: false
            });
        };

        ServiceClient.prototype.exists = function (path) {
            var data = {};
            $.ajax({
                type: "GET",
                context: this,
                url: _.get(this.application, 'config.services.workspace.endpoint') + "/exists?" + "path=" + btoa(path),
                contentType: "text/plain; charset=utf-8",
                async: false,
                success: function (response) {
                    data = response;
                },
                error: function(xhr, textStatus, errorThrown){
                    data = {"error":true, "message":"Unable to invoke exists file. Status " + textStatus};
                    alerts.error(data.message);
                }
            });
            return data;
        };

        ServiceClient.prototype.create = function (path, type) {
            var data = {};
            $.ajax({
                type: "GET",
                context: this,
                url: _.get(this.application, 'config.services.workspace.endpoint') + "/create?" + "path=" + btoa(path)
                    + "&type=" + btoa(type),
                contentType: "text/plain; charset=utf-8",
                async: false,
                success: function (response) {
                    data = response;
                },
                error: function(xhr, textStatus, errorThrown){
                    data = {"error":true, "message":"Unable to invoke create file/folder. Status " + textStatus};
                    alerts.error(data.message);
                }
            });
            return data;
        };

        ServiceClient.prototype.delete = function (path, type) {
            var data = {};
            $.ajax({
                type: "GET",
                context: this,
                url: _.get(this.application, 'config.services.workspace.endpoint') + "/delete?" + "path=" + btoa(path)
                    + "&type=" + btoa(type),
                contentType: "text/plain; charset=utf-8",
                async: false,
                success: function (response) {
                    data = response;
                },
                error: function(xhr, textStatus, errorThrown){
                    data = {"error":true, "message":"Unable to invoke delete file/folder. Status " + textStatus};
                    alerts.error(data.message);
                }
            });
            return data;
        };

        ServiceClient.prototype.writeFile = function (file) {
            var data = {};
            $.ajax({
                type: "POST",
                context: this,
                url: _.get(this.application, 'config.services.workspace.endpoint') + "/write",
                data: "location=" + btoa(file.getPath()) + "&configName=" + btoa(file.getName()) +
                                                        "&config=" + (btoa(file.getContent())),
                contentType: "text/plain; charset=utf-8",
                async: false,
                success: function (response) {
                    data = response;
                    file.setDirty(false)
                        .save();
                    log.debug("File " + file.getName() + ' saved successfully at '+ file.getPath());
                },
                error: function(xhr, textStatus, errorThrown){
                    data = {"error":true, "message":"Unable to write file " + file.getName() + ' at '+ file.getPath()};
                    alerts.error(data.message);
                }
            });
            return data;
        };

        return ServiceClient;
    });