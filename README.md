# CarpetPlayerAddition

在 Fabric Carpet 环境中提供玩家假人相关功能。

## 工程布局

- Gradle 工程：[CarpetPlayerAddition](./CarpetPlayerAddition/)
- 工程详细说明：[CarpetPlayerAddition/README.md](./CarpetPlayerAddition/README.md)
- 项目注册信息：[PycodersMod.projects.json](../../PycodersMod.projects.json)
- 工作区统一测试入口：[launch.ps1](../../launch.ps1)
- 启动参数填写说明：[launch-parameters.md](../../launch-parameters.md)

## 构建

在 CarpetPlayerAddition 目录执行 .\gradlew.bat clean build。

工程使用 Gradle Java Toolchain 声明所需 Java 版本。机器本地的 JDK 路径位于工作区 local-config，不写入 Mod 仓库。

## 标识

| 项目 | 值 |
|---|---|
| Minecraft | 1.21.6 |
| Loader | Fabric 0.16.14 |
| Java | 21 |
| Mod ID | carpet-player-addition |
| Java Package | com.pycoder.carpetplayeraddition |

Mod ID、Registry Namespace 和存档标识保持原值。工程目录、Gradle group、Java package 和 GitHub 仓库名属于工程组织信息。


## ????

# CarpetPlayerAddition v1.2.1

## Version Update - English

CarpetPlayerAddition v1.2.1 adds `/player <fakeName> magnet <true|false>`, a pickup-forwarding feature for remote item management. It is part of the wireless-terminal-style feature family while remaining in the 1.2.x release line for version ordering.

Magnet mode is disabled by default and must be enabled manually. After a real player enables it for a Carpet fake player, newly picked-up item stacks are forwarded to that fake player. The feature only forwards the items gained from the current pickup event; items that were already stored in the player inventory are not bulk-scanned or transferred.

The pickup transfer is handled by comparing the player's matching item count before and after pickup, then moving only the newly gained amount. This keeps the behavior stable when picked-up items merge into existing stacks. If the fake player's inventory cannot accept the full amount, the remaining items are dropped near the fake player, matching the existing remote-transfer fallback behavior.

This version also clarifies the binding rule for `pick`, `restock`, and `magnet`: each real player can have one bound fake player per feature. Enabling the same feature on another fake player replaces the previous target for that feature, while the three features remain independent.

## 版本更新内容 - 中文

CarpetPlayerAddition v1.2.1 新增 `/player <fakeName> magnet <true|false>`，用于把玩家刚捡起的物品自动转发给绑定的 Carpet 假人。这个功能属于远程物品管理的一部分，延续了中键取物与自动补货的思路；为了版本排序，放在 1.2.x 功能线中发布。

磁力传输默认关闭，必须手动开启。真实玩家把该功能绑定到某个 Carpet 假人后，玩家新捡起的物品会被转发给该假人。它只处理当前拾取事件中新增加的物品，不会批量扫描或转移玩家背包里原本已有的物品。

拾取转移会比较玩家拾取前后的同类物品数量，只移动这次真正新增的数量。因此即使新捡起的物品合并进已有堆叠，也能保持稳定行为。如果假人背包无法完全接收这次捡起的物品，剩余部分会掉落在假人附近，保持与现有远程转移功能一致的兜底行为。

本版本也统一了 `pick`、`restock`、`magnet` 的绑定规则：同一个真实玩家的同一种功能只保留最后绑定的一个假人。把同一种功能开启到另一个假人时，会替换之前的目标；三种功能彼此独立计算。

## 指令总表 / Command Table

| 指令 / Command | 注释 / Note |
|---|---|
| `/player <fakeName> hotbar <1-9> inventory <1-27>` | 交换假人快捷栏与背包储物格。 / Swaps a fake-player hotbar slot with an inventory storage slot. |
| `/player <fakeName> hotbar <1-9> enderchest <1-27>` | 交换假人快捷栏与末影箱格子。 / Swaps a fake-player hotbar slot with an ender chest slot. |
| `/player <fakeName> offhand inventory <1-27>` | 交换假人副手与背包储物格。 / Swaps the fake player's offhand with an inventory storage slot. |
| `/player <fakeName> offhand enderchest <1-27>` | 交换假人副手与末影箱格子。 / Swaps the fake player's offhand with an ender chest slot. |
| `/player <fakeName> head inventory <1-27>` | 交换假人头部装备槽与背包储物格。 / Swaps the fake player's head equipment slot with an inventory storage slot. |
| `/player <fakeName> head enderchest <1-27>` | 交换假人头部装备槽与末影箱格子。 / Swaps the fake player's head equipment slot with an ender chest slot. |
| `/player <fakeName> chest inventory <1-27>` | 交换假人胸甲槽与背包储物格。 / Swaps the fake player's chest equipment slot with an inventory storage slot. |
| `/player <fakeName> chest enderchest <1-27>` | 交换假人胸甲槽与末影箱格子。 / Swaps the fake player's chest equipment slot with an ender chest slot. |
| `/player <fakeName> legs inventory <1-27>` | 交换假人护腿槽与背包储物格。 / Swaps the fake player's legs equipment slot with an inventory storage slot. |
| `/player <fakeName> legs enderchest <1-27>` | 交换假人护腿槽与末影箱格子。 / Swaps the fake player's legs equipment slot with an ender chest slot. |
| `/player <fakeName> feet inventory <1-27>` | 交换假人靴子槽与背包储物格。 / Swaps the fake player's feet equipment slot with an inventory storage slot. |
| `/player <fakeName> feet enderchest <1-27>` | 交换假人靴子槽与末影箱格子。 / Swaps the fake player's feet equipment slot with an ender chest slot. |
| `/player <fakeName> ite <1-27> <1-27>` | 交换假人背包储物格与末影箱格子。 / Swaps a fake-player inventory storage slot with an ender chest slot. |
| `/player <fakeName> drop hotbar <1-9> [num]` | 从假人指定快捷栏槽位丢出物品。 / Drops items from one fake-player hotbar slot. |
| `/player <fakeName> drop hotbar all` | 丢出假人快捷栏 9 格中的全部物品。 / Drops all items from the fake player's 9 hotbar slots. |
| `/player <fakeName> drop offhand [num]` | 从假人副手丢出物品。 / Drops items from the fake player's offhand. |
| `/player <fakeName> drop head [num]` | 从假人头部装备槽丢出物品。 / Drops items from the fake player's head equipment slot. |
| `/player <fakeName> drop chest [num]` | 从假人胸甲槽丢出物品。 / Drops items from the fake player's chest equipment slot. |
| `/player <fakeName> drop legs [num]` | 从假人护腿槽丢出物品。 / Drops items from the fake player's legs equipment slot. |
| `/player <fakeName> drop feet [num]` | 从假人靴子槽丢出物品。 / Drops items from the fake player's feet equipment slot. |
| `/player <fakeName> drop inventory <1-27> [num]` | 从假人背包储物格丢出物品。 / Drops items from one fake-player inventory storage slot. |
| `/player <fakeName> drop enderchest <1-27> [num]` | 从假人末影箱格子丢出物品。 / Drops items from one fake-player ender chest slot. |
| `/player <fakeName> drop inventory all` | 丢出假人物品栏范围，不含末影箱。 / Drops the fake player's inventory range, excluding the ender chest. |
| `/player <fakeName> drop enderchest all` | 丢出假人末影箱全部物品。 / Drops all items from the fake player's ender chest. |
| `/player <fakeName> drop all` | 丢出假人可管理范围内全部物品。 / Drops all managed fake-player items. |
| `/player <fakeName> give <playerName> hotbar <1-9> [num]` | 把假人指定快捷栏槽位物品转移给玩家。 / Gives items from one fake-player hotbar slot to a player. |
| `/player <fakeName> give <playerName> hotbar all` | 把假人快捷栏 9 格物品转移给玩家。 / Gives all items from the fake player's 9 hotbar slots to a player. |
| `/player <fakeName> give <playerName> offhand [num]` | 把假人副手物品转移给玩家。 / Gives items from the fake player's offhand to a player. |
| `/player <fakeName> give <playerName> head [num]` | 把假人头部装备槽物品转移给玩家。 / Gives items from the fake player's head equipment slot to a player. |
| `/player <fakeName> give <playerName> chest [num]` | 把假人胸甲槽物品转移给玩家。 / Gives items from the fake player's chest equipment slot to a player. |
| `/player <fakeName> give <playerName> legs [num]` | 把假人护腿槽物品转移给玩家。 / Gives items from the fake player's legs equipment slot to a player. |
| `/player <fakeName> give <playerName> feet [num]` | 把假人靴子槽物品转移给玩家。 / Gives items from the fake player's feet equipment slot to a player. |
| `/player <fakeName> give <playerName> inventory <1-27> [num]` | 把假人背包储物格物品转移给玩家。 / Gives items from one fake-player inventory storage slot to a player. |
| `/player <fakeName> give <playerName> enderchest <1-27> [num]` | 把假人末影箱格子物品转移给玩家。 / Gives items from one fake-player ender chest slot to a player. |
| `/player <fakeName> give <playerName> inventory all` | 把假人物品栏范围转移给玩家，不含末影箱。 / Gives the fake player's inventory range to a player, excluding the ender chest. |
| `/player <fakeName> give <playerName> enderchest all` | 把假人末影箱全部物品转移给玩家。 / Gives all items from the fake player's ender chest to a player. |
| `/player <fakeName> give <playerName> all` | 把假人可管理范围内全部物品转移给玩家。 / Gives all managed fake-player items to a player. |
| `/player <fakeName> take <playerName> hotbar <1-9> [num]` | 假人从玩家指定快捷栏槽位拿取物品。 / The fake player takes items from one player hotbar slot. |
| `/player <fakeName> take <playerName> hotbar all` | 假人从玩家快捷栏 9 格拿取全部物品。 / The fake player takes all items from the player's 9 hotbar slots. |
| `/player <fakeName> take <playerName> offhand [num]` | 假人从玩家副手拿取物品。 / The fake player takes items from the player's offhand. |
| `/player <fakeName> take <playerName> head [num]` | 假人从玩家头部装备槽拿取物品。 / The fake player takes items from the player's head equipment slot. |
| `/player <fakeName> take <playerName> chest [num]` | 假人从玩家胸甲槽拿取物品。 / The fake player takes items from the player's chest equipment slot. |
| `/player <fakeName> take <playerName> legs [num]` | 假人从玩家护腿槽拿取物品。 / The fake player takes items from the player's legs equipment slot. |
| `/player <fakeName> take <playerName> feet [num]` | 假人从玩家靴子槽拿取物品。 / The fake player takes items from the player's feet equipment slot. |
| `/player <fakeName> take <playerName> inventory <1-27> [num]` | 假人从玩家背包储物格拿取物品。 / The fake player takes items from one player inventory storage slot. |
| `/player <fakeName> take <playerName> enderchest <1-27> [num]` | 假人从玩家末影箱格子拿取物品。 / The fake player takes items from one player ender chest slot. |
| `/player <fakeName> take <playerName> inventory all` | 假人从玩家物品栏范围拿取物品，不含末影箱。 / The fake player takes the player's inventory range, excluding the ender chest. |
| `/player <fakeName> take <playerName> enderchest all` | 假人从玩家末影箱拿取全部物品。 / The fake player takes all items from the player's ender chest. |
| `/player <fakeName> take <playerName> all` | 假人从玩家可管理范围内拿取全部物品。 / The fake player takes all managed player items. |
| `/player <fakeName> pick <true\|false>` | 绑定假人并开关中键取物；规整后不足半组才从假人补齐。 / Binds a fake player and toggles middle-click pickup; fake-player stock is used only when still below half a stack after local organization. |
| `/player <fakeName> restock <true\|false>` | 绑定假人并开关右键自动补货；默认关闭，规整后不足半组再从假人补齐。 / Binds a fake player and toggles right-click auto-restock; it is disabled by default and refills from the fake player only when still below half a stack after local organization. |
| `/player <fakeName> magnet <true\|false>` | 绑定假人并开关磁力传输；只转移玩家刚捡起的物品，不转移玩家原本已有物品。 / Binds a fake player and toggles magnet transfer; only newly picked-up items are forwarded, while existing player inventory items are left untouched. |
| `/player <fakeName> trashcan` | 开启假人远程垃圾桶模式；假人获得任何可管理物品后自动丢出全部物品。 / Enables remote trashcan mode; when the fake player receives any managed item, it automatically drops all managed items. |
| `/player <fakeName> trashcan off` | 关闭假人远程垃圾桶模式。 / Disables remote trashcan mode for the fake player. |

