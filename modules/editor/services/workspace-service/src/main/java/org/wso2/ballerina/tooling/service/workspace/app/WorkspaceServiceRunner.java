/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
package org.wso2.ballerina.tooling.service.workspace.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ballerina.tooling.service.workspace.api.PackagesApi;
import org.wso2.ballerina.tooling.service.workspace.Constants;
import org.wso2.ballerina.tooling.service.workspace.rest.FileServer;
import org.wso2.ballerina.tooling.service.workspace.rest.WorkspaceService;
import org.wso2.ballerina.tooling.service.workspace.rest.datamodel.BLangFileRestService;
import org.wso2.msf4j.MicroservicesRunner;

import java.nio.file.Paths;

/**
 * Workspace Service Entry point.
 *
 * @since 0.8.0
 */
public class WorkspaceServiceRunner {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceServiceRunner.class);

    public static void main(String[] args) {
        String balHome = System.getProperty(Constants.SYS_BAL_HOME);
        if (balHome == null) {
            balHome = System.getenv(Constants.SYS_BAL_HOME);
        }
        if (balHome == null) {
            logger.error("BALLERINA_HOME is not set. Please set ballerina.home system variable.");
            return;
        }

        boolean isCloudMode = Boolean.getBoolean(Constants.SYS_WORKSPACE_ENABLE_CLOUD);

//        // configure possible command line options
//        Options options = new Options();
//        Option cloudModeOption = new Option(Constants.CLOUD_MODE_INDICATOR_ARG,
//                Constants.CLOUD_MODE_INDICATOR_ARG_DESC);
//        options.addOption(cloudModeOption);
//        // read console args and process options
//        CommandLineParser parser = new DefaultParser();
//        HelpFormatter formatter = new HelpFormatter();
//        CommandLine commandLine;
//        try {
//            commandLine = parser.parse(options, args);
//            isCloudMode = commandLine.hasOption(Constants.CLOUD_MODE_INDICATOR_ARG);
//            logger.debug(isCloudMode ? "Cloud mode enabled." : "Running in local mode.");
//        } catch (ParseException e) {
//            // not a blocker
//            logger.warn("Exception while parsing console arguments.", e);
//            formatter.printHelp("workspace-service", options);
//        }

        Injector injector = Guice.createInjector(new WorkspaceServiceModule(isCloudMode));
        new MicroservicesRunner(Integer.getInteger(Constants.SYS_WORKSPACE_PORT, Constants.DEFAULT_WORKSPACE_PORT))
                .deploy(injector.getInstance(WorkspaceService.class))
                .deploy(new BLangFileRestService())
                .deploy(new PackagesApi())
                .start();

        int port = Integer.getInteger(Constants.SYS_FILE_WEB_PORT, Constants.DEFAULT_FILE_WEB_PORT);
        String contextRoot = Paths.get(balHome, Constants.FILE_CONTEXT_RESOURCE, Constants
                .FILE_CONTEXT_RESOURCE_EDITOR, Constants.FILE_CONTEXT_RESOURCE_EDITOR_WEB)
                .toString();
        FileServer fileServer = new FileServer();
        fileServer.setContextRoot(contextRoot);
        new MicroservicesRunner(port)
                .deploy(fileServer)
                .start();
        if (!isCloudMode) {
            logger.info("Ballerina Editor URL: http://localhost:" + port);
        }
    }
}