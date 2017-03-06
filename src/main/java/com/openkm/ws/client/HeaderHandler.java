/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.ws.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.util.Set;

public class HeaderHandler implements SOAPHandler<SOAPMessageContext> {
	private static Logger log = LoggerFactory.getLogger(HeaderHandler.class);
	private final String URI_WSS_SEC = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	private final String URI_WSS_UTIL = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
	private final String URI_WSS_PASS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText";
	private String username = "";
	private String password = "";

	public HeaderHandler(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public boolean handleMessage(SOAPMessageContext messageContext) {
		Boolean outboundProperty = (Boolean) messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outboundProperty.booleanValue()) {
			SOAPMessage message = messageContext.getMessage();

			try {
				SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
				SOAPHeader header = envelope.addHeader();
				SOAPElement security = header.addChildElement("Security", "wsse", URI_WSS_SEC);
				SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
				usernameToken.addAttribute(new QName("xmlns:wsu"), URI_WSS_UTIL);

				SOAPElement username = usernameToken.addChildElement("Username", "wsse");
				username.addTextNode(this.username);

				SOAPElement password = usernameToken.addChildElement("Password", "wsse");
				password.setAttribute("Type", URI_WSS_PASS);
				password.addTextNode(this.password);

				if (log.isDebugEnabled()) {
					// Print out the outbound SOAP message
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					message.writeTo(baos);
					log.debug(baos.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				if (log.isDebugEnabled()) {
					// This handler does nothing with the response from the Web
					// Service so we just print out the SOAP message.
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					SOAPMessage message = messageContext.getMessage();
					message.writeTo(baos);
					log.debug(baos.toString());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return outboundProperty;
	}

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleFault(SOAPMessageContext messageContext) {
		return true;
	}

	public void close(MessageContext messageContext) {
	}
}