`[num]` can be omitted for `1`, set to `1-64`, or set to `all`. / `[num]` 可省略为 `1`，也可以填写 `1-64` 或 `all`。


---

# CarpetPlayerAddition v1.2.0

## Version Update - English

CarpetPlayerAddition v1.2.0 starts the 1.2.x feature line with a remote trashcan mode for Carpet fake players. This feature is separate from the wireless-terminal-like pickup and restock features introduced in the 1.1.x line.

The new `/player <fakeName> trashcan` command turns an online Carpet fake player into a remote trashcan target. While enabled, the fake player stays silent as long as its managed containers are empty. The monitored range includes inventory storage slots, hotbar slots, offhand, armor slots, and the ender chest. Once any item appears in those containers through any route, the fake player immediately drops every managed item in front of itself, matching the existing `drop all` behavior.

The companion `/player <fakeName> trashcan off` command disables the mode. This makes it possible to place a fake player near an item input, enable trashcan mode, and then remotely transfer unwanted items to it with commands such as `take`; the fake player will automatically throw those items into the local input area.

## 版本更新内容 - 中文

CarpetPlayerAddition v1.2.0 开启 1.2.x 功能线，新增 Carpet 假人的远程垃圾桶模式。这个功能独立于 1.1.x 的仿无线终端取物与自动补货功能。

新增 `/player <fakeName> trashcan` 指令，可把一个在线 Carpet 假人设定为远程垃圾桶目标。开启后，只要假人的可管理容器全部为空，它就保持静默；监控范围包括背包储物格、快捷栏、副手、盔甲栏和末影箱。一旦这些容器通过任何方式获得物品，假人会立即把可管理范围内的全部物品丢到自己面前，效果等同于现有的 `drop all`。

配套的 `/player <fakeName> trashcan off` 指令用于关闭该模式。这样可以把假人预先放在物品输入口附近，开启垃圾桶模式后，再通过 `take` 等远程转移指令把玩家身上的杂物送到假人身上，由假人自动把物品丢进附近输入口。

## 指令总表 / Command Table

