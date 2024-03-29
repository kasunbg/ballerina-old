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

define(['require', 'jquery', 'log', 'backbone', 'file_browser'], function (require, $, log, Backbone, FileBrowser) {
    var SaveToFileDialog = Backbone.View.extend(
        /** @lends SaveToFileDialog.prototype */
        {
            /**
             * @augments Backbone.View
             * @constructs
             * @class SaveToFileDialog
             * @param {Object} config configuration options for the SaveToFileDialog
             */
            initialize: function (options) {
                this.app = options;
                this.dialog_container = $(_.get(options.config.dialog, 'container'));
                this.notification_container = _.get(options.config.tab_controller.tabs.tab.ballerina_editor.notifications, 'container');
            },

            show: function(){
                var self = this;
                this._fileSaveModal.modal('show').on('shown.bs.modal', function(){
                    self.trigger('loaded');
                });
            },

            setSelectedFile: function(path, fileName){
                this._fileBrowser.select(path);
                if(!_.isNil(this._configNameInput)){
                    this._configNameInput.val(fileName);
                }
            },

            render: function () {
                //TODO : this render method should be rewritten with improved UI
                var fileBrowser;
                var app = this.app;
                var notification_container = this.notification_container;

                if(!_.isNil(this._fileSaveModal)){
                    this._fileSaveModal.remove();
                }

                var fileSave = $(
                    "<div class='modal fade' id='saveConfigModal' tabindex='-1' role='dialog' aria-tydden='true'>" +
                    "<div class='modal-dialog file-dialog' role='document'>" +
                    "<div class='modal-content'>" +
                    "<div class='modal-header'>" +
                    "<button type='button' class='close' data-dismiss='modal' aria-label='Close'>" +
                    "<span aria-hidden='true'>&times;</span>" +
                    "</button>" +
                    "<h4 class='modal-title file-dialog-title' id='newConfigModalLabel'>Ballerina File Save Wizard</h4>" +
                    "<hr class='style1'>"+
                    "</div>" +
                    "<div class='modal-body'>" +
                    "<div class='container-fluid'>" +
                    "<form class='form-horizontal'>" +
                    "<div class='form-group'>" +
                    "<label for='configName' class='col-sm-2 file-dialog-label'>File Name :</label>" +
                    "<div class='col-sm-9'>" +
                    "<input class='file-dialog-form-control' id='configName' placeholder='eg: sample.bal'>" +
                    "</div>" +
                    "</div>" +
                    "<div class='form-group'>" +
                    "<label for='location' class='col-sm-2 file-dialog-label'>Location :</label>" +
                    "<div class='col-sm-9'>" +
                    "<input type='text' class='file-dialog-form-control' id='location' placeholder='eg: /home/user/wso2-integration-server/ballerina-configs'>" +
                    "</div>" +
                    "</div>" +
                    "<div class='form-group'>" +
                    "<div class='file-dialog-form-scrollable-block'>" +
                    "<div id='fileTree'>" +
                    "</div>" +
                    "<div id='file-browser-error' class='alert alert-danger' style='display: none;'>" +
                    "</div>" +
                    "</div>" +
                    "</div>" +
                    "<div class='form-group'>" +
                    "<div class='file-dialog-form-btn'>" +
                    "<button id='saveButton' type='button' class='btn btn-file-dialog'>save" +
                    "</button>" +
                    "<div class='divider'/>" +
                    "<button type='cancelButton' class='btn btn-file-dialog' data-dismiss='modal'>cancel</button>" +
                    "</div>" +
                    "</div>" +
                    "</form>" +
                    "<div id='newWizardError' class='alert alert-danger'>" +
                    "<strong>Error!</strong> Something went wrong." +
                    "</div>" +
                    "</div>" +
                    "</div>" +
                    "</div>" +
                    "</div>" +
                    "</div>"
                );

                var successNotification = $(
                    "<div style='z-index: 9999;' style='line-height: 20%;' class='alert alert-success' id='success-alert'>"+
                    "<span class='notification'>"+
                    "Configuration saved successfully !"+
                    "</span>"+
                    "</div>");

                var errorNotification = $(
                    "<div style='z-index: 9999;' style='line-height: 20%;' class='alert alert-danger' id='error-alert'>"+
                    "<span class='notification'>"+
                    "Error while saving configuration !"+
                    "</span>"+
                    "</div>");


                var saveConfigModal = fileSave.filter("#saveConfigModal");
                var newWizardError = fileSave.find("#newWizardError");
                var location = fileSave.find("input").filter("#location");
                var configName = fileSave.find("input").filter("#configName");

                var treeContainer  = fileSave.find("div").filter("#fileTree")
                fileBrowser = new FileBrowser({container: treeContainer, application:app, fetchFiles:false});

                fileBrowser.render();
                this._fileBrowser = fileBrowser;
                this._configNameInput = configName;

                //Gets the selected location from tree and sets the value as location
                this.listenTo(fileBrowser, 'selected', function (selectedLocation) {
                    if(selectedLocation){
                        location.val(selectedLocation);
                    }
                });

                fileSave.find("button").filter("#saveButton").click(function() {

                    var _location = location.val();
                    var _configName = configName.val();
                    if (_.isEmpty(_location)) {
                        newWizardError.text("Please enter valid file location");
                        newWizardError.show();
                        return;
                    }
                    if (_.isEmpty(_configName)) {
                        newWizardError.text("Please enter valid file name");
                        newWizardError.show();
                        return;
                    }
                    saveConfigModal.modal('hide');
                    saveConfiguration({location: location, configName:configName});
                });

                $(this.dialog_container).append(fileSave);
                newWizardError.hide();
                this._fileSaveModal = fileSave;

                function alertSuccess(){
                    $(notification_container).append(successNotification);
                    successNotification.fadeTo(2000, 200).slideUp(1000, function(){
                        successNotification.slideUp(1000);
                    });
                };

                function alertError(){
                    $(notification_container).append(errorNotification);
                    errorNotification.fadeTo(2000, 200).slideUp(1000, function(){
                        errorNotification.slideUp(1000);
                    });
                };

                function saveConfiguration() {
                    var workspaceServiceURL = "http://localhost:8289/service/workspace";
                    var saveServiceURL = workspaceServiceURL + "/write";
                    var activeTab = app.tabController.activeTab;
                    var ballerinaFileEditor= activeTab.getBallerinaFileEditor();
                    var config = ballerinaFileEditor.generateSource();
                    var payload = "location=" + btoa(location.val()) + "&configName=" + btoa(configName.val()) + "&config=" + (btoa(config));

                    $.ajax({
                        url: saveServiceURL,
                        type: "POST",
                        data: payload,
                        contentType: "text/plain; charset=utf-8",
                        async: false,
                        success: function (data, textStatus, xhr) {
                            if (xhr.status == 200) {
                                activeTab.setTitle(configName.val());
                                activeTab.getFile()
                                            .setPath(location.val())
                                            .setName(configName.val())
                                            .setContent(config)
                                            .setPersisted(true)
                                            .setDirty(false)
                                            .save();
                                if(app.workspaceExplorer.isEmpty()){
                                    app.commandManager.dispatch("open-folder", location.val());
                                    if(!app.workspaceExplorer.isActive()){
                                        app.commandManager.dispatch("toggle-file-explorer");
                                    }
                                }
                                app.breadcrumbController.setPath(location.val(), configName.val());
                                log.debug('file saved successfully')
                            } else {
                                alertError();
                            }
                        },
                        error: function(res, errorCode, error){
                            alertError();
                        }
                    });
                };
            },
        });

    return SaveToFileDialog;
});