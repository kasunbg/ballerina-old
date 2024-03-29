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

define(['jquery', 'backbone', 'lodash', 'log','./variable-tree', './tools', './frames'], function ($, Backbone, _, log, VariableTree, Tools, Frames) {
    var Debugger = Backbone.View.extend({
        initialize: function(config) {
            var errMsg;
            var self = this;
            this._breakPoints = {};
            if (!_.has(config, 'container')) {
                errMsg = 'unable to find configuration for container';
                log.error(errMsg);
                throw errMsg;
            }
            var container = $(_.get(config, 'container'));
            // check whether container element exists in dom
            if (!container.length > 0) {
                errMsg = 'unable to find container for debugger with selector: ' + _.get(config, 'container');
                log.error(errMsg);
                throw errMsg;
            }
            this._$parent_el = container;

            if (!_.has(config, 'application')) {
                log.error('Cannot init debugger. config: application not found.')
            }

            this.application = _.get(config, 'application');
            this._options = config;
            this.debuggerServiceUrl = _.get(this._options, 'application.config.services.debugger.endpoint');
            this._lastWidth = undefined;
            this._verticalSeparator = $(_.get(this._options, 'separator'));
            this._containerToAdjust = $(_.get(this._options, 'containerToAdjust'));
            
            // register command
            this.application.commandManager.registerCommand(config.command.id, {shortcuts: config.command.shortcuts});
            this.application.commandManager.registerHandler(config.command.id, this.toggleDebugger, this);

        },
        isActive: function(){
            return this._activateBtn.parent('li').hasClass('active');
        },
        toggleDebugger: function () {
            if(this.isActive()){
                this._$parent_el.parent().width('0px');
                this._containerToAdjust.css('margin-left', _.get(this._options, 'leftOffset'));
                this._verticalSeparator.css('left', _.get(this._options, 'leftOffset') - _.get(this._options, 'separatorOffset'));
                this._activateBtn.parent('li').removeClass('active');

            } else {
                this._activateBtn.tab('show');
                var width = this._lastWidth || _.get(this._options, 'defaultWidth');
                this._$parent_el.parent().width(width);
                this._containerToAdjust.css('margin-left', width + _.get(this._options, 'leftOffset'));
                this._verticalSeparator.css('left',  width + _.get(this._options, 'leftOffset') - _.get(this._options, 'separatorOffset'));
            }
        },
        render: function() {
            var self = this;
            var activateBtn = $(_.get(this._options, 'activateBtn'));
            this._activateBtn = activateBtn;

            this.renderContent();
            activateBtn.on('show.bs.tab', function (e) {
                self._isActive = true;
                var width = self._lastWidth || _.get(self._options, 'defaultWidth');
                self._$parent_el.parent().width(width);
                self._containerToAdjust.css('margin-left', width + _.get(self._options, 'leftOffset'));
                self._verticalSeparator.css('left',  width + _.get(self._options, 'leftOffset') - _.get(self._options, 'separatorOffset'));
            });

            activateBtn.on('hide.bs.tab', function (e) {
                self._isActive = false;
            });

            activateBtn.on('click', function(e){
                e.preventDefault();
                e.stopPropagation();
                self.application.commandManager.dispatch(_.get(self._options, 'command.id'));
            });

            if (this.application.isRunningOnMacOS()) {
                activateBtn.attr("title", "Debugger (" + _.get(self._options, 'command.shortcuts.mac.label') + ") ")
            } else {
                activateBtn.attr("title", "Debugger  (" + _.get(self._options, 'command.shortcuts.other.label') + ") ")
            }

            this._verticalSeparator.on('drag', function(event){
                if( event.originalEvent.clientX >= _.get(self._options, 'resizeLimits.minX')
                    && event.originalEvent.clientX <= _.get(self._options, 'resizeLimits.maxX')){
                    self._verticalSeparator.css('left', event.originalEvent.clientX - _.get(self._options, 'separatorOffset'));
                    self._verticalSeparator.css('cursor', 'ew-resize');
                    var newWidth = event.originalEvent.clientX -  _.get(self._options, 'leftOffset');
                    self._$parent_el.parent().width(newWidth);
                    self._containerToAdjust.css('margin-left', event.originalEvent.clientX);
                    self._lastWidth = newWidth;
                    self._isActive = true;
                }
                event.preventDefault();
                event.stopPropagation();
            });

            return this;

        },
        renderContent: function () {
            var debuggerContainer = $('<div></div>');
            debuggerContainer.addClass(_.get(this._options, 'cssClass.container'));
            debuggerContainer.attr('id', _.get(this._options, ('containerId')));
            this._$parent_el.append(debuggerContainer);

            Tools.render(debuggerContainer);
            VariableTree.render(debuggerContainer);
            Frames.render(debuggerContainer);

            this._debuggerContainer = debuggerContainer;
            debuggerContainer.mCustomScrollbar({
                theme: "minimal",
                scrollInertia: 0
            });
        },
        addBreakPoint: function (row, file) {
            var fileId = file.getFile().id;
            this._breakPoints[fileId] = this._breakPoints[fileId] || [];
            this._breakPoints[fileId].push(row);
            this.channel.updateBreakPoints(fileId, this._breakPoints[fileId]);
        }
    });

    return Debugger;
});

