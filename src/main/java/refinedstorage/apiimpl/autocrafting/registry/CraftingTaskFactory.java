package refinedstorage.apiimpl.autocrafting.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import refinedstorage.RSUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.autocrafting.task.IProcessable;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.util.IFluidStackList;
import refinedstorage.api.util.IItemStackList;
import refinedstorage.apiimpl.autocrafting.task.CraftingTask;
import refinedstorage.apiimpl.autocrafting.task.Processable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class CraftingTaskFactory implements ICraftingTaskFactory {
    public static final String ID = "normal";

    @Override
    @Nonnull
    public ICraftingTask create(World world, INetworkMaster network, @Nullable ItemStack stack, ICraftingPattern pattern, int quantity, @Nullable NBTTagCompound tag) {
        if (tag != null) {
            NBTTagList toProcessList = tag.getTagList(CraftingTask.NBT_TO_PROCESS, Constants.NBT.TAG_COMPOUND);

            List<IProcessable> toProcess = new ArrayList<>();

            for (int i = 0; i < toProcessList.tagCount(); ++i) {
                toProcess.add(new Processable(pattern, toProcessList.getCompoundTagAt(i)));
            }

            IItemStackList toTake = RSUtils.readItemStackList(tag.getTagList(CraftingTask.NBT_TO_TAKE, Constants.NBT.TAG_COMPOUND));
            IItemStackList internalToTake = RSUtils.readItemStackList(tag.getTagList(CraftingTask.NBT_INTERNAL_TO_TAKE, Constants.NBT.TAG_COMPOUND));
            IFluidStackList toTakeFluids = RSUtils.readFluidStackList(tag.getTagList(CraftingTask.NBT_TO_TAKE_FLUIDS, Constants.NBT.TAG_COMPOUND));

            NBTTagList toInsertList = tag.getTagList(CraftingTask.NBT_TO_INSERT, Constants.NBT.TAG_COMPOUND);

            ArrayDeque<ItemStack> toInsert = new ArrayDeque<>();

            for (int i = 0; i < toInsertList.tagCount(); ++i) {
                ItemStack insertStack = ItemStack.loadItemStackFromNBT(toInsertList.getCompoundTagAt(i));

                if (insertStack != null) {
                    toInsert.add(insertStack);
                }
            }

            NBTTagList tookList = tag.getTagList(CraftingTask.NBT_TOOK, Constants.NBT.TAG_COMPOUND);

            IItemStackList took = RSUtils.readItemStackList(tookList);

            NBTTagList tookFluidsList = tag.getTagList(CraftingTask.NBT_TOOK_FLUIDS, Constants.NBT.TAG_COMPOUND);

            List<FluidStack> tookFluids = new ArrayList<>();

            for (int i = 0; i < tookFluidsList.tagCount(); ++i) {
                FluidStack tookStack = FluidStack.loadFluidStackFromNBT(tookList.getCompoundTagAt(i));

                if (tookStack != null) {
                    tookFluids.add(tookStack);
                }
            }

            return new CraftingTask(network, stack, pattern, quantity, toProcess, toTake, internalToTake, toTakeFluids, toInsert, took, tookFluids);
        }

        return new CraftingTask(network, stack, pattern, quantity);
    }
}