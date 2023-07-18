package fr.mrpiano.hardium.network;

import io.netty.buffer.ByteBuf;

public abstract class WBPacket {
    public abstract void readData(ByteBuf data);

    public abstract void writeData(ByteBuf data);
}
