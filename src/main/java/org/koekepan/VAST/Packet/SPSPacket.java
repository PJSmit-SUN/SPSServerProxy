package org.koekepan.VAST.Packet;

import com.github.steveice10.packetlib.packet.Packet;

public class SPSPacket {
    public String username;
    public int x;
    public int y;
    public int radius;
    public String channel;
    public Packet packet;



    public SPSPacket(Packet packet, String username, String channel) {


//        this.username = username;
        this.username = username + "&" + PacketWrapper.get_unique_id(packet);
        this.channel = channel;
        this.packet = packet;
//		this.x = 3;
//		this.y = 0;
//		this.radius = 1;
    }


    public SPSPacket(Packet packet, String username, int x, int y, int radius, String channel) {
        this(packet, username, channel);
        this.x = x;
        this.y = y;
        this.radius = radius;
    }


}