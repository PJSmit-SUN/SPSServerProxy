package org.koekepan.VAST.Connection.PacketSenderRunnables;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import org.koekepan.Performance.PacketCapture;
import org.koekepan.VAST.Connection.PacketSender;
import org.koekepan.VAST.Connection.VastConnection;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import java.util.concurrent.ConcurrentHashMap;

public class ServerSender implements Runnable{

    private PacketSender packetSender;
    private VastConnection vastConnection;

    private String username;

    public ServerSender(PacketSender packetSender, VastConnection vastConnection) {
        this.packetSender = packetSender;
        this.vastConnection = vastConnection;
    }

    public void setUsername(String username) { // TODO: remove
        this.username = username;
    }

    private int queueNumberClientBound = 0;

    @Override
    public void run() {
        long timeAdded = System.currentTimeMillis();
        try {
            while (!packetSender.clientboundPacketQueue.isEmpty()) {
                boolean dosleep = true;
                try {
//                    if ((username.equals("ProxyListener2"))){
////                        System.out.println("<" + username + "> ServerSender.run: packetSender.clientboundPacketQueue.size() = " + packetSender.clientboundPacketQueue.size() + " and queueNumberClientBound = " + queueNumberClientBound + " and queueNumberClientboundLast = " + packetSender.queueNumberClientboundLast);
//                        PacketWrapper wrapper = PacketWrapper.getPacketWrapperByQueueNumber(packetSender.clientboundPacketQueue, queueNumberClientBound);
//                        if (wrapper.getPacket() instanceof ServerEntityHeadLookPacket || wrapper.getPacket() instanceof ServerEntityPositionPacket) {
//                            String message = "PacketID: " + wrapper.unique_id + " Processed: " + wrapper.isProcessed;
//                            PacketCapture.log(username, message, PacketCapture.LogCategory.TEST);
//                        }
//                    }
                    if (packetSender.clientboundPacketQueue.containsKey(queueNumberClientBound)) {
                        PacketWrapper wrapper = null;
                        try {
                            wrapper = PacketWrapper.getPacketWrapperByQueueNumber(packetSender.clientboundPacketQueue, queueNumberClientBound);
                        } catch (Exception e) {
                            System.out.println("Error getting PacketWrapper: " + e.getMessage());
                        }

                        if (wrapper != null && wrapper.isProcessed) {
                            SPSPacket spsPacket2 = null;
                            try {
                                spsPacket2 = wrapper.getSPSPacket();
                            } catch (Exception e) {
                                System.out.println("Error getting SPSPacket: " + e.getMessage());
                            }

                            try {
                                assert spsPacket2 != null;

                                vastConnection.publish(spsPacket2);
//                                System.out.println("ServerSender.run: <PUBLISHED> (clientbound) Wrapper is: " + wrapper.getPacket().getClass().getSimpleName() + " at x: " + spsPacket2.x + " y: " + spsPacket2.y);
                                if (spsPacket2.packet.getClass().getSimpleName().equals("ServerPlayerPositionRotationPacket")) {
                                    System.out.println("Sleeping for a second");
                                    Thread.sleep(250);
                                    System.out.println("Waking up");
                                }
                            } catch (Exception e) {
                                System.out.println("Error publishing packet: <" + wrapper.getPacket().getClass().getSimpleName() + ">: " + e.getMessage());
                                packetSender.removePacket(wrapper.getPacket());
                            }
                            try {

                                packetSender.removePacket(wrapper.getPacket());
                            } catch (Exception e) {

                                System.out.println("Error removing packet: " + e.getMessage());
                            }
                            try {
                                timeAdded = System.currentTimeMillis(); // Reset time after sending a packet
                                queueNumberClientBound++;
                                dosleep = false;
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }
                    }

                    // Handle timeout for both queues
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - timeAdded > 100) { // TODO: Change back to 100 when problem found (could be 50)
                        if (packetSender.clientboundPacketQueue.containsKey(queueNumberClientBound)) {
                            PacketWrapper wrapper = null;
                            try {
                                wrapper = PacketWrapper.getPacketWrapperByQueueNumber(packetSender.clientboundPacketQueue, queueNumberClientBound);
                            } catch (Exception e) {
                                System.out.println("Error getting PacketWrapper: " + e.getMessage());
                            }

                            if (wrapper != null && !wrapper.isProcessed) {
                                try {
//                                    if (!(wrapper.getPacket().getClass().getSimpleName().equals("ServerChunkDataPacket"))) {
                                        System.out.println("ServerSender.run: <TIMED OUT> (clientbound) Wrapper is: " + wrapper.getPacket().getClass().getSimpleName() + " and isProcessed: " + wrapper.isProcessed);
                                        PacketCapture.log(wrapper.getPacket().getClass().getSimpleName() + "_" + PacketWrapper.get_unique_id(wrapper.getPacket()), PacketCapture.LogCategory.DELETED_PACKETS_TIME);
//
                                        packetSender.removePacket(wrapper.getPacket());
                                        queueNumberClientBound++;
                                        dosleep = false;
                                        timeAdded = currentTime; // Reset time after handling timeouts
//                                    }
                                } catch (Exception e) {
                                    System.out.println("Error removing packet: " + e.getMessage());
                                }
                            } else {
                                System.out.println("ServerSender.run: <TIMED OUT> (clientbound) Wrapper is null or for some reason I am here but processed could be true");
//                                queueNumberClientBound++;
//                                timeAdded = currentTime; // Reset time after handling timeouts
                            }
                        } else {
//                            timeAdded = currentTime;
                        }
                    }

                    if (!packetSender.clientboundPacketQueue.containsKey(queueNumberClientBound) && queueNumberClientBound < packetSender.queueNumberClientboundLast) {
//                        while (!packetSender.clientboundPacketQueue.containsKey(queueNumberClientBound)) {
                            queueNumberClientBound++;
                            dosleep = false;
                            timeAdded = currentTime; // Reset time after handling timeouts
                            // Check if queueNumberClientBound has reached or exceeded the last queue number
//                            if (queueNumberClientBound > packetSender.queueNumberClientboundLast) {
//                                break; // Exit the loop if we have reached the end of the queue
//                            }
//                        }
                    }

                } catch (Exception e) {
                    System.out.println("Error in main loop: " + e.getMessage());
                }

                if (dosleep) {
                    Thread.sleep(1);
                }
            }
        } catch (Exception e) {
            System.out.println("ServerSender.run: Exception: " + e.getMessage());
        }
    }

}
