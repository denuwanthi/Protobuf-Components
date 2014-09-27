package org.wso2.carbon.protobuf.listener.internal;

import javax.servlet.ServletContainerInitializer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.wso2.carbon.protobuf.listener.ProtobufServletContainerInitializer;

public class ProtobufListenerActivator implements BundleActivator{
	
	@Override
	public void start(BundleContext bundleContext){
		ProtobufServletContainerInitializer protobufServletContainerInitializer = new ProtobufServletContainerInitializer();
		bundleContext.registerService(ServletContainerInitializer.class.getName(), protobufServletContainerInitializer, null);
	}

	@Override
	public void stop(BundleContext bundleContext){
	}
}
