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

package org.wso2.carbon.protobuf.registry.internal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.protobuf.registry.ProtobufRegistry;
import org.wso2.carbon.protobuf.registry.config.AcceptorsConfiguration;
import org.wso2.carbon.protobuf.registry.config.ChannelHandlersConfiguration;
import org.wso2.carbon.protobuf.registry.config.LoggerConfiguration;
import org.wso2.carbon.protobuf.registry.config.ProtobufConfigFactory;
import org.wso2.carbon.protobuf.registry.config.ProtobufConfiguration;
import org.wso2.carbon.protobuf.registry.config.ServerCallExecutorThreadPoolConfiguration;
import org.wso2.carbon.protobuf.registry.config.TimeoutCheckerThreadPoolConfiguration;
import org.wso2.carbon.protobuf.registry.config.TimeoutExecutorThreadPoolConfiguration;
import org.wso2.carbon.protobuf.registry.config.TransportConfiguration;
import org.wso2.carbon.protobuf.registry.config.exception.ProtobufConfigurationException;

import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.RpcSSLContext;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.logging.CategoryPerServiceLogger;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerPipelineFactory;
import com.googlecode.protobuf.pro.duplex.timeout.RpcTimeoutChecker;
import com.googlecode.protobuf.pro.duplex.timeout.RpcTimeoutExecutor;
import com.googlecode.protobuf.pro.duplex.timeout.TimeoutChecker;
import com.googlecode.protobuf.pro.duplex.timeout.TimeoutExecutor;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;

/*
 * This class starts an RPC server and register its registry as an OSGI service
 * for binary services.
 * 
 * It reads configuration information from pbs xml which should be placed
 * inside AS's components/repository/lib directory.
 */
public class ProtobufRegistryActivator implements BundleActivator {

	private static final Logger log = LoggerFactory.getLogger(ProtobufRegistry.class);
	private DuplexTcpServerPipelineFactory serverFactory;

