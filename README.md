# CarpetPlayerAddition

## Supported Targets

<table>
<thead>
<tr><th>Loader</th><th>Minecraft</th></tr>
</thead>
<tbody>
<tr><td><a href="https://fabricmc.net/">Fabric</a></td><td><a href="https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-6">1.21.6</a></td></tr>
</tbody>
</table>

在 Fabric Carpet 环境中提供玩家假人相关功能。

## Project layout

The buildable project is in [$(@{Loader=fabric; Version=1.21.6; Path=fabric/1.21.6}.Path)/](fabric/1.21.6/). Repository metadata remains at the root.

## Build

Run the Gradle wrapper from $(@{Loader=fabric; Version=1.21.6; Path=fabric/1.21.6}.Path)/:

``text
cd fabric/1.21.6
./gradlew clean build
``

The target uses Fabric for Minecraft 1.21.6. See the project directory for its Java and dependency requirements.