| 指令 / Command | 注释 / Note |
|---|---|
| `/player <fakeName> hotbar <1-9> inventory <1-27>` | 交换假人快捷栏与背包储物格。 / Swaps a fake-player hotbar slot with an inventory storage slot. |
| `/player <fakeName> hotbar <1-9> enderchest <1-27>` | 交换假人快捷栏与末影箱格子。 / Swaps a fake-player hotbar slot with an ender chest slot. |
| `/player <fakeName> offhand inventory <1-27>` | 交换假人副手与背包储物格。 / Swaps the fake player's offhand with an inventory storage slot. |
| `/player <fakeName> offhand enderchest <1-27>` | 交换假人副手与末影箱格子。 / Swaps the fake player's offhand with an ender chest slot. |
| `/player <fakeName> head inventory <1-27>` | 交换假人头部装备槽与背包储物格。 / Swaps the fake player's head equipment slot with an inventory storage slot. |
| `/player <fakeName> head enderchest <1-27>` | 交换假人头部装备槽与末影箱格子。 / Swaps the fake player's head equipment slot with an ender chest slot. |
| `/player <fakeName> chest inventory <1-27>` | 交换假人胸甲槽与背包储物格。 / Swaps the fake player's chest equipment slot with an inventory storage slot. |
| `/player <fakeName> chest enderchest <1-27>` | 交换假人胸甲槽与末影箱格子。 / Swaps the fake player's chest equipment slot with an ender chest slot. |
| `/player <fakeName> legs inventory <1-27>` | 交换假人护腿槽与背包储物格。 / Swaps the fake player's legs equipment slot with an inventory storage slot. |
| `/player <fakeName> legs enderchest <1-27>` | 交换假人护腿槽与末影箱格子。 / Swaps the fake player's legs equipment slot with an ender chest slot. |
| `/player <fakeName> feet inventory <1-27>` | 交换假人靴子槽与背包储物格。 / Swaps the fake player's feet equipment slot with an inventory storage slot. |
| `/player <fakeName> feet enderchest <1-27>` | 交换假人靴子槽与末影箱格子。 / Swaps the fake player's feet equipment slot with an ender chest slot. |
| `/player <fakeName> ite <1-27> <1-27>` | 交换假人背包储物格与末影箱格子。 / Swaps a fake-player inventory storage slot with an ender chest slot. |
| `/player <fakeName> drop hotbar <1-9> [num]` | 从假人指定快捷栏槽位丢出物品。 / Drops items from one fake-player hotbar slot. |
| `/player <fakeName> drop hotbar all` | 丢出假人快捷栏 9 格中的全部物品。 / Drops all items from the fake player's 9 hotbar slots. |
| `/player <fakeName> drop offhand [num]` | 从假人副手丢出物品。 / Drops items from the fake player's offhand. |
| `/player <fakeName> drop head [num]` | 从假人头部装备槽丢出物品。 / Drops items from the fake player's head equipment slot. |
| `/player <fakeName> drop chest [num]` | 从假人胸甲槽丢出物品。 / Drops items from the fake player's chest equipment slot. |
| `/player <fakeName> drop legs [num]` | 从假人护腿槽丢出物品。 / Drops items from the fake player's legs equipment slot. |
| `/player <fakeName> drop feet [num]` | 从假人靴子槽丢出物品。 / Drops items from the fake player's feet equipment slot. |
| `/player <fakeName> drop inventory <1-27> [num]` | 从假人背包储物格丢出物品。 / Drops items from one fake-player inventory storage slot. |
| `/player <fakeName> drop enderchest <1-27> [num]` | 从假人末影箱格子丢出物品。 / Drops items from one fake-player ender chest slot. |
| `/player <fakeName> drop inventory all` | 丢出假人物品栏范围，不含末影箱。 / Drops the fake player's inventory range, excluding the ender chest. |
| `/player <fakeName> drop enderchest all` | 丢出假人末影箱全部物品。 / Drops all items from the fake player's ender chest. |
| `/player <fakeName> drop all` | 丢出假人可管理范围内全部物品。 / Drops all managed fake-player items. |
| `/player <fakeName> give <playerName> hotbar <1-9> [num]` | 把假人指定快捷栏槽位物品转移给玩家。 / Gives items from one fake-player hotbar slot to a player. |
| `/player <fakeName> give <playerName> hotbar all` | 把假人快捷栏 9 格物品转移给玩家。 / Gives all items from the fake player's 9 hotbar slots to a player. |
| `/player <fakeName> give <playerName> offhand [num]` | 把假人副手物品转移给玩家。 / Gives items from the fake player's offhand to a player. |
| `/player <fakeName> give <playerName> head [num]` | 把假人头部装备槽物品转移给玩家。 / Gives items from the fake player's head equipment slot to a player. |
| `/player <fakeName> give <playerName> chest [num]` | 把假人胸甲槽物品转移给玩家。 / Gives items from the fake player's chest equipment slot to a player. |
| `/player <fakeName> give <playerName> legs [num]` | 把假人护腿槽物品转移给玩家。 / Gives items from the fake player's legs equipment slot to a player. |
| `/player <fakeName> give <playerName> feet [num]` | 把假人靴子槽物品转移给玩家。 / Gives items from the fake player's feet equipment slot to a player. |
| `/player <fakeName> give <playerName> inventory <1-27> [num]` | 把假人背包储物格物品转移给玩家。 / Gives items from one fake-player inventory storage slot to a player. |
| `/player <fakeName> give <playerName> enderchest <1-27> [num]` | 把假人末影箱格子物品转移给玩家。 / Gives items from one fake-player ender chest slot to a player. |
| `/player <fakeName> give <playerName> inventory all` | 把假人物品栏范围转移给玩家，不含末影箱。 / Gives the fake player's inventory range to a player, excluding the ender chest. |
| `/player <fakeName> give <playerName> enderchest all` | 把假人末影箱全部物品转移给玩家。 / Gives all items from the fake player's ender chest to a player. |
| `/player <fakeName> give <playerName> all` | 把假人可管理范围内全部物品转移给玩家。 / Gives all managed fake-player items to a player. |
| `/player <fakeName> take <playerName> hotbar <1-9> [num]` | 假人从玩家指定快捷栏槽位拿取物品。 / The fake player takes items from one player hotbar slot. |
| `/player <fakeName> take <playerName> hotbar all` | 假人从玩家快捷栏 9 格拿取全部物品。 / The fake player takes all items from the player's 9 hotbar slots. |
| `/player <fakeName> take <playerName> offhand [num]` | 假人从玩家副手拿取物品。 / The fake player takes items from the player's offhand. |
| `/player <fakeName> take <playerName> head [num]` | 假人从玩家头部装备槽拿取物品。 / The fake player takes items from the player's head equipment slot. |
| `/player <fakeName> take <playerName> chest [num]` | 假人从玩家胸甲槽拿取物品。 / The fake player takes items from the player's chest equipment slot. |
| `/player <fakeName> take <playerName> legs [num]` | 假人从玩家护腿槽拿取物品。 / The fake player takes items from the player's legs equipment slot. |
| `/player <fakeName> take <playerName> feet [num]` | 假人从玩家靴子槽拿取物品。 / The fake player takes items from the player's feet equipment slot. |
| `/player <fakeName> take <playerName> inventory <1-27> [num]` | 假人从玩家背包储物格拿取物品。 / The fake player takes items from one player inventory storage slot. |
| `/player <fakeName> take <playerName> enderchest <1-27> [num]` | 假人从玩家末影箱格子拿取物品。 / The fake player takes items from one player ender chest slot. |
| `/player <fakeName> take <playerName> inventory all` | 假人从玩家物品栏范围拿取物品，不含末影箱。 / The fake player takes the player's inventory range, excluding the ender chest. |
| `/player <fakeName> take <playerName> enderchest all` | 假人从玩家末影箱拿取全部物品。 / The fake player takes all items from the player's ender chest. |
| `/player <fakeName> take <playerName> all` | 假人从玩家可管理范围内拿取全部物品。 / The fake player takes all managed player items. |
| `/player <fakeName> pick <true\|false>` | 绑定假人并开关中键取物；其它快捷栏有目标物品而主手没有时先互换，规整后不足半组才从假人补齐。 / Binds a fake player and toggles middle-click pickup; if another hotbar slot has the target item while the main-hand slot does not, those slots are swapped first, then fake-player stock is used only when still below half a stack. |
| `/player <fakeName> restock <true\|false>` | 绑定假人并开关右键自动补货；默认关闭，其它快捷栏有目标物品而主手没有时先互换，规整后不足半组再从假人补齐。 / Binds a fake player and toggles right-click auto-restock; it is disabled by default, swaps another matching hotbar slot with the main-hand slot first when needed, and refills from the fake player only when still below half a stack. |
| `/player <fakeName> trashcan` | 开启假人远程垃圾桶模式；假人获得任何可管理物品后自动丢出全部物品。 / Enables remote trashcan mode; when the fake player receives any managed item, it automatically drops all managed items. |
| `/player <fakeName> trashcan off` | 关闭假人远程垃圾桶模式。 / Disables remote trashcan mode for the fake player. |

`[num]` can be omitted for `1`, set to `1-64`, or set to `all`. / `[num]` 可省略为 `1`，也可以填写 `1-64` 或 `all`。


---

# CarpetPlayerAddition v1.1.1

## Version Update - English

CarpetPlayerAddition v1.1.1 expands the wireless-terminal-like feature line with automatic restocking and a refined middle-click pickup rule set.

