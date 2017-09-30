/*
 * Created on 10.11.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gvs.server.socket;

import gvs.server.ConnectionMonitor;
import gvs.server.ModelBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

/**
 * This class is the endpoint for every connection. The acctual communication is
 * completed over this class. The client information will be stored.
 * 
 * @author mkoller
 */
public class ServerConnectionXML extends Thread {

	private ModelBuilder mb = null;
	private Socket client = null;
	private BufferedReader in = null;
	private PrintStream outStream = null;
	private ConnectionMonitor monitor = null;
	private String remoteHost = null;
	private final String SCHEMA = "gvs.xsd";

	// PROTOCOL
	private final String OK = "OK";
	private final String FAILED = "FAILED";
	private final String RESERVEGVS = "reserveGVS";
	private final String RELEASEGVS = "releaseGVS";
	private Logger serverLogger = null;

	/**
	 * Opens the streams
	 * 
	 * @param client
	 */
	public ServerConnectionXML(Socket client) {
		this.client = client;
		// TODO: check Logger relplacement
		// serverLogger = gvs.common.Logger.getInstance().getServerLogger();
		serverLogger = LoggerFactory.getLogger(ServerConnectionXML.class);
		mb = ModelBuilder.getInstance();
		try {
			monitor = ConnectionMonitor.getInstance();
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			outStream = new PrintStream(client.getOutputStream(), true);
			remoteHost = client.getRemoteSocketAddress().toString();
			serverLogger.info("New connection created");
		} catch (Exception e) {
			serverLogger.error("Connection(Socket client): " + e);
		}
	}

	/**
	 * Reads the client commands
	 */
	public void run() {
		String str = "";
		try {
			while ((str = in.readLine()) != null) {
				if (str.equals(RESERVEGVS)) {
					int status = monitor.reserveService(remoteHost);
					if (status >= 0) {
						serverLogger.info("Service reserved: " + remoteHost);
						File dateiServer = new File("input.xml");
						FileWriter schreibeStrom = new FileWriter(dateiServer);
						outStream.println(OK);
						outStream.flush();
						StringBuffer b = new StringBuffer();
						while ((str = in.readLine()) != null) {
							if (str.equals(RELEASEGVS)) {
								monitor.releaseServer(remoteHost);
								serverLogger.info("Service released: " + remoteHost);
								break;
							} else {

								for (int i = 0; i < str.length(); i++) {
									if ((str.charAt(i)) == ';') {
										String output = b.toString();
										output = output.trim();

										schreibeStrom.write(output);
										schreibeStrom.flush();
										schreibeStrom.close();

										buildDocument(dateiServer);

										dateiServer = new File("input.xml");
										schreibeStrom = new FileWriter(dateiServer);
										b = new StringBuffer();
									} else {
										b.append(str.charAt(i));

									}
								}
							}
						}
					} else {
						serverLogger.info("Server busy");
						outStream.println(FAILED);
						outStream.flush();
						in.close();
						outStream.close();
						client.close();
						break;
					}
				}
				if (str.equals(RELEASEGVS)) {
					monitor.releaseServer(remoteHost);
					serverLogger.info("Service released: " + remoteHost);
					break;
				}
			}
			in.close();
			outStream.close();
			client.close();
			monitor.releaseServer(remoteHost);
		} catch (IOException e) {
			System.out.println("Exception" + e.getMessage());
			monitor.releaseServer(remoteHost);
		}

	}

	/**
	 * Checks the recieved data and passes it to the model builder
	 * 
	 * @param pFile
	 */
	public synchronized void buildDocument(File pFile) {
		serverLogger.info("Build document from recieved datas");
		SAXReader reader = new SAXReader();
		try {
			reader.setValidation(true);
			// TODO: store urls separately?
			reader.setFeature("http://apache.org/xml/features/validation/schema", true);
			reader.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", SCHEMA);
			Document document = reader.read(pFile);

			mb.buildModelFromXML(document);
		} catch (Exception e) {
			serverLogger.error("Document invalid");
			serverLogger.error(e.getMessage());
		}
	}
}
