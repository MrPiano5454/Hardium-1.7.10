package fr.mrpiano.hardium.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

/**
 * Server side proxy
 */
public class Proxy
{
    public Proxy()
    {
    }
    public void initRenderRegistry() {
    }
    /**
     * This function returns either the player from the handler if it's on the
     * server, or directly from the minecraft instance if it's the client.
     */
    public EntityPlayer getPlayerFromNetHandler (INetHandler handler) {
        if (handler instanceof NetHandlerPlayServer) {
            return ((NetHandlerPlayServer) handler).playerEntity;
        } else {
            return null;
        }
    }
}