The new `/player <fakeName> restock <true|false>` command binds the executing player to a Carpet fake player and toggles automatic restocking. Restock is disabled by default in every game mode and only changes when the player explicitly runs the command. When enabled, right-click use from the selected hotbar slot or offhand schedules a server-side check after the vanilla use action. The mod first organizes matching items already carried by the player. If another hotbar slot already contains the target item but the selected main-hand hotbar slot does not, those two hotbar slots are swapped first. If no hotbar slot contains it but the offhand already does, matching inventory stacks are consolidated into the offhand. Otherwise the mod uses an empty hotbar slot first, and only uses the offhand when no hotbar slot can be used. If the organized stack is still below half a stack, items are pulled from the bound fake player's managed inventory and ender chest until it reaches half a stack. Dropping items does not trigger this feature.

If the fake player does not have enough matching items, the player still receives an actionbar message explaining that only a partial refill was possible or that no stock was available. This keeps the behavior visible without silently failing.

Middle-click pickup is also refined. It no longer searches armor slots. When another hotbar slot already contains the target item and the selected main-hand hotbar slot does not, pickup swaps those two hotbar slots first. If no hotbar slot contains the target item but the offhand does, inventory stacks are consolidated into the offhand. If neither hotbar nor offhand already contains the target item, an empty hotbar slot is preferred and the offhand is used only as fallback. If the consolidated local amount is still below half a stack, the missing amount is pulled from the fake player.

## 版本更新内容 - 中文

CarpetPlayerAddition v1.1.1 继续扩展仿无线终端功能，新增右键自动补货，并细化中键取物与自动补货的本地物品规整规则。

新增 `/player <fakeName> restock <true|false>` 指令，用于把执行指令的玩家绑定到指定 Carpet 假人，并开关右键自动补货。自动补货在所有游戏模式下默认关闭，不会像 pick 一样随创造/生存自动切换，必须由玩家手动开启或关闭。开启后，玩家右键使用当前快捷栏或副手物品时，模组会在原版使用动作完成后的服务器下一 tick 检查目标物品；它会先规整玩家身上已有的同类物品。如果其它快捷栏已有目标物品、但当前主手快捷栏没有目标物品，会先把这两个快捷栏互换；如果快捷栏没有目标物品但副手已有目标物品，就把背包中的同类物品规整到副手；否则优先使用空快捷栏，快捷栏不可用时才使用副手。规整后的数量仍不足半组时，再从绑定假人的可管理物品栏和末影箱中补到半组。按 Q 丢弃物品不会触发补货。

如果假人物品不足，玩家会收到字幕提示，说明只能部分补货或没有库存，不会静默失败。

中键取物逻辑也同步修改：不再搜索盔甲栏。如果其它快捷栏已有目标物品、但当前主手快捷栏没有目标物品，会先把这两个快捷栏互换；如果快捷栏没有目标物品但副手已有目标物品，则把背包中的同类物品规整到副手；如果快捷栏和副手都没有目标物品，则优先使用空快捷栏，快捷栏不可用才使用副手。规整后如果总数仍不足半组，才从假人补齐缺口。

## 指令总表 / Command Table

| 指令 / Command | 注释 / Note |
|---|---|
| `/player <fakeName> hotbar <1-9> inventory <1-27>` | 交换假人快捷栏与背包储物格。 / Swaps a fake-player hotbar slot with an inventory storage slot. |
| `/player <fakeName> hotbar <1-9> enderchest <1-27>` | 交换假人快捷栏与末影箱格子。 / Swaps a fake-player hotbar slot with an ender chest slot. |
| `/player <fakeName> offhand inventory <1-27>` | 交换假人副手与背包储物格。 / Swaps the fake player's offhand with an inventory storage slot. |
| `/player <fakeName> offhand enderchest <1-27>` | 交换假人副手与末影箱格子。 / Swaps the fake player's offhand with an ender chest slot. |
| `/player <fakeName> head inventory <1-27>` | 交换假人头部装备槽与背包储物格。 / Swaps the fake player's head equipment slot with an inventory storage slot. |
| `/player <fakeName> head enderchest <1-27>` | 交换假人头部装备槽与末影箱格子。 / Swaps the fake player's head equipment slot with an ender chest slot. |
| `/player <fakeName> chest inventory <1-27>` | 交换假人胸甲槽与背包储物格。 / Swaps the fake player's chest equipment slot with an inventory storage slot. |
| `/player <fakeName> chest enderchest <1-27>` | 交换假人胸甲槽与末影箱格子。 / Swaps the fake player's chest equipment slot with an ender chest slot. |
| `/player <fakeName> legs inventory <1-27>` | 交换假人护腿槽与背包储物格。 / Swaps the fake player's legs equipment slot with an inventory storage slot. |
| `/player <fakeName> legs enderchest <1-27>` | 交换假人护腿槽与末影箱格子。 / Swaps the fake player's legs equipment slot with an ender chest slot. |
| `/player <fakeName> feet inventory <1-27>` | 交换假人靴子槽与背包储物格。 / Swaps the fake player's feet equipment slot with an inventory storage slot. |
| `/player <fakeName> feet enderchest <1-27>` | 交换假人靴子槽与末影箱格子。 / Swaps the fake player's feet equipment slot with an ender chest slot. |
| `/player <fakeName> ite <1-27> <1-27>` | 交换假人背包储物格与末影箱格子。 / Swaps a fake-player inventory storage slot with an ender chest slot. |
| `/player <fakeName> drop hotbar <1-9> [num]` | 从假人指定快捷栏槽位丢出物品。 / Drops items from one fake-player hotbar slot. |
| `/player <fakeName> drop hotbar all` | 丢出假人快捷栏 9 格中的全部物品。 / Drops all items from the fake player's 9 hotbar slots. |
| `/player <fakeName> drop offhand [num]` | 从假人副手丢出物品。 / Drops items from the fake player's offhand. |
| `/player <fakeName> drop head [num]` | 从假人头部装备槽丢出物品。 / Drops items from the fake player's head equipment slot. |
| `/player <fakeName> drop chest [num]` | 从假人胸甲槽丢出物品。 / Drops items from the fake player's chest equipment slot. |
| `/player <fakeName> drop legs [num]` | 从假人护腿槽丢出物品。 / Drops items from the fake player's legs equipment slot. |
| `/player <fakeName> drop feet [num]` | 从假人靴子槽丢出物品。 / Drops items from the fake player's feet equipment slot. |
| `/player <fakeName> drop inventory <1-27> [num]` | 从假人背包储物格丢出物品。 / Drops items from one fake-player inventory storage slot. |
| `/player <fakeName> drop enderchest <1-27> [num]` | 从假人末影箱格子丢出物品。 / Drops items from one fake-player ender chest slot. |
| `/player <fakeName> drop inventory all` | 丢出假人物品栏范围，不含末影箱。 / Drops the fake player's inventory range, excluding the ender chest. |
| `/player <fakeName> drop enderchest all` | 丢出假人末影箱全部物品。 / Drops all items from the fake player's ender chest. |
| `/player <fakeName> drop all` | 丢出假人可管理范围内全部物品。 / Drops all managed fake-player items. |
| `/player <fakeName> give <playerName> hotbar <1-9> [num]` | 把假人指定快捷栏槽位物品转移给玩家。 / Gives items from one fake-player hotbar slot to a player. |
| `/player <fakeName> give <playerName> hotbar all` | 把假人快捷栏 9 格物品转移给玩家。 / Gives all items from the fake player's 9 hotbar slots to a player. |
| `/player <fakeName> give <playerName> offhand [num]` | 把假人副手物品转移给玩家。 / Gives items from the fake player's offhand to a player. |
| `/player <fakeName> give <playerName> head [num]` | 把假人头部装备槽物品转移给玩家。 / Gives items from the fake player's head equipment slot to a player. |
| `/player <fakeName> give <playerName> chest [num]` | 把假人胸甲槽物品转移给玩家。 / Gives items from the fake player's chest equipment slot to a player. |
| `/player <fakeName> give <playerName> legs [num]` | 把假人护腿槽物品转移给玩家。 / Gives items from the fake player's legs equipment slot to a player. |
| `/player <fakeName> give <playerName> feet [num]` | 把假人靴子槽物品转移给玩家。 / Gives items from the fake player's feet equipment slot to a player. |
| `/player <fakeName> give <playerName> inventory <1-27> [num]` | 把假人背包储物格物品转移给玩家。 / Gives items from one fake-player inventory storage slot to a player. |
| `/player <fakeName> give <playerName> enderchest <1-27> [num]` | 把假人末影箱格子物品转移给玩家。 / Gives items from one fake-player ender chest slot to a player. |
| `/player <fakeName> give <playerName> inventory all` | 把假人物品栏范围转移给玩家，不含末影箱。 / Gives the fake player's inventory range to a player, excluding the ender chest. |
| `/player <fakeName> give <playerName> enderchest all` | 把假人末影箱全部物品转移给玩家。 / Gives all items from the fake player's ender chest to a player. |
| `/player <fakeName> give <playerName> all` | 把假人可管理范围内全部物品转移给玩家。 / Gives all managed fake-player items to a player. |
| `/player <fakeName> take <playerName> hotbar <1-9> [num]` | 假人从玩家指定快捷栏槽位拿取物品。 / The fake player takes items from one player hotbar slot. |
| `/player <fakeName> take <playerName> hotbar all` | 假人从玩家快捷栏 9 格拿取全部物品。 / The fake player takes all items from the player's 9 hotbar slots. |
| `/player <fakeName> take <playerName> offhand [num]` | 假人从玩家副手拿取物品。 / The fake player takes items from the player's offhand. |
| `/player <fakeName> take <playerName> head [num]` | 假人从玩家头部装备槽拿取物品。 / The fake player takes items from the player's head equipment slot. |
| `/player <fakeName> take <playerName> chest [num]` | 假人从玩家胸甲槽拿取物品。 / The fake player takes items from the player's chest equipment slot. |
| `/player <fakeName> take <playerName> legs [num]` | 假人从玩家护腿槽拿取物品。 / The fake player takes items from the player's legs equipment slot. |
| `/player <fakeName> take <playerName> feet [num]` | 假人从玩家靴子槽拿取物品。 / The fake player takes items from the player's feet equipment slot. |
| `/player <fakeName> take <playerName> inventory <1-27> [num]` | 假人从玩家背包储物格拿取物品。 / The fake player takes items from one player inventory storage slot. |
| `/player <fakeName> take <playerName> enderchest <1-27> [num]` | 假人从玩家末影箱格子拿取物品。 / The fake player takes items from one player ender chest slot. |
| `/player <fakeName> take <playerName> inventory all` | 假人从玩家物品栏范围拿取物品，不含末影箱。 / The fake player takes the player's inventory range, excluding the ender chest. |
| `/player <fakeName> take <playerName> enderchest all` | 假人从玩家末影箱拿取全部物品。 / The fake player takes all items from the player's ender chest. |
| `/player <fakeName> take <playerName> all` | 假人从玩家可管理范围内拿取全部物品。 / The fake player takes all managed player items. |
| `/player <fakeName> pick <true\|false>` | 绑定假人并开关中键取物；其它快捷栏有目标物品而主手没有时先互换，规整后不足半组才从假人补齐。 / Binds a fake player and toggles middle-click pickup; if another hotbar slot has the target item while the main-hand slot does not, those slots are swapped first, then fake-player stock is used only when still below half a stack. |
| `/player <fakeName> restock <true\|false>` | 绑定假人并开关右键自动补货；默认关闭，其它快捷栏有目标物品而主手没有时先互换，规整后不足半组再从假人补齐。 / Binds a fake player and toggles right-click auto-restock; it is disabled by default, swaps another matching hotbar slot with the main-hand slot first when needed, and refills from the fake player only when still below half a stack. |

