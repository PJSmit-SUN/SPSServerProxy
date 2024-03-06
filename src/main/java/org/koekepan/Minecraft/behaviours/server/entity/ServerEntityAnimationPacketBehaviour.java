package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAnimationPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerEntityAnimationPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerEntityAnimationPacketBehaviour() {
    }

    public ServerEntityAnimationPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityAnimationPacket serverEntityAnimationPacket = (ServerEntityAnimationPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());

        int x = 0;
        int y = 0;
        int z = 0;

        if (EmulatedClientConnection.isPlayer(serverEntityAnimationPacket.getEntityId())) {
            x = (int) EmulatedClientConnection.getXByEntityId(serverEntityAnimationPacket.getEntityId());
            y = (int) EmulatedClientConnection.getYByEntityId(serverEntityAnimationPacket.getEntityId());
            z = (int) EmulatedClientConnection.getZByEntityId(serverEntityAnimationPacket.getEntityId());
        } else if (EntityTracker.isEntity(serverEntityAnimationPacket.getEntityId())){
            x = (int) EntityTracker.getXByEntityId(serverEntityAnimationPacket.getEntityId());
            y = (int) EntityTracker.getYByEntityId(serverEntityAnimationPacket.getEntityId());
            z = (int) EntityTracker.getZByEntityId(serverEntityAnimationPacket.getEntityId());
        } else {
//            Logger.log(this, Logger.Level.ERROR, new String[]{"Entity", "animation", "behaviour"}, "Entity not found: " + serverEntityAnimationPacket.getEntityId());
            return;
        }

        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", x, z, 0, "clientBound");
        } else {
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) x, (int) z, 0, emulatedClientConnection.getUsername());
        }
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
