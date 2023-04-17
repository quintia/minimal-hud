package net.quintia.mods;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MinimalHud.MODID)
public class MinimalHud {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "minimalhud";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public MinimalHud() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            // Some client setup code
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("minimalhud", new Overlay());
        }
    }
}

class Overlay implements IGuiOverlay {
    private static final int MARGIN = 7;
    private static final int FRAME = 5;
    private static final int PADDING = 3;

    private static final int FRAME_COLOR_T = 0x60FFFFFF;
    private static final int FRAME_COLOR_L = 0x60FF0000;
    private static final int FRAME_COLOR_B = 0x600000FF;
    private static final int FRAME_COLOR_R = 0x6000FF00;

//    private final Minecraft mc;

    public Overlay() {
    }

    private static String fDouble(double q) {
        return String.format("%.2f", q);
    }

    private static String fVec(Vec3 v) {
        return fDouble(v.x) + " " + fDouble(v.y) + " " + fDouble(v.z);
    }

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
        Minecraft mc = gui.getMinecraft();
        if (mc.player == null) {
            return;
        }

        Font font = gui.getFont();
        int fontHeight = font.lineHeight;
        String x = "X: " + fDouble(mc.player.getX());
        String y = "Y: " + fDouble(mc.player.getY());
        String z = "Z: " + fDouble(mc.player.getZ());

        int widthOfIndicator = Math.max(
                font.width(x),
                Math.max(font.width(y), font.width(z))
        );

        // top frame
        GuiComponent.fill(poseStack,
                width - MARGIN - PADDING * 2 - widthOfIndicator - FRAME * 2,
                MARGIN,
                width - MARGIN - FRAME,  // shorten right
                MARGIN + FRAME,
                FRAME_COLOR_T);
        // left frame
        GuiComponent.fill(poseStack,
                width - MARGIN - PADDING * 2 - widthOfIndicator - FRAME * 2,
                MARGIN + FRAME,  // shorten top
                width - MARGIN - PADDING * 2 - widthOfIndicator - FRAME,
                MARGIN + PADDING + fontHeight * 3 + FRAME * 2,
                FRAME_COLOR_L);
        // bottom frame
        GuiComponent.fill(poseStack,
                width - MARGIN - PADDING * 2 - widthOfIndicator - FRAME,  //shorten left
                MARGIN + PADDING + fontHeight * 3 + FRAME,
                width - MARGIN,
                MARGIN + PADDING + fontHeight * 3 + FRAME * 2,
                FRAME_COLOR_B);
        // right frame
        GuiComponent.fill(poseStack,
                width - MARGIN - FRAME,
                MARGIN,
                width - MARGIN,
                MARGIN + PADDING + fontHeight * 3 + FRAME,  //shorten bottom
                FRAME_COLOR_R);

        // draw coordinates
        GuiComponent.drawString(poseStack, gui.getFont(),
                x,
                width - MARGIN - FRAME - PADDING - widthOfIndicator,  // x
                MARGIN + FRAME + PADDING,  // y
                0xA0FFFFFF);
        GuiComponent.drawString(poseStack, gui.getFont(),
                y,
                width - MARGIN - FRAME - PADDING - widthOfIndicator,  // x
                MARGIN + FRAME + PADDING + fontHeight,  // y
                0xA0FFFFFF);
        GuiComponent.drawString(poseStack, gui.getFont(),
                z,
                width - MARGIN - FRAME - PADDING - widthOfIndicator,  // x
                MARGIN + FRAME + PADDING + fontHeight * 2,  // y
                0xA0FFFFFF);
    }
}