`[num]` can be omitted for `1`, set to `1-64`, or set to `all`. / `[num]` 可省略为 `1`，也可以填写 `1-64` 或 `all`。


---

# CarpetPlayerAddition v1.1.0

## Version Update - English

CarpetPlayerAddition v1.1.0 adds the first wireless-terminal-like pick feature for Carpet fake players.

Because the pick feature has to capture the player's middle-click input on the client and then ask the server to move items from the bound fake player, v1.1.0 should be installed on both the client and the server when using `/player <fakeName> pick <true|false>`. The other `/player` inventory-management commands are still server-side command features, but this version is published as a client-and-server mod so the middle-click workflow can work in singleplayer, LAN, and multiplayer.

The new `/player <fakeName> pick <true|false>` command binds the executing player to a specified fake player and toggles middle-click item pickup. When enabled, middle-clicking a block in survival sends the block's corresponding item request to the server. The server first checks the player's own offhand, hotbar, and main inventory for the same item before touching the bound fake player.

If the player already has at least half a stack of the requested item, the mod does not take anything from the fake player. It only swaps the player's selected hotbar slot with the slot that already contains the item. If the player has the item but less than half a stack, that slot is first swapped into the selected hotbar slot, then the mod only pulls the missing amount from the fake player until the selected stack reaches half a stack. If the player has none of the item, the mod behaves like the earlier pick behavior and tries to fetch up to half a stack from the fake player. If the player has none of the item and has no inventory capacity, no item is taken from the fake player and nothing is dropped on the ground. Creative mode automatically disables this pick feature, while switching back to survival automatically enables it again for players who already have a fake-player binding.

## 版本更新内容 - 中文

CarpetPlayerAddition v1.1.0 加入了第一批仿无线终端体验的中键取物功能。

由于中键取物必须在客户端捕获玩家的鼠标中键输入，再把请求发给服务器，由服务器从绑定假人身上转移物品，所以使用 `/player <fakeName> pick <true|false>` 时，v1.1.0 需要客户端和服务端都安装。其它 `/player` 库存管理指令本质上仍是服务端命令功能，但这个版本会按客户端 + 服务端版本发布，以保证中键流程在单人、局域网和多人服务器中都能工作。

新增 `/player <fakeName> pick <true|false>` 指令，用来把执行指令的玩家绑定到指定假人，并开关中键取物功能。功能开启后，玩家在生存模式下用鼠标中键点击方块时，客户端会把对应方块物品请求发送给服务器。服务器会先检查玩家自己的副手、快捷栏和主背包里是否已有相同物品，然后才会考虑从绑定假人身上取物。

如果玩家已经拥有数量大于等于半组的目标物品，模组不会从假人处取任何物品，只会把玩家当前主手所在快捷栏与已有目标物品的槽位交换。如果玩家已有目标物品但不足半组，会先把该槽位交换到主手，再只从假人处补足缺少的数量，直到主手达到半组。如果玩家完全没有目标物品，则沿用之前的逻辑，尝试从假人处取最多半组。若玩家完全没有目标物品且背包没有可用容量，则不会从假人取物，也不会把物品丢在地上。切换到创造模式会自动关闭该功能，切回生存模式会对已经绑定过假人的玩家自动开启。

## 指令总表 / Command Table

