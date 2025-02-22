package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import java.util.UUID;

public class ServerSpawnMobPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerSpawnMobPacketBehaviour() {
    }

    public ServerSpawnMobPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerSpawnMobPacket serverSpawnMobPacket = (ServerSpawnMobPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());

        double x = serverSpawnMobPacket.getX();
        double y = serverSpawnMobPacket.getY();
        double z = serverSpawnMobPacket.getZ();

        int entityId = serverSpawnMobPacket.getEntityId();
        UUID uuid = serverSpawnMobPacket.getUUID();

        if (!EmulatedClientConnection.isPlayer(entityId)){
            if (EntityTracker.getEntityTrackerByEntityId(entityId) == null) {
                new EntityTracker(x, y, z, entityId, uuid);
//                System.out.println("Spawning Mob with entityID: " + entityId);
            } else {
//                System.out.println("Mob already exists!");
                EntityTracker.moveByEntityId(entityId, x, y, z, (byte) 0);
            }
        }

//        Logger.log(this, Logger.Level.DEBUG, new String[]{"Entity", "spawnEntity", "behaviour"}, "Spawn entity: " + entityId + " :: " + serverSpawnMobPacket.getType().toString());
        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", (int) x, (int) z, 0, "clientBound");
        } else {
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) emulatedClientConnection.getXPosition(), (int) emulatedClientConnection.getZPosition(), 0, emulatedClientConnection.getUsername());

            System.out.println("ServerSpawnMobPacketBehaviour::process => " + emulatedClientConnection.getUsername() + " is spawning mob at: " + x + ", " + y + ", " + z);
//            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername());

        }
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
