/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.protobuf.registry.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServerConfiguration {
	
    @XmlElement(name = "Host", required = true)
    private String host;

    @XmlElement(name = "Port", required = true)
    private int port;
    
    @XmlElement(name = "EnableSSL", required = true, type = Boolean.class)
    private boolean isSSLEnabled;
    
    @XmlElement(name = "ServerCallExecutorThreadPool", required = true)
    private ServerCallExecutorThreadPoolConfiguration serverCallExecutorThreadPoolConfiguration;
    
    @XmlElement(name = "TimeoutExecutorThreadPool", required = true)
    private TimeoutExecutorThreadPoolConfiguration timeoutExecutorThreadPoolConfiguration;
    
    @XmlElement(name = "TimeoutCheckerThreadPool", required = true)
    private TimeoutCheckerThreadPoolConfiguration timeoutCheckerThreadPoolConfiguration;
    
    @XmlElement(name = "Logger", required = true)
    private LoggerConfiguration loggerConfiguration;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSSLEnabled() {
		return isSSLEnabled;
	}

	public void setSSLEnabled(boolean isSSLEnabled) {
		this.isSSLEnabled = isSSLEnabled;
	}

	public ServerCallExecutorThreadPoolConfiguration getServerCallExecutorThreadPoolConfiguration() {
		return serverCallExecutorThreadPoolConfiguration;
	}

	public void setServerCallExecutorThreadPoolConfiguration(
			ServerCallExecutorThreadPoolConfiguration serverCallExecutorThreadPoolConfiguration) {
		this.serverCallExecutorThreadPoolConfiguration = serverCallExecutorThreadPoolConfiguration;
	}

	public TimeoutExecutorThreadPoolConfiguration getTimeoutExecutorThreadPoolConfiguration() {
		return timeoutExecutorThreadPoolConfiguration;
	}

	public void setTimeoutExecutorThreadPoolConfiguration(
			TimeoutExecutorThreadPoolConfiguration timeoutExecutorThreadPoolConfiguration) {
		this.timeoutExecutorThreadPoolConfiguration = timeoutExecutorThreadPoolConfiguration;
	}

	public TimeoutCheckerThreadPoolConfiguration getTimeoutCheckerThreadPoolConfiguration() {
		return timeoutCheckerThreadPoolConfiguration;
	}

	public void setTimeoutCheckerThreadPoolConfiguration(
			TimeoutCheckerThreadPoolConfiguration timeoutCheckerThreadPoolConfiguration) {
		this.timeoutCheckerThreadPoolConfiguration = timeoutCheckerThreadPoolConfiguration;
	}

	public LoggerConfiguration getLoggerConfiguration() {
		return loggerConfiguration;
	}

	public void setLoggerConfiguration(LoggerConfiguration loggerConfiguration) {
		this.loggerConfiguration = loggerConfiguration;
	}
}