| 指令 / Command | 注释 / Note |
|---|---|
| `/player <fakeName> hotbar <1-9> inventory <1-27>` | 交换假人快捷栏与背包储物格。 / Swaps a fake-player hotbar slot with an inventory storage slot. |
| `/player <fakeName> hotbar <1-9> enderchest <1-27>` | 交换假人快捷栏与末影箱格子。 / Swaps a fake-player hotbar slot with an ender chest slot. |
| `/player <fakeName> offhand inventory <1-27>` | 交换假人副手与背包储物格。 / Swaps the fake player's offhand with an inventory storage slot. |
| `/player <fakeName> offhand enderchest <1-27>` | 交换假人副手与末影箱格子。 / Swaps the fake player's offhand with an ender chest slot. |
| `/player <fakeName> head inventory <1-27>` | 交换假人头部装备槽与背包储物格。 / Swaps the fake player's head equipment slot with an inventory storage slot. |
| `/player <fakeName> head enderchest <1-27>` | 交换假人头部装备槽与末影箱格子。 / Swaps the fake player's head equipment slot with an ender chest slot. |
| `/player <fakeName> chest inventory <1-27>` | 交换假人胸甲槽与背包储物格。 / Swaps the fake player's chest equipment slot with an inventory storage slot. |
| `/player <fakeName> chest enderchest <1-27>` | 交换假人胸甲槽与末影箱格子。 / Swaps the fake player's chest equipment slot with an ender chest slot. |
| `/player <fakeName> legs inventory <1-27>` | 交换假人护腿槽与背包储物格。 / Swaps the fake player's legs equipment slot with an inventory storage slot. |
| `/player <fakeName> legs enderchest <1-27>` | 交换假人护腿槽与末影箱格子。 / Swaps the fake player's legs equipment slot with an ender chest slot. |
| `/player <fakeName> feet inventory <1-27>` | 交换假人靴子槽与背包储物格。 / Swaps the fake player's feet equipment slot with an inventory storage slot. |
| `/player <fakeName> feet enderchest <1-27>` | 交换假人靴子槽与末影箱格子。 / Swaps the fake player's feet equipment slot with an ender chest slot. |
| `/player <fakeName> ite <1-27> <1-27>` | 交换假人背包储物格与末影箱格子。 / Swaps a fake-player inventory storage slot with an ender chest slot. |
| `/player <fakeName> drop hotbar <1-9> [num]` | 从假人指定快捷栏槽位丢出物品。 / Drops items from one fake-player hotbar slot. |
| `/player <fakeName> drop hotbar all` | 丢出假人快捷栏 9 格中的全部物品。 / Drops all items from the fake player's 9 hotbar slots. |
| `/player <fakeName> drop offhand [num]` | 从假人副手丢出物品。 / Drops items from the fake player's offhand. |
| `/player <fakeName> drop head [num]` | 从假人头部装备槽丢出物品。 / Drops items from the fake player's head equipment slot. |
| `/player <fakeName> drop chest [num]` | 从假人胸甲槽丢出物品。 / Drops items from the fake player's chest equipment slot. |
| `/player <fakeName> drop legs [num]` | 从假人护腿槽丢出物品。 / Drops items from the fake player's legs equipment slot. |
| `/player <fakeName> drop feet [num]` | 从假人靴子槽丢出物品。 / Drops items from the fake player's feet equipment slot. |
| `/player <fakeName> drop inventory <1-27> [num]` | 从假人背包储物格丢出物品。 / Drops items from one fake-player inventory storage slot. |
| `/player <fakeName> drop enderchest <1-27> [num]` | 从假人末影箱格子丢出物品。 / Drops items from one fake-player ender chest slot. |
| `/player <fakeName> drop inventory all` | 丢出假人物品栏范围，不含末影箱。 / Drops the fake player's inventory range, excluding the ender chest. |
| `/player <fakeName> drop enderchest all` | 丢出假人末影箱全部物品。 / Drops all items from the fake player's ender chest. |
| `/player <fakeName> drop all` | 丢出假人可管理范围内全部物品。 / Drops all managed fake-player items. |
| `/player <fakeName> give <playerName> hotbar <1-9> [num]` | 把假人指定快捷栏槽位物品转移给玩家。 / Gives items from one fake-player hotbar slot to a player. |
| `/player <fakeName> give <playerName> hotbar all` | 把假人快捷栏 9 格物品转移给玩家。 / Gives all items from the fake player's 9 hotbar slots to a player. |
| `/player <fakeName> give <playerName> offhand [num]` | 把假人副手物品转移给玩家。 / Gives items from the fake player's offhand to a player. |
| `/player <fakeName> give <playerName> head [num]` | 把假人头部装备槽物品转移给玩家。 / Gives items from the fake player's head equipment slot to a player. |
| `/player <fakeName> give <playerName> chest [num]` | 把假人胸甲槽物品转移给玩家。 / Gives items from the fake player's chest equipment slot to a player. |
| `/player <fakeName> give <playerName> legs [num]` | 把假人护腿槽物品转移给玩家。 / Gives items from the fake player's legs equipment slot to a player. |
| `/player <fakeName> give <playerName> feet [num]` | 把假人靴子槽物品转移给玩家。 / Gives items from the fake player's feet equipment slot to a player. |
| `/player <fakeName> give <playerName> inventory <1-27> [num]` | 把假人背包储物格物品转移给玩家。 / Gives items from one fake-player inventory storage slot to a player. |
| `/player <fakeName> give <playerName> enderchest <1-27> [num]` | 把假人末影箱格子物品转移给玩家。 / Gives items from one fake-player ender chest slot to a player. |
| `/player <fakeName> give <playerName> inventory all` | 把假人物品栏范围转移给玩家，不含末影箱。 / Gives the fake player's inventory range to a player, excluding the ender chest. |
| `/player <fakeName> give <playerName> enderchest all` | 把假人末影箱全部物品转移给玩家。 / Gives all items from the fake player's ender chest to a player. |
| `/player <fakeName> give <playerName> all` | 把假人可管理范围内全部物品转移给玩家。 / Gives all managed fake-player items to a player. |
| `/player <fakeName> take <playerName> hotbar <1-9> [num]` | 假人从玩家指定快捷栏槽位拿取物品。 / The fake player takes items from one player hotbar slot. |
| `/player <fakeName> take <playerName> hotbar all` | 假人从玩家快捷栏 9 格拿取全部物品。 / The fake player takes all items from the player's 9 hotbar slots. |
| `/player <fakeName> take <playerName> offhand [num]` | 假人从玩家副手拿取物品。 / The fake player takes items from the player's offhand. |
| `/player <fakeName> take <playerName> head [num]` | 假人从玩家头部装备槽拿取物品。 / The fake player takes items from the player's head equipment slot. |
| `/player <fakeName> take <playerName> chest [num]` | 假人从玩家胸甲槽拿取物品。 / The fake player takes items from the player's chest equipment slot. |
| `/player <fakeName> take <playerName> legs [num]` | 假人从玩家护腿槽拿取物品。 / The fake player takes items from the player's legs equipment slot. |
| `/player <fakeName> take <playerName> feet [num]` | 假人从玩家靴子槽拿取物品。 / The fake player takes items from the player's feet equipment slot. |
| `/player <fakeName> take <playerName> inventory <1-27> [num]` | 假人从玩家背包储物格拿取物品。 / The fake player takes items from one player inventory storage slot. |
| `/player <fakeName> take <playerName> enderchest <1-27> [num]` | 假人从玩家末影箱格子拿取物品。 / The fake player takes items from one player ender chest slot. |
| `/player <fakeName> take <playerName> inventory all` | 假人从玩家物品栏范围拿取物品，不含末影箱。 / The fake player takes the player's inventory range, excluding the ender chest. |
| `/player <fakeName> take <playerName> enderchest all` | 假人从玩家末影箱拿取全部物品。 / The fake player takes all items from the player's ender chest. |
| `/player <fakeName> take <playerName> all` | 假人从玩家可管理范围内拿取全部物品。 / The fake player takes all managed player items. |
| `/player <fakeName> pick <true\|false>` | 绑定假人并开关中键取物；优先切换玩家已有物品，不足半组才从假人补齐，满包且无目标物品时不取物。 / Binds a fake player and toggles middle-click pickup; it prefers the player's existing matching item, only tops up from the fake player when below half a stack, and takes nothing if the player has no matching item and no capacity. |

`[num]` can be omitted for `1`, set to `1-64`, or set to `all`. / `[num]` 可省略为 `1`，也可以填写 `1-64` 或 `all`。


---

# CarpetPlayerAddition v1.0.1

## Version Update - English

CarpetPlayerAddition v1.0.1 expands the item transfer command set for Carpet fake players.

The main new feature is the `take` command family. It works as the reverse direction of `give`: instead of moving items from a fake player to an online player, `take` lets the fake player take items from an online player. The command suffixes intentionally mirror `give`, so players can use the same slot names, number rules, and range rules with a different transfer direction.

This version also adds batch hotbar operations for all non-swap item-moving commands. `drop`, `give`, and `take` now all support `hotbar all`, which applies the operation to the fake player's or source player's full hotbar from slot 1 to slot 9. Swap commands are unchanged; there is no batch hotbar swap command.

For `take`, the source player must be online. Items are removed from the source player's selected slot or range, then inserted into the fake player's normal inventory. If the fake player's inventory cannot accept all items, the remaining items are dropped near the fake player. Empty source slots are treated as a successful no-op, matching the existing empty-slot behavior of other commands.

## 版本更新内容 - 中文

CarpetPlayerAddition v1.0.1 扩展了 Carpet 假人的物品转移指令组。

本版本的主要新增功能是 `take` 指令系列。它是 `give` 的反方向：`give` 是把假人物品转移给在线玩家，而 `take` 是让假人从在线玩家身上拿取物品。`take` 的后缀结构刻意与 `give` 保持一致，因此槽位名称、数量规则、范围规则都可以沿用，只是转移方向相反。

