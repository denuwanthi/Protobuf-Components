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

package org.wso2.carbon.protobuf.registry;

import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;

/*
 * It provides APIs to register and remove services to/from Protobuf Service Registry.
 * 
 * Any class can get an instance of this class from OSGI run time and use it to
 * register/remove services
 */
public interface ProtobufRegistry {
	public String registerBlockingService(BlockingService blockingService);
	public String registerService(Service service);
	public String removeBlockingService(String serviceName);
	public String removeService(String serviceName);
}