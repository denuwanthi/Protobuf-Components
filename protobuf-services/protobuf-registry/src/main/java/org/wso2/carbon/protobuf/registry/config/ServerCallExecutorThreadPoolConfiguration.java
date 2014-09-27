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
public class ServerCallExecutorThreadPoolConfiguration {

    @XmlElement(name = "CorePoolSize", required = true)
    private int corePoolSize;
    
    @XmlElement(name = "MaxPoolSize", required = true)
    private int maxPoolSize;
    
    @XmlElement(name = "MaxPoolTimeout", required = true)
    private int maxPoolTimeout;
    
    @XmlElement(name = "WorkQueueCapacity", required = true)
    private int workQueueCapacity;

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getMaxPoolTimeout() {
		return maxPoolTimeout;
	}

	public void setMaxPoolTimeout(int maxPoolTimeout) {
		this.maxPoolTimeout = maxPoolTimeout;
	}

	public int getWorkQueueCapacity() {
		return workQueueCapacity;
	}

	public void setWorkQueueCapacity(int workQueueCapacity) {
		this.workQueueCapacity = workQueueCapacity;
	}
}