本版本还为所有非交换类物品移动指令增加了快捷栏批量操作。`drop`、`give`、`take` 现在都支持 `hotbar all`，会一次性处理快捷栏 1 到 9 共 9 个槽位。交换指令保持不变，没有新增快捷栏批量交换指令。

对于 `take`，来源玩家必须在线。物品会从来源玩家指定槽位或范围中扣除，然后优先插入假人的普通背包；如果假人背包无法完全接收，剩余物品会掉落在假人附近。来源槽位为空时视为成功 no-op，与已有命令的空槽行为保持一致。

## 指令总表 / Command Table

| 指令 / Command | 注释 / Note |
|---|---|
| `/player <fakeName> hotbar <1-9> inventory <1-27>` | 交换假人快捷栏与背包储物格。 / Swaps a fake-player hotbar slot with an inventory storage slot. |
| `/player <fakeName> hotbar <1-9> enderchest <1-27>` | 交换假人快捷栏与末影箱格子。 / Swaps a fake-player hotbar slot with an ender chest slot. |
| `/player <fakeName> offhand inventory <1-27>` | 交换假人副手与背包储物格。 / Swaps the fake player's offhand with an inventory storage slot. |
| `/player <fakeName> offhand enderchest <1-27>` | 交换假人副手与末影箱格子。 / Swaps the fake player's offhand with an ender chest slot. |
| `/player <fakeName> head inventory <1-27>` | 交换假人头部装备槽与背包储物格。 / Swaps the fake player's head equipment slot with an inventory storage slot. |
| `/player <fakeName> head enderchest <1-27>` | 交换假人头部装备槽与末影箱格子。 / Swaps the fake player's head equipment slot with an ender chest slot. |
| `/player <fakeName> chest inventory <1-27>` | 交换假人胸甲槽与背包储物格。 / Swaps the fake player's chest equipment slot with an inventory storage slot. |
| `/player <fakeName> chest enderchest <1-27>` | 交换假人胸甲槽与末影箱格子。 / Swaps the fake player's chest equipment slot with an ender chest slot. |
| `/player <fakeName> legs inventory <1-27>` | 交换假人护腿槽与背包储物格。 / Swaps the fake player's legs equipment slot with an inventory storage slot. |
| `/player <fakeName> legs enderchest <1-27>` | 交换假人护腿槽与末影箱格子。 / Swaps the fake player's legs equipment slot with an ender chest slot. |
| `/player <fakeName> feet inventory <1-27>` | 交换假人靴子槽与背包储物格。 / Swaps the fake player's feet equipment slot with an inventory storage slot. |
| `/player <fakeName> feet enderchest <1-27>` | 交换假人靴子槽与末影箱格子。 / Swaps the fake player's feet equipment slot with an ender chest slot. |
| `/player <fakeName> ite <1-27> <1-27>` | 交换假人背包储物格与末影箱格子。 / Swaps a fake-player inventory storage slot with an ender chest slot. |
| `/player <fakeName> drop hotbar <1-9> [num]` | 从假人指定快捷栏槽位丢出物品。 / Drops items from one fake-player hotbar slot. |
| `/player <fakeName> drop hotbar all` | 丢出假人快捷栏 9 格中的全部物品。 / Drops all items from the fake player's 9 hotbar slots. |
| `/player <fakeName> drop offhand [num]` | 从假人副手丢出物品。 / Drops items from the fake player's offhand. |
| `/player <fakeName> drop head [num]` | 从假人头部装备槽丢出物品。 / Drops items from the fake player's head equipment slot. |
| `/player <fakeName> drop chest [num]` | 从假人胸甲槽丢出物品。 / Drops items from the fake player's chest equipment slot. |
| `/player <fakeName> drop legs [num]` | 从假人护腿槽丢出物品。 / Drops items from the fake player's legs equipment slot. |
| `/player <fakeName> drop feet [num]` | 从假人靴子槽丢出物品。 / Drops items from the fake player's feet equipment slot. |
| `/player <fakeName> drop inventory <1-27> [num]` | 从假人背包储物格丢出物品。 / Drops items from one fake-player inventory storage slot. |
| `/player <fakeName> drop enderchest <1-27> [num]` | 从假人末影箱格子丢出物品。 / Drops items from one fake-player ender chest slot. |
| `/player <fakeName> drop inventory all` | 丢出假人物品栏范围，不含末影箱。 / Drops the fake player's inventory range, excluding the ender chest. |
| `/player <fakeName> drop enderchest all` | 丢出假人末影箱全部物品。 / Drops all items from the fake player's ender chest. |
| `/player <fakeName> drop all` | 丢出假人可管理范围内全部物品。 / Drops all managed fake-player items. |
| `/player <fakeName> give <playerName> hotbar <1-9> [num]` | 把假人指定快捷栏槽位物品转移给玩家。 / Gives items from one fake-player hotbar slot to a player. |
| `/player <fakeName> give <playerName> hotbar all` | 把假人快捷栏 9 格物品转移给玩家。 / Gives all items from the fake player's 9 hotbar slots to a player. |
| `/player <fakeName> give <playerName> offhand [num]` | 把假人副手物品转移给玩家。 / Gives items from the fake player's offhand to a player. |
| `/player <fakeName> give <playerName> head [num]` | 把假人头部装备槽物品转移给玩家。 / Gives items from the fake player's head equipment slot to a player. |
| `/player <fakeName> give <playerName> chest [num]` | 把假人胸甲槽物品转移给玩家。 / Gives items from the fake player's chest equipment slot to a player. |
| `/player <fakeName> give <playerName> legs [num]` | 把假人护腿槽物品转移给玩家。 / Gives items from the fake player's legs equipment slot to a player. |
| `/player <fakeName> give <playerName> feet [num]` | 把假人靴子槽物品转移给玩家。 / Gives items from the fake player's feet equipment slot to a player. |
| `/player <fakeName> give <playerName> inventory <1-27> [num]` | 把假人背包储物格物品转移给玩家。 / Gives items from one fake-player inventory storage slot to a player. |
| `/player <fakeName> give <playerName> enderchest <1-27> [num]` | 把假人末影箱格子物品转移给玩家。 / Gives items from one fake-player ender chest slot to a player. |
| `/player <fakeName> give <playerName> inventory all` | 把假人物品栏范围转移给玩家，不含末影箱。 / Gives the fake player's inventory range to a player, excluding the ender chest. |
| `/player <fakeName> give <playerName> enderchest all` | 把假人末影箱全部物品转移给玩家。 / Gives all items from the fake player's ender chest to a player. |
| `/player <fakeName> give <playerName> all` | 把假人可管理范围内全部物品转移给玩家。 / Gives all managed fake-player items to a player. |
| `/player <fakeName> take <playerName> hotbar <1-9> [num]` | 假人从玩家指定快捷栏槽位拿取物品。 / The fake player takes items from one player hotbar slot. |
| `/player <fakeName> take <playerName> hotbar all` | 假人从玩家快捷栏 9 格拿取全部物品。 / The fake player takes all items from the player's 9 hotbar slots. |
| `/player <fakeName> take <playerName> offhand [num]` | 假人从玩家副手拿取物品。 / The fake player takes items from the player's offhand. |
| `/player <fakeName> take <playerName> head [num]` | 假人从玩家头部装备槽拿取物品。 / The fake player takes items from the player's head equipment slot. |
| `/player <fakeName> take <playerName> chest [num]` | 假人从玩家胸甲槽拿取物品。 / The fake player takes items from the player's chest equipment slot. |
| `/player <fakeName> take <playerName> legs [num]` | 假人从玩家护腿槽拿取物品。 / The fake player takes items from the player's legs equipment slot. |
| `/player <fakeName> take <playerName> feet [num]` | 假人从玩家靴子槽拿取物品。 / The fake player takes items from the player's feet equipment slot. |
| `/player <fakeName> take <playerName> inventory <1-27> [num]` | 假人从玩家背包储物格拿取物品。 / The fake player takes items from one player inventory storage slot. |
| `/player <fakeName> take <playerName> enderchest <1-27> [num]` | 假人从玩家末影箱格子拿取物品。 / The fake player takes items from one player ender chest slot. |
| `/player <fakeName> take <playerName> inventory all` | 假人从玩家物品栏范围拿取物品，不含末影箱。 / The fake player takes the player's inventory range, excluding the ender chest. |
| `/player <fakeName> take <playerName> enderchest all` | 假人从玩家末影箱拿取全部物品。 / The fake player takes all items from the player's ender chest. |
| `/player <fakeName> take <playerName> all` | 假人从玩家可管理范围内拿取全部物品。 / The fake player takes all managed player items. |

