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

package org.wso2.carbon.protobuf.listener;

import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.protobuf.annotation.ProtobufService;
import org.wso2.carbon.protobuf.listener.internal.ProtobufServiceData;
import org.wso2.carbon.protobuf.listener.internal.ProtobufServletContextListener;
import org.wso2.carbon.protobuf.listener.internal.servlet.ProtobufServlet;
import org.wso2.carbon.protobuf.registry.ProtobufRegistry;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 * This class registers PB services into Binary Services Registry.
 * It will listen for an annotation (@ProtoBufService) and register services when
 * corresponding wars are deployed.
 */
@HandlesTypes({ ProtobufService.class })
public class ProtobufServletContainerInitializer implements ServletContainerInitializer {

	private static final Log log = LogFactory.getLog(ProtobufServletContainerInitializer.class);

	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext)
			throws ServletException {

		if (classes == null || classes.size() == 0) {
			return;
		}
		// adding a listener to remove services when wars are undeployed
		servletContext.addListener(new ProtobufServletContextListener());
		// keeps track of PB services in a PB war
		// Please note that, a PB war can contain many PB services
		List<ProtobufServiceData> serviceList = new ArrayList<ProtobufServiceData>();
		// servlet to display proto files (like WSDL files)
		ServletRegistration.Dynamic dynamic = servletContext.addServlet("ProtoBufServlet",
				ProtobufServlet.class);

		for (Class<?> clazz : classes) {
			// Getting binary service registry
			ProtobufRegistry binaryServiceRegistry = (ProtobufRegistry) PrivilegedCarbonContext
					.getThreadLocalCarbonContext().getOSGiService(ProtobufRegistry.class);
			// Is it a blocking service or not
			boolean blocking = clazz.getAnnotation(ProtobufService.class).blocking();
			Method reflectiveMethod = null;
			Object serviceObj = null;
			String serviceName;
			String serviceType;
			try {
				if (blocking) {
					// getting newReflectiveBlocking method which will return a
					// blocking service
					reflectiveMethod = clazz.getInterfaces()[0].getDeclaringClass().getMethod(
							"newReflectiveBlockingService", clazz.getInterfaces()[0]);
					// Since it is a static method, we pass null
					serviceObj = reflectiveMethod.invoke(null, clazz.newInstance());
					BlockingService blockingService = (BlockingService) serviceObj;
					// register service into Binary Service Registry
					serviceName = binaryServiceRegistry.registerBlockingService(blockingService);
					serviceType = "BlockingService";
					// keeps PB service information in a bean
					// we need these when removing the services from Binary
					// Service Registry
					// we are using these beans instances inside our destroyer
					serviceList.add(new ProtobufServiceData(serviceName, serviceType));
					servletContext.setAttribute("services", serviceList);
					dynamic.addMapping("/");
				} else {
					// getting newReflectiveService which will return a non
					// blocking service
					reflectiveMethod = clazz.getInterfaces()[0].getDeclaringClass().getMethod(
							"newReflectiveService", clazz.getInterfaces()[0]);
					// Since it is a static method, we pass null
					serviceObj = reflectiveMethod.invoke(null, clazz.newInstance());
					Service service = (Service) serviceObj;
					// register service into Binary Service Registry
					serviceName = binaryServiceRegistry.registerService(service);
					serviceType = "NonBlockingService";
					// keeps PB service information in a bean
					// we need these information to remove the service from
					// Binary Service Registry later
					// we are using these bean instances in our destroyer
					serviceList.add(new ProtobufServiceData(serviceName, serviceType));
					servletContext.setAttribute("services", serviceList);
					dynamic.addMapping("/");
				}
			} catch (InvocationTargetException e) {
				String msg = "InvocationTargetException" + e.getLocalizedMessage();
				log.error(msg, e);
			} catch (NoSuchMethodException e) {
				String msg = "NoSuchMethodException" + e.getLocalizedMessage();
				log.error(msg, e);
			} catch (InstantiationException e) {
				String msg = "InstantiationException" + e.getLocalizedMessage();
				log.error(msg, e);
			} catch (IllegalAccessException e) {
				String msg = "IllegalAccessException" + e.getLocalizedMessage();
				log.error(msg, e);
			}
		}
	}
}