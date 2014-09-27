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
public class LoggerConfiguration {
	
    @XmlElement(name = "LogReqProto", required = true, type = Boolean.class)
    private boolean isLogReqProtoEnabled;
    
    @XmlElement(name = "LogResProto", required = true, type = Boolean.class)
    private boolean isLogResProtoEnabled;
    
    @XmlElement(name = "LogEventProto", required = true, type = Boolean.class)
    private boolean isLogEventProtoEnabled;

	public boolean isLogReqProtoEnabled() {
		return isLogReqProtoEnabled;
	}

	public void setLogReqProtoEnabled(boolean isLogReqProtoEnabled) {
		this.isLogReqProtoEnabled = isLogReqProtoEnabled;
	}

	public boolean isLogResProtoEnabled() {
		return isLogResProtoEnabled;
	}

	public void setLogResProtoEnabled(boolean isLogResProtoEnabled) {
		this.isLogResProtoEnabled = isLogResProtoEnabled;
	}

	public boolean isLogEventProtoEnabled() {
		return isLogEventProtoEnabled;
	}

	public void setLogEventProtoEnabled(boolean isLogEventProtoEnabled) {
		this.isLogEventProtoEnabled = isLogEventProtoEnabled;
	}
}