`[num]` can be omitted for `1`, set to `1-64`, or set to `all`. / `[num]` 可省略为 `1`，也可以填写 `1-64` 或 `all`。


---

# CarpetPlayerAddition v1.0.0

## English

Version 1.0.0 provides three groups of fake-player item commands: drop, swap, and give.

In the command examples below, `<fakeName>` is the Carpet fake player name, and `<playerName>` is the name of an online player.

### drop: make a fake player drop items

The drop commands make the fake player drop items into the world, similar to using the normal item-drop action.

Single-slot commands:

```mcfunction
/player <fakeName> drop hotbar <1-9> [num]
/player <fakeName> drop offhand [num]
/player <fakeName> drop head [num]
/player <fakeName> drop chest [num]
/player <fakeName> drop legs [num]
/player <fakeName> drop feet [num]
/player <fakeName> drop inventory <1-27> [num]
/player <fakeName> drop enderchest <1-27> [num]
```

`[num]` is optional and defaults to `1`. It can be `1-64` or `all`. If the requested amount is larger than the stack size, or if `all` is used, the whole stack is dropped.

Batch commands:

```mcfunction
/player <fakeName> drop inventory all
/player <fakeName> drop enderchest all
/player <fakeName> drop all
```

- `inventory all`: includes inventory storage, hotbar, offhand, and armor slots, but excludes the ender chest.
- `enderchest all`: includes the fake player’s ender chest.
- `all`: includes both `inventory all` and `enderchest all`.

### swap: exchange fake-player slots

The swap commands exchange items between two fake-player slots. Empty slots can also be swapped.

Hotbar with inventory or ender chest:

```mcfunction
/player <fakeName> hotbar <1-9> inventory <1-27>
/player <fakeName> hotbar <1-9> enderchest <1-27>
```

Equipment with inventory or ender chest:

```mcfunction
/player <fakeName> offhand inventory <1-27>
/player <fakeName> offhand enderchest <1-27>
/player <fakeName> head inventory <1-27>
/player <fakeName> head enderchest <1-27>
/player <fakeName> chest inventory <1-27>
/player <fakeName> chest enderchest <1-27>
/player <fakeName> legs inventory <1-27>
/player <fakeName> legs enderchest <1-27>
/player <fakeName> feet inventory <1-27>
/player <fakeName> feet enderchest <1-27>
```

Inventory with ender chest:

```mcfunction
/player <fakeName> ite <inventorySlot> <enderchestSlot>
```

`ite` means inventory to ender chest. It swaps one fake-player inventory storage slot with one fake-player ender chest slot.

### give: transfer fake-player items to a player

The give commands transfer items from the fake player to an online player. Items are inserted into the target player’s inventory first. If the target inventory is full, the remaining items are dropped near the target player.

Single-slot commands:

```mcfunction
/player <fakeName> give <playerName> hotbar <1-9> [num]
/player <fakeName> give <playerName> offhand [num]
/player <fakeName> give <playerName> head [num]
/player <fakeName> give <playerName> chest [num]
/player <fakeName> give <playerName> legs [num]
/player <fakeName> give <playerName> feet [num]
/player <fakeName> give <playerName> inventory <1-27> [num]
/player <fakeName> give <playerName> enderchest <1-27> [num]
```

`[num]` is optional and defaults to `1`. It can be `1-64` or `all`. If the requested amount is larger than the stack size, or if `all` is used, the whole stack is transferred.

Batch commands:

```mcfunction
/player <fakeName> give <playerName> inventory all
/player <fakeName> give <playerName> enderchest all
/player <fakeName> give <playerName> all
```

- `inventory all`: transfers inventory storage, hotbar, offhand, and armor slots, but excludes the ender chest.
- `enderchest all`: transfers the fake player’s ender chest.
- `all`: transfers both `inventory all` and `enderchest all`.

## 中文翻译

v1.0.0 提供三类假人物品指令：drop、交换、give。

以下命令中的 `<fakeName>` 表示 Carpet 假人名称，`<playerName>` 表示在线玩家名称。

### drop：让假人丢出物品

drop 指令用于让假人像执行普通丢弃物品操作一样，把物品丢到世界中。

单个槽位命令：

```mcfunction
/player <fakeName> drop hotbar <1-9> [num]
/player <fakeName> drop offhand [num]
/player <fakeName> drop head [num]
/player <fakeName> drop chest [num]
/player <fakeName> drop legs [num]
/player <fakeName> drop feet [num]
/player <fakeName> drop inventory <1-27> [num]
/player <fakeName> drop enderchest <1-27> [num]
```

`[num]` 可省略，省略时默认为 `1`；也可以填写 `1-64` 或 `all`。当请求数量超过该槽位物品数量，或填写 `all` 时，会丢出整组物品。

批量命令：

```mcfunction
/player <fakeName> drop inventory all
/player <fakeName> drop enderchest all
/player <fakeName> drop all
```

- `inventory all`：包含物品栏储物格、快捷栏、副手和盔甲槽，不包含末影箱。
- `enderchest all`：包含假人的末影箱。
- `all`：包含 `inventory all` 和 `enderchest all`。

### 交换：交换假人物品槽位

交换指令用于在假人的两个槽位之间交换物品。空槽也可以参与交换。

快捷栏与物品栏或末影箱交换：

```mcfunction
/player <fakeName> hotbar <1-9> inventory <1-27>
/player <fakeName> hotbar <1-9> enderchest <1-27>
```

装备槽与物品栏或末影箱交换：

```mcfunction
/player <fakeName> offhand inventory <1-27>
/player <fakeName> offhand enderchest <1-27>
/player <fakeName> head inventory <1-27>
/player <fakeName> head enderchest <1-27>
/player <fakeName> chest inventory <1-27>
/player <fakeName> chest enderchest <1-27>
/player <fakeName> legs inventory <1-27>
/player <fakeName> legs enderchest <1-27>
/player <fakeName> feet inventory <1-27>
/player <fakeName> feet enderchest <1-27>
```

物品栏与末影箱交换：

```mcfunction
/player <fakeName> ite <inventorySlot> <enderchestSlot>
```

`ite` 表示 inventory to ender chest，用于交换假人的一个物品栏储物格和一个末影箱格子。

### give：把假人物品转移给玩家

give 指令用于把假人的物品转移给在线玩家。物品会优先进入目标玩家背包；如果目标玩家背包已满，剩余物品会掉落在目标玩家附近。

单个槽位命令：

```mcfunction
/player <fakeName> give <playerName> hotbar <1-9> [num]
/player <fakeName> give <playerName> offhand [num]
/player <fakeName> give <playerName> head [num]
/player <fakeName> give <playerName> chest [num]
/player <fakeName> give <playerName> legs [num]
/player <fakeName> give <playerName> feet [num]
/player <fakeName> give <playerName> inventory <1-27> [num]
/player <fakeName> give <playerName> enderchest <1-27> [num]
```

`[num]` 可省略，省略时默认为 `1`；也可以填写 `1-64` 或 `all`。当请求数量超过该槽位物品数量，或填写 `all` 时，会转移整组物品。

批量命令：

```mcfunction
/player <fakeName> give <playerName> inventory all
/player <fakeName> give <playerName> enderchest all
/player <fakeName> give <playerName> all
```

- `inventory all`：转移物品栏储物格、快捷栏、副手和盔甲槽，不包含末影箱。
- `enderchest all`：转移假人的末影箱。
- `all`：转移 `inventory all` 和 `enderchest all`。

## License

本项目采用 MIT License，详见 [LICENSE](./LICENSE)。

本仓库的 Gradle Wrapper 保留其随附的 Apache-2.0 许可，详见 [THIRD_PARTY_NOTICES.md](./THIRD_PARTY_NOTICES.md)。
