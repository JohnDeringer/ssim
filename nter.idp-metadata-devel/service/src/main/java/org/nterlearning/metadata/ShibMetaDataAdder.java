/*
 * National Training and Education Resource (NTER)
 * Copyright (C) 2012 SRI International
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.nterlearning.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Iterator;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Stateless
@WebService(endpointInterface = "org.nterlearning.metadata.MetaDataAdder")
public class ShibMetaDataAdder implements MetaDataAdder {
	private static final String ID_ATT = "entityID";
	private static final String ENTITY_TAG = "md:EntityDescriptor";

	Logger log = Logger.getLogger(ShibMetaDataAdder.class);

	private String configFileLocation;
	private static XPath xpath;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nter.metadata.MetaDataAdder#addMetaData(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean addMetaData(String content) {
		boolean retVal = false;
		FileLock fl = null;

		try {
			if (content != null && !content.isEmpty()) {
				log.info("Adding SP metadata to " + configFileLocation);

				// First we need to parse the content string into a DOM document
				Document frag = parseDocument(content);

				// Now grab the base of the content node and add it to the
				// config
				// file
				File configFile = new File(this.configFileLocation);
				RandomAccessFile raf = new RandomAccessFile(configFile, "rw");
				FileChannel fc = raf.getChannel();
				fl = fc.lock();

				if (fl != null && fl.isValid()) {

					Document doc = openConfigDocument(configFile);
					XPath xpath = getXPath();

					Node newEntity = null;
					Node entities = null;
					// find the entity tag in the new content
					newEntity = (Node) xpath.evaluate("//" + ENTITY_TAG, frag,
							XPathConstants.NODE);

					// Find the first METADATA_PROVIDER tag
					// NOTE: This not working for some reason, can just do
					// "getDocumentElement"
					// entities = (Node) xpath.evaluate("/"
					// + ENTITIES_DESCRIPTOR_TAG, doc, XPathConstants.NODE);
					entities = (Node) doc.getDocumentElement();

					if (entities != null) {

						// create a sub element to add
						// Element e = createNewEntityElement(doc, sp_hostname);

						if (newEntity != null) {
							// add the element in there
							Node res = entities.appendChild(doc.importNode(
									newEntity, true));

							if (res != null) {

								// write the doc out to file
								save(doc);

								retVal = true;
							} else {
								log.error("Couldn't append new SP metadata node");
							}
						} else {
							log.error("Couldn't locate SP metadata node in:"
									+ content);
						}
					} else {
						log.error("Couldn't parse metadata config file:"
								+ configFileLocation);
					}
					
				} else {
					log.warn("Invalid SP metadata content received: " + content);
				}
			}

		} catch (FileNotFoundException e) {
			log.error("Couldn't locate config file:" + this.configFileLocation,
					e);
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (fl != null && fl.isValid()) {
				try {
					fl.release();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}

		return retVal;
	}

	/**
	 * saves a DOM Document to disk
	 * 
	 * @param doc
	 *            - The DOM document to write out
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	private void save(Document doc) throws TransformerConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {

		// create a new DOM source
		DOMSource ds = new DOMSource(doc);

		// Create a new file (or overwrite it)
		File file = new File(this.configFileLocation);

		Result result = new StreamResult(file.toURI().getPath());

		// load up the XSLT stylesheet
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer xformer = factory.newTransformer();

		xformer.setOutputProperty(OutputKeys.METHOD, "xml");
		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// xformer.setOutputProperty("http://xml.apache.org/xsltd;indent-amount",
		// "4");
		xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		// Transform the XML to the file
		xformer.transform(ds, result);

	}

	public void setConfigFileLocation(String configFileLocation) {
		this.configFileLocation = configFileLocation;
	}

	public String getConfigFileLocation() {
		return configFileLocation;
	}

	@Override
	public boolean removeMetaData(String hostname) {
		// remove the element and force the file to be written out
		return removeMetaDataElement(hostname, true);

	}

	/**
	 * Remove a metadata element for a given hostname
	 * 
	 * @param hostname
	 * @param save_results
	 *            indicates if updated results are written back to file. For
	 *            tests only!
	 * @return
	 */

	public boolean removeMetaDataElement(String hostname, boolean save_results) {

		boolean retVal = false;
		FileLock fl = null;

		try {
			if (hostname != null && !hostname.isEmpty()) {
				// get the DOM document from disk, make sure to lock it too
				File configFile = new File(this.configFileLocation);
				RandomAccessFile raf = new RandomAccessFile(configFile, "rw");
				FileChannel fc = raf.getChannel();
				fl = fc.lock();

				if (fl != null && fl.isValid()) {

					Document doc = openConfigDocument(configFile);

					XPath xpath = getXPath();

					String query = "//" + ENTITY_TAG
					+ "[matches(@" + ID_ATT + ",'^.*" + hostname + ".*$')]";
					
					// find the tag with the right hostname attribute
//					Node metadataNode = (Node) xpath.evaluate("//" + ENTITY_TAG
//							+ "[matches(@" + ID_ATT + ",'" + hostname + "']", doc,
//							XPathConstants.NODE);

					Node metadataNode = (Node) xpath.evaluate(query, doc,XPathConstants.NODE);
					
					if (metadataNode != null) {
						// remove the node
						// System.out.println("Found node, removing it");
						metadataNode.getParentNode().removeChild(metadataNode);
						// save the document if indicated
						if (save_results) {
							save(doc);
						}
						retVal = true;
					} else {
						log.warn("Unable to locate SP metadata node for remove request of host:"
								+ hostname);
					}
				}
			}

		} catch (Exception e) {
			log.error(e);
		} finally {
			if (fl != null && fl.isValid()) {
				try {
					fl.release();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}

		return retVal;
	}

	private XPath getXPath() {
		if (xpath == null) {
			//XPathFactory factory = XPathFactory.newInstance();
			XPathFactory factory = new net.sf.saxon.xpath.XPathFactoryImpl();
			xpath = factory.newXPath();
			xpath.setNamespaceContext(new MetaNamespaceContext());
		}
		return xpath;
	}

	/**
	 * open the config document
	 * 
	 * @return a DOM document reference
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document openConfigDocument(File f)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder;
		builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(f);

		return doc;
	}

	/**
	 * Parses an XML document from a string
	 * 
	 * @param content
	 *            String containting the XML content
	 * @return a DOM document
	 */
	private Document parseDocument(String content) {
		Document retVal = null;

		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			retVal = builder.parse(new InputSource(new StringReader(content)));

		} catch (ParserConfigurationException e) {
			log.error(e);
			
		} catch (SAXException e) {
			log.error(e);
			
		} catch (IOException e) {
			log.error(e);
		}

		return retVal;
	}

	/**
	 * @author fritter63 helper class for the namespace mapping
	 * 
	 */
	public class MetaNamespaceContext implements NamespaceContext {
		private HashMap<String, String> hm = new HashMap<String, String>();
		{

			// hm.put("rp", "urn:mace:shibboleth:2.0:relying-party");
			// hm.put("shibmd", "urn:mace:shibboleth:2.0:metadata");
			// hm.put("saml", "urn:mace:shibboleth:2.0:relying-party:saml");
			// hm.put("resource", "urn:mace:shibboleth:2.0:resource");
			// hm.put("security", "urn:mace:shibboleth:2.0:security");
			// hm.put("samlsec", "urn:mace:shibboleth:2.0:security:saml");
			hm.put("md", "urn:oasis:names:tc:SAML:2.0:metadata");
			hm.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			hm.put("xml", XMLConstants.NULL_NS_URI);

		};

		public String getNamespaceURI(String prefix) {

			String retVal = hm.get(prefix);

			if (retVal == null)
				retVal = XMLConstants.NULL_NS_URI;

			return retVal;
		}

		// Dummy implementation - not used!
		public Iterator<String> getPrefixes(String val) {
			return null;
		}

		// Dummy implemenation - not used!
		public String getPrefix(String uri) {
			return null;
		}
	}

	@Override
	public boolean ping() {
		log.info("Ping received");

		return true;
	};
}
