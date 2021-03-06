package network;

import java.net.*;

import core.Main;
import misc.Debug;
import menu.NetworkingMenu;
import menu.event.Event;
import misc.compress.Compressor;

public class NetworkDevice
{
	public static final short PORT = 4208;
	private DatagramSocket socket;
	private NetworkingMenu menu; // when packet was received, menu.handlePacket(...) will take care of the packet

	public NetworkDevice()
	{
		try
		{
			socket = new DatagramSocket(PORT); // create socket
		} catch (Exception e)
		{
			Debug.error("can't create socket");
		}
	}

	public void send(Packet packet, InetAddress ip) // send packet to ip
	{
		Debug.warnIf(packet == null, "NetworkDevice.send(): packet is null");
		Debug.warnIf(ip == null, "NetworkDevice.send(): ip is null");
		byte[] data = Compressor.compress(packet); // serialize packet -> data
		DatagramPacket datagramPacket = new DatagramPacket(data, data.length, ip, PORT); // create datagramPacket
		try
		{
			socket.send(datagramPacket); // send
		} catch (Exception e)
		{
			Debug.error("NetworkDevice.send(): Failed to send " + datagramPacket + " to IP " + ip.getHostName());
		}

		Debug.note("sent " + packet + " to IP " + ip.getHostName() + "; size = " + data.length + " bytes", Debug.Tags.NETWORK);
	}

	public void receive() // called by Main.run() permanently, receives packets and gives them to menu.handlePacket(...)
	{
		byte[] data = new byte[Packet.MAX_SIZE]; // create byte[] data
		DatagramPacket datagramPacket = new DatagramPacket(data, data.length); // create DatagramPacket
		
		try
		{
			socket.receive(datagramPacket); // receive packet
		} catch (Exception e)
		{
			Debug.error("NetworkDevice.receive(): Failed to receive packet from IP " + datagramPacket.getAddress().getHostName());
		}

		Packet packet = (Packet) Compressor.decompress(data);
		Debug.note("rcvd " + packet + " from IP " + datagramPacket.getAddress().getHostName() + "; size = " + data.length + " bytes", Debug.Tags.NETWORK);

		if (menu != null) // if there is a target menu
		{
			menu.receivePacket(packet, datagramPacket.getAddress()); // let it handle the packet
		}
	}

	public void setMenu(NetworkingMenu menu) { this.menu = menu; } // set target menu
}
