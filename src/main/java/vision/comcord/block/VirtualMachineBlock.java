package vision.comcord.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import vision.comcord.CraftOpsMod;
import vision.comcord.block_entity.VirtualMachineBlockEntity;

public class VirtualMachineBlock extends Block implements BlockEntityProvider {

    public VirtualMachineBlock() {
        super(FabricBlockSettings.create().strength(4.0f));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VirtualMachineBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) return;

        var runRequest = RunInstancesRequest.builder()
                .imageId("ami-0d318f1f104612755")
                .instanceType("t2.micro")
                .maxCount(1)
                .minCount(1)
                .build();

        var response = CraftOpsMod.EC2.runInstances(runRequest);
        var instanceId = response.instances().get(0).instanceId();
        var blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof VirtualMachineBlockEntity vmbe)) return;

        vmbe.instanceId = instanceId;

        CraftOpsMod.LOGGER.info("Instance " + instanceId + " successfully created");
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient) return;

        var blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof VirtualMachineBlockEntity vmbe)) return;

        var instanceId = vmbe.instanceId;

        var request = StopInstancesRequest.builder().instanceIds(instanceId).build();
        CraftOpsMod.EC2.stopInstances(request);

        CraftOpsMod.LOGGER.info("Instance " + instanceId + " successfully stopped");
    }
}
