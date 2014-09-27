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
public class TransportConfiguration {

    @XmlElement(name = "Acceptors", required = true)
    private AcceptorsConfiguration acceptorsConfiguration;
    
    @XmlElement(name = "ChannelHandlers", required = true)
    private ChannelHandlersConfiguration channelHandlersConfiguration;
    
    @XmlElement(name = "TCPNoDelay", required = true, type = Boolean.class)
    private boolean isTCPNoDelay;

	public AcceptorsConfiguration getAcceptorsConfiguration() {
		return acceptorsConfiguration;
	}

	public void setAcceptorsConfiguration(
			AcceptorsConfiguration acceptorsConfiguration) {
		this.acceptorsConfiguration = acceptorsConfiguration;
	}

	public ChannelHandlersConfiguration getChannelHandlersConfiguration() {
		return channelHandlersConfiguration;
	}

	public void setChannelHandlersConfiguration(
			ChannelHandlersConfiguration channelHandlersConfiguration) {
		this.channelHandlersConfiguration = channelHandlersConfiguration;
	}

	public boolean isTCPNoDelay() {
		return isTCPNoDelay;
	}

	public void setTCPNoDelay(boolean isTCPNoDelay) {
		this.isTCPNoDelay = isTCPNoDelay;
	}
}
