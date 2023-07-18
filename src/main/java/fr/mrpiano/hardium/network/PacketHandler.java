package fr.mrpiano.hardium.network;

import java.io.IOException;


import cpw.mods.fml.common.network.NetworkRegistry;
import fr.mrpiano.hardium.Hardium;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class PacketHandler extends SimpleChannelInboundHandler<WBPacket>  {
    private void onBlockMsg(INetHandler handler, EntityPlayer player, BlockMsgPacket packet) throws IOException {
        Block blk = Block.getBlockById(packet.blockID);
        if ((blk != null) && (blk instanceof HardiumMessageDest)) {
            System.out.println("deliverMessage(" + packet.blockID + ")");
            ((HardiumMessageDest)blk).deliverMessage(handler, player, packet.msgdata);
        }
    }

    @Override
    protected  void channelRead0(ChannelHandlerContext ctx, WBPacket packet) {
        try {
            INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            EntityPlayer player = Hardium.proxy.getPlayerFromNetHandler(netHandler);

            if (packet instanceof BlockMsgPacket) {
                onBlockMsg(netHandler, player, (BlockMsgPacket) packet);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}