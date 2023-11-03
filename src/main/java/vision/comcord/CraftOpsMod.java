package vision.comcord;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import vision.comcord.block.VirtualMachineBlock;
import vision.comcord.block_entity.VirtualMachineBlockEntity;

public class CraftOpsMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("craftops");

    public static final Ec2Client EC2 = Ec2Client.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(ProfileCredentialsProvider.create("personal"))
            .build();

    public static final Block VIRTUAL_MACHINE_BLOCK = new VirtualMachineBlock();
    public static final BlockEntityType<VirtualMachineBlockEntity> VIRTUAL_MACHINE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("craftops", "virtual_machine_block_entity"),
            FabricBlockEntityTypeBuilder.create(VirtualMachineBlockEntity::new, VIRTUAL_MACHINE_BLOCK).build()
    );

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        Registry.register(Registries.BLOCK, new Identifier("craftops", "computer_block"), VIRTUAL_MACHINE_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("craftops", "computer_block"), new BlockItem(VIRTUAL_MACHINE_BLOCK, new Item.Settings()));

        LOGGER.info("Hello Fabric world!");
    }
}