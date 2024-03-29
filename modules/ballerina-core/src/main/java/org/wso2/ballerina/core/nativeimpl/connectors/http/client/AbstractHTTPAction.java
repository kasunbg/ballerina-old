/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ballerina.core.nativeimpl.connectors.http.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ballerina.core.exception.BallerinaException;
import org.wso2.ballerina.core.interpreter.Context;
import org.wso2.ballerina.core.model.Connector;
import org.wso2.ballerina.core.model.values.BMessage;
import org.wso2.ballerina.core.model.values.BValue;
import org.wso2.ballerina.core.nativeimpl.connectors.AbstractNativeAction;
import org.wso2.ballerina.core.nativeimpl.connectors.BalConnectorCallback;
import org.wso2.ballerina.core.nativeimpl.connectors.http.Constants;
import org.wso2.ballerina.core.runtime.internal.ServiceContextHolder;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.DefaultCarbonMessage;
import org.wso2.carbon.messaging.Headers;
import org.wso2.carbon.messaging.MessageDataSource;
import org.wso2.carbon.messaging.MessageProcessorException;

import java.net.MalformedURLException;
import java.net.URL;

import static org.wso2.ballerina.core.runtime.Constants.BALLERINA_VERSION;

/**
 * {@code AbstractHTTPAction} is the base class for all HTTP Connector Actions.
 */
public abstract class AbstractHTTPAction extends AbstractNativeAction {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHTTPAction.class);

    private static final String BALLERINA_USER_AGENT;

    static {
        String version = System.getProperty(BALLERINA_VERSION);
        if (version != null) {
            BALLERINA_USER_AGENT = "ballerina/" + version;
        } else {
            BALLERINA_USER_AGENT = "ballerina";
        }
    }

    protected void prepareRequest(Connector connector, String path, CarbonMessage cMsg) {

        // Handle operations for empty content messages initiated from the Ballerina core itself
        if (cMsg instanceof DefaultCarbonMessage && cMsg.isEmpty() && cMsg.getMessageDataSource() == null) {
            cMsg.setEndOfMsgAdded(true);
        }

        String uri = null;
        try {
            uri = ((HTTPConnector) connector).getServiceUri() + path;

            URL url = new URL(uri);
            String host = url.getHost();
            int port = 80;
            if (url.getPort() != -1) {
                port = url.getPort();
            } else if (url.getProtocol().equalsIgnoreCase(Constants.PROTOCOL_HTTPS)) {
                port = 443;
            }

            cMsg.setProperty(Constants.HOST, host);
            cMsg.setProperty(Constants.PORT, port);
            String toPath = url.getPath();
            String query = url.getQuery();
            if (query != null) {
                toPath = toPath + "?" + query;
            }
            cMsg.setProperty(Constants.TO, toPath);

            cMsg.setProperty(Constants.PROTOCOL, url.getProtocol());
            if (port != 80) {
                cMsg.getHeaders().set(Constants.HOST, host + ":" + port);
            } else {
                cMsg.getHeaders().set(Constants.HOST, host);
            }

            //Set User-Agent Header
            Object headerObj = cMsg.getProperty(org.wso2.ballerina.core.runtime.Constants.INTERMEDIATE_HEADERS);

            if (headerObj == null) {
                headerObj = new Headers();
                cMsg.setProperty(org.wso2.ballerina.core.runtime.Constants.INTERMEDIATE_HEADERS, headerObj);
            }
            Headers headers = (Headers) headerObj;

            if (!headers.contains(Constants.USER_AGENT_HEADER)) { // If User-Agent is not already set from program
                cMsg.setHeader(Constants.USER_AGENT_HEADER, BALLERINA_USER_AGENT);
            }

        } catch (MalformedURLException e) {
            throw new BallerinaException("Malformed url specified. " + e.getMessage());
        } catch (Throwable t) {
            throw new BallerinaException("Failed to prepare request. " + t.getMessage());
        }

    }

    protected BValue executeAction(Context context, CarbonMessage message) {

        try {
            BalConnectorCallback balConnectorCallback = new BalConnectorCallback(context);
            // Handle the message built scenario
            if (message.isAlreadyRead()) {
                MessageDataSource messageDataSource = message.getMessageDataSource();
                if (messageDataSource != null) {
                    messageDataSource.serializeData();
                    message.setEndOfMsgAdded(true);
                    message.getHeaders().remove(Constants.HTTP_CONTENT_LENGTH);
                    message.getHeaders()
                            .set(Constants.HTTP_CONTENT_LENGTH, String.valueOf(message.getFullMessageLength()));

                } else {
                    message.setEndOfMsgAdded(true);
                    logger.debug("Sending an empty message");
                }
            }
            ServiceContextHolder.getInstance().getSender().send(message, balConnectorCallback);

            while (!balConnectorCallback.isResponseArrived()) {
                synchronized (context) {
                    if (!balConnectorCallback.isResponseArrived()) {
                        logger.debug("Waiting for a response");
                        context.wait();
                    }
                }
            }
            handleTransportException(balConnectorCallback.getValueRef());
            return balConnectorCallback.getValueRef();
        } catch (MessageProcessorException e) {
            throw new BallerinaException("Failed to send the message to an endpoint ", context);
        } catch (InterruptedException ignore) {
        } catch (Throwable e) {
            throw new BallerinaException(e.getMessage(), context);
        }
        return null;
    }

    private void handleTransportException(BValue valueRef) {
        if (valueRef instanceof BMessage) {
            BMessage bMsg = (BMessage) valueRef;
            if (bMsg.value() == null) {
                throw new BallerinaException("Received unknown message for the action invocation");
            }
            if (bMsg.value().getMessagingException() != null) {
                throw new BallerinaException(bMsg.value().getMessagingException().getMessage());
            }
        } else {
            throw new BallerinaException("Invalid message received for the action invocation");
        }
    }
}
