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

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.wso2.carbon.base.CarbonBaseConstants;
import org.wso2.carbon.protobuf.registry.config.exception.ProtobufConfigurationException;

public class ProtobufConfigFactory {

	public static ProtobufConfiguration build() throws ProtobufConfigurationException {

		ProtobufConfiguration protobufConfig = null;
		
		try {
			String pbsXmlLocation = System.getProperty(
					CarbonBaseConstants.CARBON_HOME)+
					File.separator+"repository"+
					File.separator+"conf"+
					File.separator+"etc"+
					File.separator+"protobuf-server.xml";
			File file = new File(pbsXmlLocation);
			JAXBContext context = JAXBContext.newInstance(ProtobufConfiguration.class);
            // validate pbs.xml using the schema
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            StreamSource streamSource = new StreamSource();
            streamSource.setInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("protobuf-server.xsd"));
            Schema schema = sf.newSchema(streamSource);
			Unmarshaller un = context.createUnmarshaller();
            un.setSchema(schema);
			protobufConfig = (ProtobufConfiguration) un.unmarshal(file);
		} catch (Exception e) {
			String msg = "Error while loading cluster configuration file";
			System.out.println(e);
			throw new ProtobufConfigurationException(msg, e);
		}
		
		return protobufConfig;
	}

}