	public void start(BundleContext bundleContext) {
		
		log.info("/////////////////////////////////////");

		// load protobuf server configurations from pbs xml
		ProtobufConfiguration configuration = null;
		try {
			configuration = ProtobufConfigFactory.build();
		} catch (ProtobufConfigurationException e) {
			String msg = "Error while loading pbs xml file " + e.getLocalizedMessage();
			log.error(msg ,e);
			return;
		}

		if (!configuration.isEnabled()) {
			log.debug("ProtobufServer is not enabled in pbs xml");
			return;
		}

		log.info("Starting ProtobufServer...");
		
		// gathering configurations into local variables
		ServerConfiguration carbonConfig = ServerConfiguration.getInstance();
		org.wso2.carbon.protobuf.registry.config.ServerConfiguration serverConfig = configuration.getServerConfiguration();
		ServerCallExecutorThreadPoolConfiguration callExecutorConfig = serverConfig.getServerCallExecutorThreadPoolConfiguration();
		TimeoutExecutorThreadPoolConfiguration timeoutExecutorConfig = serverConfig.getTimeoutExecutorThreadPoolConfiguration();
		TimeoutCheckerThreadPoolConfiguration timeoutCheckerConfig = serverConfig.getTimeoutCheckerThreadPoolConfiguration();
		LoggerConfiguration loggerConfig = serverConfig.getLoggerConfiguration();
		TransportConfiguration transportConfig = configuration.getTransportConfiguration();
		AcceptorsConfiguration acceptorsConfig = transportConfig.getAcceptorsConfiguration();
		ChannelHandlersConfiguration channelHandlersConfig = transportConfig.getChannelHandlersConfiguration();
		
		String hostName = carbonConfig.getFirstProperty("HostName");
		int port = serverConfig.getPort();
		int portOffset = Integer.parseInt(carbonConfig.getFirstProperty("Ports.Offset"));
		int effectivePort = port + portOffset;
		// server information
		PeerInfo serverInfo = new PeerInfo(hostName, effectivePort);

		int callExecutorCorePoolSize = callExecutorConfig.getCorePoolSize();
		int callExecutorMaxPoolSize = callExecutorConfig.getMaxPoolSize();
		int callExecutorMaxPoolTimeout = callExecutorConfig.getMaxPoolTimeout();
		int callExecutorWorkQueueCapacity = callExecutorConfig.getWorkQueueCapacity();
		// call executor
		RpcServerCallExecutor callExecutor = new ThreadPoolCallExecutor(
				callExecutorCorePoolSize, 
				callExecutorMaxPoolSize, 
				callExecutorMaxPoolTimeout, 
				TimeUnit.SECONDS, 
				new LinkedBlockingQueue<Runnable>(callExecutorWorkQueueCapacity),
				Executors.defaultThreadFactory());

		serverFactory = new DuplexTcpServerPipelineFactory(serverInfo);
		serverFactory.setRpcServerCallExecutor(callExecutor);

		// if SSL encryption is enabled
		if (serverConfig.isSSLEnabled()) {
			//read keystore and truststore from carbon
			String keystorePassword = carbonConfig.getFirstProperty("Security.KeyStore.Password");
			String keystorePath = carbonConfig.getFirstProperty("Security.KeyStore.Location");
			String truststorePassword = carbonConfig.getFirstProperty("Security.TrustStore.Password");
			String truststorePath = carbonConfig.getFirstProperty("Security.TrustStore.Location");
			RpcSSLContext sslCtx = new RpcSSLContext();
			sslCtx.setKeystorePassword(keystorePassword);
			sslCtx.setKeystorePath(keystorePath);
			sslCtx.setTruststorePassword(truststorePassword);
			sslCtx.setTruststorePath(truststorePath);
			try {
				sslCtx.init();
			} catch (Exception e) {
				String msg = "Couldn't create SSL Context : " + e.getLocalizedMessage();
				log.error(msg, e);
				return;
			}
			serverFactory.setSslContext(sslCtx);
		}
		
		// Timeout Executor
		int timeoutExecutorCorePoolSize = timeoutExecutorConfig.getCorePoolSize();
		int timeoutExecutorMaxPoolSize = timeoutExecutorConfig.getMaxPoolSize();
		int timeoutExecutorKeepAliveTime = timeoutExecutorConfig.getKeepAliveTime();
		BlockingQueue<Runnable> timeoutExecutorWorkQueue = new ArrayBlockingQueue<Runnable>(timeoutExecutorCorePoolSize, false);
		ThreadFactory timeoutExecutorTF = new RenamingThreadFactoryProxy("timeout", Executors.defaultThreadFactory());
		RpcTimeoutExecutor timeoutExecutor = new TimeoutExecutor(
				timeoutExecutorCorePoolSize, 
				timeoutExecutorMaxPoolSize, 
				timeoutExecutorKeepAliveTime, 
				TimeUnit.SECONDS, 
				timeoutExecutorWorkQueue, 
				timeoutExecutorTF);
		
		// Timeout Checker
		int timeoutCheckerSleepTimeMs = timeoutCheckerConfig.getPeriod();
		int timeoutCheckerCorePoolSize = timeoutCheckerConfig.getCorePoolSize();
		ThreadFactory timeoutCheckerTF = new RenamingThreadFactoryProxy("check", Executors.defaultThreadFactory());
		RpcTimeoutChecker timeoutChecker = new TimeoutChecker(
				timeoutCheckerSleepTimeMs, 
				timeoutCheckerCorePoolSize, 
				timeoutCheckerTF);
		timeoutChecker.setTimeoutExecutor(timeoutExecutor);
		timeoutChecker.startChecking(serverFactory.getRpcClientRegistry());
		
		// setup a RPC event listener - it just logs what happens
		RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
		RpcConnectionEventListener listener = new RpcConnectionEventListener() {
			@Override
			public void connectionReestablished(RpcClientChannel clientChannel) {
				log.info("Protobuf connection Reestablished " + clientChannel);
			}

			@Override
			public void connectionOpened(RpcClientChannel clientChannel) {
				log.info("Protobuf connection Opened " + clientChannel);
			}

			@Override
			public void connectionLost(RpcClientChannel clientChannel) {
				log.info("Protobuf connection Lost " + clientChannel);
			}

			@Override
			public void connectionChanged(RpcClientChannel clientChannel) {
				log.info("Protobuf connection Changed " + clientChannel);
			}
		};
		rpcEventNotifier.setEventListener(listener);
		serverFactory.registerConnectionEventListener(rpcEventNotifier);

		// ProtobufServer Logger
		boolean isLogReq = loggerConfig.isLogReqProtoEnabled();
		boolean isLogRes = loggerConfig.isLogResProtoEnabled();
		boolean isLogEve = loggerConfig.isLogEventProtoEnabled();
		CategoryPerServiceLogger logger = new CategoryPerServiceLogger();
		logger.setLogRequestProto(isLogReq);
		logger.setLogResponseProto(isLogRes);
		logger.setLogEventProto(isLogEve);
		
		if(isLogReq || isLogRes || isLogEve) {
			serverFactory.setLogger(logger);
		} else {
			serverFactory.setLogger(null);
		}

		// Call acceptors parameters
		int acceptorsPoolSize = acceptorsConfig.getPoolSize();
		int acceptorsSendBufferSize = acceptorsConfig.getSendBufferSize();
		int acceptorsReceiverBufferSize = acceptorsConfig.getReceiverBufferSize();
		// Channel handlers parameters
		int channelHandlersPoolSize = channelHandlersConfig.getPoolSize();
		int channelHandlersSendBufferSize = channelHandlersConfig.getSendBufferSize();
		int channelHandlersReceiverBufferSize = channelHandlersConfig.getReceiverBufferSize();
		// enable nagle's algorithm or not
		boolean tcpNoDelay = transportConfig.isTCPNoDelay();
		
		// boss and worker thread factories
		ThreadFactory bossTF = new RenamingThreadFactoryProxy("boss", Executors.defaultThreadFactory());
		NioEventLoopGroup boss = new NioEventLoopGroup(acceptorsPoolSize, bossTF);
		ThreadFactory workersTF = new RenamingThreadFactoryProxy("worker", Executors.defaultThreadFactory());
		NioEventLoopGroup workers = new NioEventLoopGroup(channelHandlersPoolSize, workersTF);
		
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, workers);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_SNDBUF, acceptorsSendBufferSize);
		bootstrap.option(ChannelOption.SO_RCVBUF, acceptorsReceiverBufferSize);
		bootstrap.childOption(ChannelOption.SO_RCVBUF, channelHandlersReceiverBufferSize);
		bootstrap.childOption(ChannelOption.SO_SNDBUF, channelHandlersSendBufferSize);
		bootstrap.option(ChannelOption.TCP_NODELAY, tcpNoDelay);
		bootstrap.childHandler(serverFactory);
		bootstrap.localAddress(serverInfo.getPort());

		// To release resources on shutdown
		CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
		shutdownHandler.addResource(boss);
		shutdownHandler.addResource(workers);
		shutdownHandler.addResource(callExecutor);
		shutdownHandler.addResource(timeoutChecker);
		shutdownHandler.addResource(timeoutExecutor);

		// Bind and start to accept incoming connections.
		bootstrap.bind();
		log.info("ProtobufServer Serving " + serverInfo);
		// Register ProtobufServer Registry as an OSGi service
		ProtobufRegistry pbsRegistry = new ProtobufRegistryImpl(serverFactory);
		bundleContext.registerService(ProtobufRegistry.class.getName(), pbsRegistry, null);
	}

	public void stop(BundleContext bundleContext) {
		log.info("Shutting down ProtobufServer...");
	}
}
