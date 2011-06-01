package es.unican.tlmat.wsn.drivers.waspmote.multiplexer;

import es.unican.tlmat.util.ExtendedMacAddress;
import es.unican.tlmat.util.HexUtils;
import es.unican.tlmat.util.LRUcache;
import es.unican.tlmat.wsn.drivers.waspmote.frame.XBeeFrame;

import java.sql.*;

/**
 * @author TLMAT UC
 */
public class NodeAddressingHelper {

	private static final int CACHE_SIZE = 8;

	private static String urlDB = "jdbc:mysql://localhost:3306/smartsantander";

	private static final LRUcache<Integer, ExtendedMacAddress> macAddressDigiLookupTableCache = new LRUcache<Integer, ExtendedMacAddress>(
			CACHE_SIZE);
	private static final LRUcache<Integer, ExtendedMacAddress> macAddress802154LookupTableCache = new LRUcache<Integer, ExtendedMacAddress>(
			CACHE_SIZE);
	private static final LRUcache<ExtendedMacAddress, Integer> nodeIDLookupTableCache = new LRUcache<ExtendedMacAddress, Integer>(
			CACHE_SIZE);

	public static ExtendedMacAddress getMACAddress(int nodeID, int protocol) {
		LRUcache<Integer, ExtendedMacAddress> lookupTableCache;
		String mysqlQuery;
		switch (protocol) {
		case XBeeFrame.PROTOCOL_802_15_4:
			lookupTableCache = macAddress802154LookupTableCache;
			mysqlQuery = "SELECT mac1 FROM Nodes where nodeId=" + nodeID + ";";
			break;
		case XBeeFrame.PROTOCOL_DIGIMESH:
			lookupTableCache = macAddressDigiLookupTableCache;
			mysqlQuery = "SELECT mac2 FROM Nodes where nodeId=" + nodeID + ";";
			break;
		default:
			return null;
		}

		ExtendedMacAddress macAddress = lookupTableCache.get(nodeID);
		if (macAddress == null) {
			Connection conn = null;
			try {
				conn = createConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(mysqlQuery);
				if (rs.next()) {
					byte[] binaryMacAddress = HexUtils.hexString2ByteArray(rs.getString(1));
					macAddress = new ExtendedMacAddress(binaryMacAddress);
					lookupTableCache.put(nodeID, macAddress);
					nodeIDLookupTableCache.put(macAddress, nodeID);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeConnection(conn);
			}
		}

		return macAddress;
	}

	public static Integer getNodeID(ExtendedMacAddress macAddress, int protocol) {
		Integer nodeID = nodeIDLookupTableCache.get(macAddress);
		if (nodeID == null) {
			String mysqlQuery;
			switch (protocol) {
			case XBeeFrame.PROTOCOL_802_15_4:
				mysqlQuery = "SELECT nodeID FROM Nodes where mac1='" + macAddress.toString() + "';";
				break;
			case XBeeFrame.PROTOCOL_DIGIMESH:
				mysqlQuery = "SELECT nodeID FROM Nodes where mac2='" + macAddress.toString() + "';";
				break;
			default:
				return null;
			}

			Connection conn = null;
			try {
				conn = createConnection();

				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(mysqlQuery);
				if (rs.next()) {
					nodeID = Integer.valueOf(rs.getInt(1));
					nodeIDLookupTableCache.put(macAddress, nodeID);
					switch (protocol) {
					case XBeeFrame.PROTOCOL_802_15_4:
						macAddress802154LookupTableCache.put(nodeID, macAddress);
						break;
					case XBeeFrame.PROTOCOL_DIGIMESH:
						macAddressDigiLookupTableCache.put(nodeID, macAddress);
						break;
					default:
						break;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeConnection(conn);
			}
		}
		return nodeID;
	}

	public static void flushCache() {
		macAddress802154LookupTableCache.clear();
		macAddressDigiLookupTableCache.clear();
		nodeIDLookupTableCache.clear();
	}

	private static Connection createConnection() throws SQLException {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(urlDB, "smartsantander", "asdfgh");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return conn;
	}

	private static void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
