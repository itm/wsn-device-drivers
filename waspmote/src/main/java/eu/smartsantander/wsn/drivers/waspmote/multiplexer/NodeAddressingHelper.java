package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.util.HexUtils;
import eu.smartsantander.util.LRUcache;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;

import java.sql.*;

/**
 * @author TLMAT UC
 */
public class NodeAddressingHelper {

	private static final int CACHE_SIZE = 8;

	private static String urlDB = "jdbc:mysql://localhost:3306/smartsantander";

	private static final LRUcache<Integer, MacAddress> macAddressDigiLookupTableCache = new LRUcache<Integer, MacAddress>(
			CACHE_SIZE);
	private static final LRUcache<Integer, MacAddress> macAddress802154LookupTableCache = new LRUcache<Integer, MacAddress>(
			CACHE_SIZE);
	private static final LRUcache<MacAddress, Integer> nodeIDLookupTableCache = new LRUcache<MacAddress, Integer>(
			CACHE_SIZE);

	public static MacAddress getMACAddress(int nodeID, int protocol) {
		LRUcache<Integer, MacAddress> lookupTableCache;
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

		MacAddress macAddress = lookupTableCache.get(nodeID);
		if (macAddress == null) {
			Connection conn = null;
			try {
				conn = createConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(mysqlQuery);
				if (rs.next()) {
					byte[] binaryMacAddress = HexUtils.hexString2ByteArray(rs.getString(1));
					macAddress = new MacAddress(binaryMacAddress);
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

	public static Integer getNodeID(MacAddress macAddress, int protocol) {
		Integer nodeID = nodeIDLookupTableCache.get(macAddress);
		if (nodeID == null) {
			String mysqlQuery;
			switch (protocol) {
			case XBeeFrame.PROTOCOL_802_15_4:
				mysqlQuery = "SELECT nodeID FROM Nodes where mac1='" + macAddress.toString(null) + "';";
				break;
			case XBeeFrame.PROTOCOL_DIGIMESH:
				mysqlQuery = "SELECT nodeID FROM Nodes where mac2='" + macAddress.toString(null) + "';";
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
