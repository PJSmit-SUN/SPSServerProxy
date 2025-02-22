package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAttachPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerEntityAttachPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
    private boolean retry = true;
//    private IServerSession serverSession;

    private ServerEntityAttachPacketBehaviour() {
    }

    public ServerEntityAttachPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityAttachPacket serverEntityAttachPacket = (ServerEntityAttachPacket) packet;

        int x = 0;
        int y = 0;
        int z = 0;

        if (EmulatedClientConnection.isPlayer(serverEntityAttachPacket.getEntityId())) {
            x = (int) EmulatedClientConnection.getXByEntityId(serverEntityAttachPacket.getEntityId());
            y = (int) EmulatedClientConnection.getYByEntityId(serverEntityAttachPacket.getEntityId());
            z = (int) EmulatedClientConnection.getZByEntityId(serverEntityAttachPacket.getEntityId());
        } else if (EntityTracker.isEntity(serverEntityAttachPacket.getEntityId())){
            x = (int) EntityTracker.getXByEntityId(serverEntityAttachPacket.getEntityId());
            y = (int) EntityTracker.getYByEntityId(serverEntityAttachPacket.getEntityId());
            z = (int) EntityTracker.getZByEntityId(serverEntityAttachPacket.getEntityId());
        } else {
            if (retry) {
                new Thread(() -> {
                    try {
                        Thread.sleep(20);
                        process(packet);
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                System.out.println("ServerEntityAttachPacketBehaviour::process => Error: Entity not found for packet: " + packet.getClass().getSimpleName() + " Entity Id: " + serverEntityAttachPacket.getEntityId());
            }
            return;
        }

        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", x, z, 0, "clientBound");
        } else {
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(),(int) emulatedClientConnection.getXPosition(), (int) emulatedClientConnection.getZPosition(), 0, emulatedClientConnection.getUsername());
//            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername());
        }
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
