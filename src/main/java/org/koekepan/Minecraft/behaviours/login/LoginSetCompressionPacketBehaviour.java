package org.koekepan.herobrineproxy.packet.behaviours.login;

import com.github.steveice10.packetlib.packet.Packet;


import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.Logger;
import org.koekepan.herobrineproxy.behaviour.Behaviour;

public class LoginSetCompressionPacketBehaviour implements Behaviour<Packet> {
		
	public LoginSetCompressionPacketBehaviour() {}
	
	
	@Override
	public void process(Packet packet) {
		Logger.log(this, Logger.Level.DEBUG, new String[]{"network", "connection", "initialisation"}, "LoginSetCompressionPacketBehaviour::process => Received setCompressionPacket");
	}
}