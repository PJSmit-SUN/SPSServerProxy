//package org.koekepan.herobrineproxy.packet.behaviours.client;
//
//import org.koekepan.herobrineproxy.ConsoleIO;
//import org.koekepan.herobrineproxy.Logger;
//import org.koekepan.herobrineproxy.behaviour.Behaviour;
//import org.koekepan.herobrineproxy.session.IProxySessionNew;
//
//import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
//import com.github.steveice10.packetlib.packet.Packet;
//
//public class ClientLoginStartPacketBehaviour implements Behaviour<Packet>{
//
//	private IProxySessionNew proxySession;
//
//	@SuppressWarnings("unused")
//	private ClientLoginStartPacketBehaviour() {}
//
//
//	public ClientLoginStartPacketBehaviour(IProxySessionNew proxySession) {
//		this.proxySession = proxySession;
//	}
//
//
//	@Override
//	public void process(Packet packet) {
//		LoginStartPacket loginPacket = (LoginStartPacket)packet;
//		String username = loginPacket.getUsername();
//		String serverHost = proxySession.getServerHost();
//		int serverPort = proxySession.getServerPort();
//		Logger.log(this, Logger.Level.DEBUG, new String[]{"behaviour", "serverBound"}, "Setting username of ProxySession <"+proxySession.getClass().getSimpleName()+"> to <" + username + ">");
//
//		proxySession.setUsername(username);
//		Logger.log(this, Logger.Level.DEBUG, new String[]{"behaviour", "serverBound"}, "Player \"" + username + "\" is connecting to <" + serverHost + ":" + serverPort + ">");
//		proxySession.connect(serverHost, serverPort);
//	}
//
//}
