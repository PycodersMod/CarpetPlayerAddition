package top.local.carpetplayeraddition.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import top.local.carpetplayeraddition.core.CommandResult;
import top.local.carpetplayeraddition.core.DropAmount;
import top.local.carpetplayeraddition.core.DropService;
import top.local.carpetplayeraddition.core.EquipmentTarget;
import top.local.carpetplayeraddition.core.FakePlayerResolver;
import top.local.carpetplayeraddition.core.GiveTransferService;
import top.local.carpetplayeraddition.core.MagnetStateService;
import top.local.carpetplayeraddition.core.PlayerSlots;
import top.local.carpetplayeraddition.core.PickStateService;
import top.local.carpetplayeraddition.core.RestockStateService;
import top.local.carpetplayeraddition.core.SlotRef;
import top.local.carpetplayeraddition.core.SwapService;
import top.local.carpetplayeraddition.core.TakeTransferService;
import top.local.carpetplayeraddition.core.TargetPlayerResolver;
import top.local.carpetplayeraddition.core.TrashcanStateService;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class PlayerCommandExtensionRegistrar {
    private static final SwapService SWAP_SERVICE = new SwapService();
    private static final DropService DROP_SERVICE = new DropService();
    private static final GiveTransferService GIVE_SERVICE = new GiveTransferService();
    private static final TakeTransferService TAKE_SERVICE = new TakeTransferService();

    private PlayerCommandExtensionRegistrar() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register(PlayerCommandExtensionRegistrar::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        registerPlayerNode(dispatcher);
    }

    private static void registerPlayerNode(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("player")
                .then(argument("player", StringArgumentType.word())
                        .then(literal("ite")
                                .then(argument("inventorySlot", IntegerArgumentType.integer(1, 27))
                                        .then(argument("enderSlot", IntegerArgumentType.integer(1, 27))
                                                .executes(context -> execute(context, () -> {
                                                    ServerPlayer fake = FakePlayerResolver.requireFake(context);
                                                    return SWAP_SERVICE.swap(fake,
                                                            PlayerSlots.storage(fake, IntegerArgumentType.getInteger(context, "inventorySlot")),
                                                            PlayerSlots.enderChest(fake, IntegerArgumentType.getInteger(context, "enderSlot")));
                                                })))))
                        .then(hotbarSwap())
                        .then(equipmentSwap(EquipmentTarget.OFFHAND))
                        .then(equipmentSwap(EquipmentTarget.HEAD))
                        .then(equipmentSwap(EquipmentTarget.CHEST))
                        .then(equipmentSwap(EquipmentTarget.LEGS))
                        .then(equipmentSwap(EquipmentTarget.FEET))
                        .then(dropCommands())
                        .then(giveCommands())
                        .then(takeCommands())
                        .then(pickCommands())
                        .then(restockCommands())
                        .then(magnetCommands())
                        .then(trashcanCommands())));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> hotbarSwap() {
        return literal("hotbar")
                .then(argument("slot", IntegerArgumentType.integer(1, 9))
                        .then(literal("inventory")
                                .then(argument("inventorySlot", IntegerArgumentType.integer(1, 27))
                                        .executes(context -> execute(context, () -> {
                                            ServerPlayer fake = FakePlayerResolver.requireFake(context);
                                            return SWAP_SERVICE.swap(fake,
                                                    PlayerSlots.hotbar(fake, IntegerArgumentType.getInteger(context, "slot")),
                                                    PlayerSlots.storage(fake, IntegerArgumentType.getInteger(context, "inventorySlot")));
                                        }))))
                        .then(literal("enderchest")
                                .then(argument("enderSlot", IntegerArgumentType.integer(1, 27))
                                        .executes(context -> execute(context, () -> {
                                            ServerPlayer fake = FakePlayerResolver.requireFake(context);
                                            return SWAP_SERVICE.swap(fake,
                                                    PlayerSlots.hotbar(fake, IntegerArgumentType.getInteger(context, "slot")),
                                                    PlayerSlots.enderChest(fake, IntegerArgumentType.getInteger(context, "enderSlot")));
                                        }))))) ;
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> equipmentSwap(EquipmentTarget target) {
        return literal(target.literal())
                .then(literal("inventory")
                        .then(argument("inventorySlot", IntegerArgumentType.integer(1, 27))
                                .executes(context -> execute(context, () -> {
                                    ServerPlayer fake = FakePlayerResolver.requireFake(context);
                                    return SWAP_SERVICE.swap(fake, PlayerSlots.equipment(fake, target), PlayerSlots.storage(fake, IntegerArgumentType.getInteger(context, "inventorySlot")));
                                }))))
                .then(literal("enderchest")
                        .then(argument("enderSlot", IntegerArgumentType.integer(1, 27))
                                .executes(context -> execute(context, () -> {
                                    ServerPlayer fake = FakePlayerResolver.requireFake(context);
                                    return SWAP_SERVICE.swap(fake, PlayerSlots.equipment(fake, target), PlayerSlots.enderChest(fake, IntegerArgumentType.getInteger(context, "enderSlot")));
                                }))));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> dropCommands() {
        return literal("drop")
                .then(literal("hotbar")
                        .then(literal("all").executes(context -> dropRange(context, PlayerSlots::hotbarAll)))
                        .then(argument("slot", IntegerArgumentType.integer(1, 9))
                                .executes(context -> dropSlot(context, fake -> PlayerSlots.hotbar(fake, IntegerArgumentType.getInteger(context, "slot")), DropAmount.one()))
                                .then(argument("amount", IntegerArgumentType.integer(1, 64))
                                        .executes(context -> dropSlot(context, fake -> PlayerSlots.hotbar(fake, IntegerArgumentType.getInteger(context, "slot")), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                                .then(literal("all")
                                        .executes(context -> dropSlot(context, fake -> PlayerSlots.hotbar(fake, IntegerArgumentType.getInteger(context, "slot")), DropAmount.all())))))
                .then(dropEquipment(EquipmentTarget.OFFHAND))
                .then(dropEquipment(EquipmentTarget.HEAD))
                .then(dropEquipment(EquipmentTarget.CHEST))
                .then(dropEquipment(EquipmentTarget.LEGS))
                .then(dropEquipment(EquipmentTarget.FEET))
                .then(literal("inventory")
                        .then(literal("all").executes(context -> dropRange(context, PlayerSlots::inventoryAll)))
                        .then(argument("inventorySlot", IntegerArgumentType.integer(1, 27))
                                .executes(context -> dropSlot(context, fake -> PlayerSlots.storage(fake, IntegerArgumentType.getInteger(context, "inventorySlot")), DropAmount.one()))
                                .then(argument("amount", IntegerArgumentType.integer(1, 64))
                                        .executes(context -> dropSlot(context, fake -> PlayerSlots.storage(fake, IntegerArgumentType.getInteger(context, "inventorySlot")), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                                .then(literal("all")
                                        .executes(context -> dropSlot(context, fake -> PlayerSlots.storage(fake, IntegerArgumentType.getInteger(context, "inventorySlot")), DropAmount.all())))))
                .then(literal("enderchest")
                        .then(literal("all").executes(context -> dropRange(context, PlayerSlots::enderChestAll)))
                        .then(argument("enderSlot", IntegerArgumentType.integer(1, 27))
                                .executes(context -> dropSlot(context, fake -> PlayerSlots.enderChest(fake, IntegerArgumentType.getInteger(context, "enderSlot")), DropAmount.one()))
                                .then(argument("amount", IntegerArgumentType.integer(1, 64))
                                        .executes(context -> dropSlot(context, fake -> PlayerSlots.enderChest(fake, IntegerArgumentType.getInteger(context, "enderSlot")), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                                .then(literal("all")
                                        .executes(context -> dropSlot(context, fake -> PlayerSlots.enderChest(fake, IntegerArgumentType.getInteger(context, "enderSlot")), DropAmount.all())))))
                .then(literal("all").executes(context -> dropRange(context, PlayerSlots::all)));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> dropEquipment(EquipmentTarget target) {
        return literal(target.literal())
                .executes(context -> dropSlot(context, fake -> PlayerSlots.equipment(fake, target), DropAmount.one()))
                .then(argument("amount", IntegerArgumentType.integer(1, 64))
                        .executes(context -> dropSlot(context, fake -> PlayerSlots.equipment(fake, target), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                .then(literal("all")
                        .executes(context -> dropSlot(context, fake -> PlayerSlots.equipment(fake, target), DropAmount.all())));
    }

    private static int dropSlot(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context, SlotFactory slotFactory, DropAmount amount) {
        return execute(context, () -> {
            ServerPlayer fake = FakePlayerResolver.requireFake(context);
            return DROP_SERVICE.dropSlot(fake, slotFactory.create(fake), amount);
        });
    }

    private static int dropRange(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context, RangeFactory rangeFactory) {
        return execute(context, () -> {
            ServerPlayer fake = FakePlayerResolver.requireFake(context);
            return DROP_SERVICE.dropRange(fake, rangeFactory.create(fake));
        });
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> giveCommands() {
        return literal("give")
                .then(argument("targetPlayer", StringArgumentType.word())
                        .then(giveHotbar())
                        .then(giveEquipment(EquipmentTarget.OFFHAND))
                        .then(giveEquipment(EquipmentTarget.HEAD))
                        .then(giveEquipment(EquipmentTarget.CHEST))
                        .then(giveEquipment(EquipmentTarget.LEGS))
                        .then(giveEquipment(EquipmentTarget.FEET))
                        .then(literal("inventory")
                                .then(literal("all").executes(context -> giveRange(context, PlayerSlots::inventoryAll)))
                                .then(argument("inventorySlot", IntegerArgumentType.integer(1, 27))
                                        .executes(context -> giveSlot(context, fake -> PlayerSlots.storage(fake, IntegerArgumentType.getInteger(context, "inventorySlot")), DropAmount.one()))
                                        .then(argument("amount", IntegerArgumentType.integer(1, 64))
                                                .executes(context -> giveSlot(context, fake -> PlayerSlots.storage(fake, IntegerArgumentType.getInteger(context, "inventorySlot")), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                                        .then(literal("all")
                                                .executes(context -> giveSlot(context, fake -> PlayerSlots.storage(fake, IntegerArgumentType.getInteger(context, "inventorySlot")), DropAmount.all())))))
                        .then(literal("enderchest")
                                .then(literal("all").executes(context -> giveRange(context, PlayerSlots::enderChestAll)))
                                .then(argument("enderSlot", IntegerArgumentType.integer(1, 27))
                                        .executes(context -> giveSlot(context, fake -> PlayerSlots.enderChest(fake, IntegerArgumentType.getInteger(context, "enderSlot")), DropAmount.one()))
                                        .then(argument("amount", IntegerArgumentType.integer(1, 64))
                                                .executes(context -> giveSlot(context, fake -> PlayerSlots.enderChest(fake, IntegerArgumentType.getInteger(context, "enderSlot")), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                                        .then(literal("all")
                                                .executes(context -> giveSlot(context, fake -> PlayerSlots.enderChest(fake, IntegerArgumentType.getInteger(context, "enderSlot")), DropAmount.all())))))
                        .then(literal("all").executes(context -> giveRange(context, PlayerSlots::all))));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> giveEquipment(EquipmentTarget target) {
        return literal(target.literal())
                .executes(context -> giveSlot(context, fake -> PlayerSlots.equipment(fake, target), DropAmount.one()))
                .then(argument("amount", IntegerArgumentType.integer(1, 64))
                        .executes(context -> giveSlot(context, fake -> PlayerSlots.equipment(fake, target), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                .then(literal("all")
                        .executes(context -> giveSlot(context, fake -> PlayerSlots.equipment(fake, target), DropAmount.all())));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> giveHotbar() {
        return literal("hotbar")
                .then(literal("all").executes(context -> giveRange(context, PlayerSlots::hotbarAll)))
                .then(argument("slot", IntegerArgumentType.integer(1, 9))
                        .executes(context -> giveSlot(context, fake -> PlayerSlots.hotbar(fake, IntegerArgumentType.getInteger(context, "slot")), DropAmount.one()))
                        .then(argument("amount", IntegerArgumentType.integer(1, 64))
                                .executes(context -> giveSlot(context, fake -> PlayerSlots.hotbar(fake, IntegerArgumentType.getInteger(context, "slot")), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                        .then(literal("all")
                                .executes(context -> giveSlot(context, fake -> PlayerSlots.hotbar(fake, IntegerArgumentType.getInteger(context, "slot")), DropAmount.all()))));
    }

    private static int giveSlot(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context, SlotFactory slotFactory, DropAmount amount) {
        return execute(context, () -> {
            ServerPlayer fake = FakePlayerResolver.requireFake(context);
            ServerPlayer target = TargetPlayerResolver.requireOnline(context.getSource().getServer(), StringArgumentType.getString(context, "targetPlayer"));
            return GIVE_SERVICE.giveSlot(fake, target, slotFactory.create(fake), amount);
        });
    }

    private static int giveRange(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context, RangeFactory rangeFactory) {
        return execute(context, () -> {
            ServerPlayer fake = FakePlayerResolver.requireFake(context);
            ServerPlayer target = TargetPlayerResolver.requireOnline(context.getSource().getServer(), StringArgumentType.getString(context, "targetPlayer"));
            return GIVE_SERVICE.giveRange(fake, target, rangeFactory.create(fake));
        });
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> takeCommands() {
        return literal("take")
                .then(argument("targetPlayer", StringArgumentType.word())
                        .then(takeHotbar())
                        .then(takeEquipment(EquipmentTarget.OFFHAND))
                        .then(takeEquipment(EquipmentTarget.HEAD))
                        .then(takeEquipment(EquipmentTarget.CHEST))
                        .then(takeEquipment(EquipmentTarget.LEGS))
                        .then(takeEquipment(EquipmentTarget.FEET))
                        .then(literal("inventory")
                                .then(literal("all").executes(context -> takeRange(context, PlayerSlots::inventoryAll)))
                                .then(argument("inventorySlot", IntegerArgumentType.integer(1, 27))
                                        .executes(context -> takeSlot(context, source -> PlayerSlots.storage(source, IntegerArgumentType.getInteger(context, "inventorySlot")), DropAmount.one()))
                                        .then(argument("amount", IntegerArgumentType.integer(1, 64))
                                                .executes(context -> takeSlot(context, source -> PlayerSlots.storage(source, IntegerArgumentType.getInteger(context, "inventorySlot")), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                                        .then(literal("all")
                                                .executes(context -> takeSlot(context, source -> PlayerSlots.storage(source, IntegerArgumentType.getInteger(context, "inventorySlot")), DropAmount.all())))))
                        .then(literal("enderchest")
                                .then(literal("all").executes(context -> takeRange(context, PlayerSlots::enderChestAll)))
                                .then(argument("enderSlot", IntegerArgumentType.integer(1, 27))
                                        .executes(context -> takeSlot(context, source -> PlayerSlots.enderChest(source, IntegerArgumentType.getInteger(context, "enderSlot")), DropAmount.one()))
                                        .then(argument("amount", IntegerArgumentType.integer(1, 64))
                                                .executes(context -> takeSlot(context, source -> PlayerSlots.enderChest(source, IntegerArgumentType.getInteger(context, "enderSlot")), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                                        .then(literal("all")
                                                .executes(context -> takeSlot(context, source -> PlayerSlots.enderChest(source, IntegerArgumentType.getInteger(context, "enderSlot")), DropAmount.all())))))
                        .then(literal("all").executes(context -> takeRange(context, PlayerSlots::all))));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> takeEquipment(EquipmentTarget target) {
        return literal(target.literal())
                .executes(context -> takeSlot(context, source -> PlayerSlots.equipment(source, target), DropAmount.one()))
                .then(argument("amount", IntegerArgumentType.integer(1, 64))
                        .executes(context -> takeSlot(context, source -> PlayerSlots.equipment(source, target), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                .then(literal("all")
                        .executes(context -> takeSlot(context, source -> PlayerSlots.equipment(source, target), DropAmount.all())));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> takeHotbar() {
        return literal("hotbar")
                .then(literal("all").executes(context -> takeRange(context, PlayerSlots::hotbarAll)))
                .then(argument("slot", IntegerArgumentType.integer(1, 9))
                        .executes(context -> takeSlot(context, source -> PlayerSlots.hotbar(source, IntegerArgumentType.getInteger(context, "slot")), DropAmount.one()))
                        .then(argument("amount", IntegerArgumentType.integer(1, 64))
                                .executes(context -> takeSlot(context, source -> PlayerSlots.hotbar(source, IntegerArgumentType.getInteger(context, "slot")), DropAmount.of(IntegerArgumentType.getInteger(context, "amount")))))
                        .then(literal("all")
                                .executes(context -> takeSlot(context, source -> PlayerSlots.hotbar(source, IntegerArgumentType.getInteger(context, "slot")), DropAmount.all()))));
    }

    private static int takeSlot(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context, SlotFactory slotFactory, DropAmount amount) {
        return execute(context, () -> {
            ServerPlayer fake = FakePlayerResolver.requireFake(context);
            ServerPlayer source = TargetPlayerResolver.requireOnline(context.getSource().getServer(), StringArgumentType.getString(context, "targetPlayer"));
            return TAKE_SERVICE.takeSlot(fake, source, slotFactory.create(source), amount);
        });
    }

    private static int takeRange(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context, RangeFactory rangeFactory) {
        return execute(context, () -> {
            ServerPlayer fake = FakePlayerResolver.requireFake(context);
            ServerPlayer source = TargetPlayerResolver.requireOnline(context.getSource().getServer(), StringArgumentType.getString(context, "targetPlayer"));
            return TAKE_SERVICE.takeRange(fake, source, rangeFactory.create(source));
        });
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> pickCommands() {
        return literal("pick")
                .then(argument("enabled", BoolArgumentType.bool())
                        .executes(context -> execute(context, () -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player == null) {
                                throw new IllegalArgumentException("只有玩家可以设置中键取物");
                            }
                            String fakeName = StringArgumentType.getString(context, "player");
                            return PickStateService.configure(player, fakeName, BoolArgumentType.getBool(context, "enabled"));
                        })));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> restockCommands() {
        return literal("restock")
                .then(argument("enabled", BoolArgumentType.bool())
                        .executes(context -> execute(context, () -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player == null) {
                                throw new IllegalArgumentException("只有玩家可以设置右键自动补货");
                            }
                            String fakeName = StringArgumentType.getString(context, "player");
                            return RestockStateService.configure(player, fakeName, BoolArgumentType.getBool(context, "enabled"));
                        })));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> magnetCommands() {
        return literal("magnet")
                .then(argument("enabled", BoolArgumentType.bool())
                        .executes(context -> execute(context, () -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player == null) {
                                throw new IllegalArgumentException("只有玩家可以设置磁力传输");
                            }
                            String fakeName = StringArgumentType.getString(context, "player");
                            return MagnetStateService.configure(player, fakeName, BoolArgumentType.getBool(context, "enabled"));
                        })));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> trashcanCommands() {
        return literal("trashcan")
                .executes(context -> execute(context, () -> TrashcanStateService.configure(FakePlayerResolver.requireFake(context), true)))
                .then(literal("off")
                        .executes(context -> execute(context, () -> TrashcanStateService.configure(FakePlayerResolver.requireFake(context), false))));
    }

    private static int execute(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context, ThrowingSupplier action) {
        try {
            CommandResult result = action.get();
            if (result.success()) {
                context.getSource().sendSuccess(() -> Component.literal(result.message()), false);
                return 1;
            }
            context.getSource().sendFailure(Component.literal(result.message()));
            return 0;
        } catch (IllegalArgumentException exception) {
            context.getSource().sendFailure(Component.literal(exception.getMessage()));
            return 0;
        }
    }

    @FunctionalInterface
    private interface ThrowingSupplier {
        CommandResult get();
    }

    @FunctionalInterface
    private interface SlotFactory {
        SlotRef create(ServerPlayer fakePlayer);
    }

    @FunctionalInterface
    private interface RangeFactory {
        List<SlotRef> create(ServerPlayer fakePlayer);
    }
}
