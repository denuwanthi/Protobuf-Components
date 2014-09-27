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

package org.wso2.carbon.protobuf.listener.internal.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class ProtobufServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		String filename = "WEB-INF/service.proto";
		ServletContext context = getServletContext();
		InputStream inputStream = context.getResourceAsStream(filename);
		InputStreamReader inputStreamReader;
		BufferedReader reader = null;
		PrintWriter out = null;
		if (inputStream != null) {
			try {
				inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
				reader = new BufferedReader(inputStreamReader);
				out = response.getWriter();
				String fileContent;
				while ((fileContent = reader.readLine()) != null) {
					out.println(fileContent);
				}
			} finally {
				if (out != null) {
					out.flush();
					out.close();
				}
				if (reader != null) {
					reader.close();
				}
			}
		}
	}
}
