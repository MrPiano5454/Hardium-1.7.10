package fr.mrpiano.hardium.network;



import fr.mrpiano.hardium.Hardium;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.relauncher.Side;

public class HardiumChannelHandler extends FMLIndexedMessageToMessageCodec<WBPacket> {
    public static final String CHANNEL = "WesterosBlocks";
    public static final byte BLOCKMSG = 0x00;   // Command code for block message

    public HardiumChannelHandler() {
        addDiscriminator(0, BlockMsgPacket.class);
        //addDiscriminator(1, PacketTileState.class);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, WBPacket packet, ByteBuf data) throws Exception {
        packet.writeData(data);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, WBPacket packet) {
        packet.readData(data);
    }
    
    public static void sendBlockMessage(Block blk, EntityPlayerMP player, byte msgid, byte[] data) {
        byte[] blkmsg = new byte[1 + data.length];
        blkmsg[0] = msgid;
        System.arraycopy(data, 0, blkmsg, 1, data.length);
        int blkid = Block.getIdFromBlock(blk);
        BlockMsgPacket pkt = new BlockMsgPacket(blkid, blkmsg);
        sendToPlayer(pkt, player);
        System.out.println("sendBlockMessge(" + blkid + ", " + player.getDisplayName() + "," + msgid);
    }
    
    public static void sendToPlayer(WBPacket packet , EntityPlayerMP player) {
        try {
            Hardium.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET)
                    .set(FMLOutboundHandler.OutboundTarget.PLAYER);
            Hardium.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            Hardium.channels.get(Side.SERVER).writeOutbound(packet);
        } catch (Throwable t) {
            String name = player.getDisplayName();

            if (name == null) {
                name = "<no name>";
            }
            Hardium.log.warning("sentToPlayer \"" + name + "\" error: " + t.getMessage());
        }
    }
    
    /*
    byte[] buf = new byte[3 + 1 + data.length];
    buf[0] = BLOCKMSG;
    buf[1] = (byte) ((blk.blockID >> 8) & 0xFF);
    buf[2] = (byte) (blk.blockID & 0xFF);
    buf[3] = msgid;
    System.arraycopy(data, 0, buf, 4, data.length);
    Packet250CustomPayload pkt = PacketDispatcher.getPacket(CHANNEL, buf);
    player.playerNetServerHandler.sendPacketToPlayer(pkt);
    System.out.println("sendBlockMessge(" + blk.blockID + ", " + player.getEntityName() + "," + msgid);
    */

}
