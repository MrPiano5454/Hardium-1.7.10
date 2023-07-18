package fr.mrpiano.hardium;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import fr.mrpiano.hardium.asm.ClassTransformer;
import fr.mrpiano.hardium.network.HardiumChannelHandler;
import fr.mrpiano.hardium.network.PacketHandler;
import fr.mrpiano.hardium.render.WCFenceRenderer;
import fr.mrpiano.hardium.render.WCLadderRenderer;
import fr.mrpiano.hardium.render.WCFluidCTMRenderer;
import fr.mrpiano.hardium.render.WCCuboidRenderer;
import fr.mrpiano.hardium.render.WCHalfDoorRenderer;
import fr.mrpiano.hardium.render.WCCuboidNSEWUDRenderer;
import fr.mrpiano.hardium.render.WCStairRenderer;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.world.biome.BiomeGenBase;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import fr.mrpiano.hardium.proxy.Proxy;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
@Mod(modid = "hardium", name = "Hardium", version = Version.VER)
public class Hardium
{
    public static Logger log = Logger.getLogger("Hardium");

    // The instance of your mod that Forge uses.
    @Instance("Hardium")
    public static Hardium instance;

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide = "fr.mrpiano.hardium.proxy.ClientProxy", serverSide = "fr.mrpiano.hardium.proxy.Proxy")
    public static Proxy proxy;

    // Dynmap support

    // Block classes
    public static Block customBlocks[];
    public static HashMap<String, Block> customBlocksByName;
    // Custom renders
    public static int fenceRenderID;
    public static int ladderRenderID;
    public static int halfdoorRenderID;
    public static int cuboidRenderID;
    public static int cuboidNSEWUDRenderID;
    public static int stairRenderID;
    public static int fluidCTMRenderID;
    // Use stair render fix
    public boolean useFixedStairs = false;
    public boolean useFixedPressurePlate = false;
    public boolean useWaterCTMFix = false;
    public boolean snowInTaiga = false;

    public static BitSet slabStyleLightingBlocks = new BitSet();

    public static HardiumConfig customConfig;

    public static HardiumBlockDef[] customBlockDefs;

    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    public static Block findBlockByName(String blkname) {
        Block blk = customBlocksByName.get(blkname);
        if (blk != null) {
            return blk;
        }
        try {
            return Block.getBlockFromName(blkname);
        } catch (NumberFormatException nfx) {
        }
        return null;
    }

    public boolean good_init = false;

    public static void crash(Exception x, String msg) {
        CrashReport crashreport = CrashReport.makeCrashReport(x, msg);
        throw new ReportedException(crashreport);
    }
    public static void crash(String msg) {
        crash(new Exception(), msg);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // Initialize
        HardiumBlockDef.initialize();
        HardiumCreativeTab.init();

        // Read our block definition resource
        InputStream in = getClass().getResourceAsStream("/WesterosBlocks.json");
        if (in == null) {
            crash("WesterosBlocks couldn't find its block definition resource");
            return;
        }
        InputStreamReader rdr = new InputStreamReader(in);
        Gson gson = new Gson();
        try {
            customConfig = gson.fromJson(rdr, HardiumConfig.class);
            customBlockDefs = customConfig.blocks;
        } catch (JsonSyntaxException iox) {
            crash(iox, "WesterosBlocks couldn't parse its block definition");
            return;
        } catch (JsonIOException iox) {
            crash(iox, "WesterosBlocks couldn't read its block definition");
            return;
        } finally {
            if (in != null) { try { in.close(); } catch (IOException iox) {}; in = null; }
        }
        log.info("Loaded " + customBlockDefs.length + " block definitions");

        if (HardiumBlockDef.sanityCheck(customBlockDefs) == false) {
            crash("WesterosBlocks.json failed sanity check");
            return;
        }

        // Load configuration file - use suggested (config/WesterosBlocks.cfg)
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        try
        {
            cfg.load();
            useFixedStairs = cfg.get("Settings",  "useFixedStairs", true).getBoolean(true);
            useFixedPressurePlate = cfg.get("Settings", "useFixedPressurePlate", true).getBoolean(true);
            useWaterCTMFix = cfg.get("Settings", "useWaterCTMFix", true).getBoolean(true);
            snowInTaiga = cfg.get("Settings", "snowInTaiga", true).getBoolean(true);
            good_init = true;
        }
        catch (Exception e)
        {
            crash(e, "WesterosBlocks couldn't load its configuration");
        }
        finally
        {
            cfg.save();
        }
        // Initialize with standard block IDs
        slabStyleLightingBlocks.clear();
        /**NOTYET
         slabStyleLightingBlocks.set(Block.stoneSingleSlab.blockID);
         slabStyleLightingBlocks.set(Block.woodSingleSlab.blockID);
         slabStyleLightingBlocks.set(Block.tilledField.blockID);
         slabStyleLightingBlocks.set(Block.stairsWoodOak.blockID);
         slabStyleLightingBlocks.set(Block.stairsCobblestone.blockID);
         */
        if(snowInTaiga) {
            BiomeGenBase.taiga.setEnableSnow().setTemperatureRainfall(-0.5F, 0.4F);
        }
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        if (!good_init) {
            crash("preInit failed - aborting load()");
            return;
        }
        // Register renderer
        fenceRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new WCFenceRenderer());
        ladderRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new WCLadderRenderer());
        cuboidRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new WCCuboidRenderer());
        halfdoorRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new WCHalfDoorRenderer());
        cuboidNSEWUDRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new WCCuboidNSEWUDRenderer());
        stairRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new WCStairRenderer());
        if (useWaterCTMFix && ClassTransformer.checkForCTMSupport()) {
            fluidCTMRenderID = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new WCFluidCTMRenderer());
        }
        else {
            useWaterCTMFix = false;
            fluidCTMRenderID = 4;   // Vanilla fluid renderer
        }
        proxy.initRenderRegistry();

        //NOTYET NetworkRegistry.newChannel(WesterosBlocksPacketHandler.CHANNEL, new WesterosBlocksPacketHandler());
        // Construct custom block definitions
        ArrayList<Block> blklist = new ArrayList<Block>();
        customBlocksByName = new HashMap<String, Block>();
        for (int i = 0; i < customBlockDefs.length; i++) {
            if (customBlockDefs[i] == null) continue;
            Block[] blks = customBlockDefs[i].createBlocks();
            if (blks != null) {
                for (int j = 0; j < blks.length; j++) {
                    Block blk = blks[j];
                    blklist.add(blk);
                    customBlocksByName.put(customBlockDefs[i].getBlockName(j), blk);
                }
            }
            else {
                crash("Invalid block definition for " + customBlockDefs[i].blockName + " - aborted during load()");
                return;
            }
        }
        customBlocks = blklist.toArray(new Block[blklist.size()]);

        // Initialize custom block definitions
        for (int i = 0; i < customBlocks.length; i++) {
            if (customBlocks[i] instanceof HardiumLifecycle) {
                ((HardiumLifecycle)customBlocks[i]).initializeBlockDefinition();
            }
        }

        // Register custom block definitions
        for (int i = 0; i < customBlocks.length; i++) {
            if (customBlocks[i] instanceof HardiumLifecycle) {
                ((HardiumLifecycle)customBlocks[i]).registerBlockDefinition();
            }
        }
        // Set up channel
        channels = NetworkRegistry.INSTANCE.newChannel(HardiumChannelHandler.CHANNEL, new HardiumChannelHandler(), new PacketHandler());

    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event)
    {
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
    }
}