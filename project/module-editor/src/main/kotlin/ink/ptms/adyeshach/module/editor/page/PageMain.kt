package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.sendNativeFullMessage
import ink.ptms.adyeshach.module.editor.sendNativeHalfMessage
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.page.MainPage
 *
 * @author 坏黑
 * @since 2022/12/19 18:22
 */
class PageMain(editor: EditPanel) : Page(editor) {

    val derivedId = entity.getTag(StandardTags.DERIVED)?.toString()

    override fun open(index: Int) {
        super.open(index)
        // ID
        appendId().newLine().newLine()
        when {
            // 无效单位
            entity.isInvalid -> {
                json.append("  ").appendLang("entity-is-invalid")
                // 发送半屏消息
                player.sendNativeHalfMessage(json)
            }
            // 衍生单位
            derivedId != null -> {
                // 对 derivedId 长度超过 20 的进行截断
                val id = if (derivedId.length > 20) {
                    derivedId.substring(0, 20) + "..."
                } else {
                    derivedId
                }
                json.append("  ").appendLang("entity-is-derived", id, derivedId)
                // 发送半屏消息
                player.sendNativeHalfMessage(json)
            }
            else -> {
                json.append("  ")
                // 观察者
                json.appendLang("viewer", entity.viewPlayers.viewers.size).hoverText(hoverViewers())
                // 控制器
                json.append(" ").appendLang("controller", entity.getController().size).hoverText(hoverController()).runCommand("/adyeshach edit ${entity.uniqueId} controller")
                // 标签
                json.append(" ").appendLang("tags", entity.getTags().size).hoverText(hoverTags())
                // 关联
                json.append(" ").appendLang("link", linkedCount()).hoverText(hoverLinked()).newLine().newLine()

                // 特性 & 数据 & 微调
                json.append("  ")
                json.appendLang("entity-public-traits")
                    .hoverText(player.lang("entity-public-traits-help"))
                    .runCommand("/adyeshach edit ${entity.uniqueId} traits")
                json.append(" §8| ")
                json.appendLang("entity-public-meta")
                    .hoverText(player.lang("entity-public-meta-help"))
                    .runCommand("/adyeshach edit ${entity.uniqueId} public-meta")
                json.append(" ")
                json.appendLang("entity-private-meta")
                    .hoverText(player.lang("entity-private-meta-help"))
                    .runCommand("/adyeshach edit ${entity.uniqueId} private-meta")
                json.append(" §8| ")
                json.appendLang("entity-move")
                    .hoverText(player.lang("entity-move-help"))
                    .runCommand("/adyeshach edit ${entity.uniqueId} move")
                    .newLine().newLine()

                // 操作
                json.append("  ")
                (0..6).forEach { i ->
                    val command = when (i) {
                        0 -> "tp ${entity.uniqueId}"
                        1 -> "tp ${entity.uniqueId} here"
                        2 -> "move ${entity.uniqueId} here"
                        3 -> "look ${entity.uniqueId} here"
                        4 -> "look ${entity.uniqueId} like"
                        5 -> "clone ${entity.uniqueId}"
                        6 -> "remove ${entity.uniqueId}"
                        else -> error("Unknown command $i")
                    }
                    json.appendLang("entity-tools-$i")
                        .hoverText(player.lang("entity-tools-$i-help"))
                        .runCommand("/adyeshach api se adyeshach $command")
                    json.append(" ")
                }

                // 展开对话框提示
                json.newLine().newLine().newLine().newLine().newLine()
                json.append("  ").appendLang("open-chat-screen")

                // 发送全屏消息
                player.sendNativeFullMessage(json)
            }
        }
    }

    /** 控制器描述 */
    fun hoverController(): String {
        return entity.getController().map { "&7${it.priority()}. &f${it} &8(${it.group()})" }.colored().joinToString("\n")
    }

    /** 标签描述 */
    fun hoverTags(): String {
        val list = arrayListOf<String>()
        list += entity.getTags().map { "&7${it.key}: &f${it.value}" }
        return list.colored().joinToString("\n")
    }

    /** 关联实体数量 */
    fun linkedCount(): Int {
        return entity.getPassengers().size + if (entity.getVehicle() != null) 1 else 0
    }

    /** 关联实体描述 */
    fun hoverLinked(): String {
        val list = arrayListOf<String>()
        val vehicle = entity.getVehicle()
        if (vehicle != null) {
            list += player.lang("link-vehicle")
            list += "&f - &7${vehicle.id} &8(${vehicle.entityType})"
        }
        val passengers = entity.getPassengers()
        if (passengers.isNotEmpty()) {
            list += player.lang("link-passengers")
            list += passengers.map { "&f - &7${it.id} &8(${it.entityType})" }
        }
        return list.colored().joinToString("\n")
    }

    /** 观察者描述 **/
    fun hoverViewers(): String {
        val list = arrayListOf<String>()
        if (entity.viewPlayers.viewers.isNotEmpty()) {
            list += player.lang("viewer-list-all")
            // 只显示前 8 位
            list += entity.viewPlayers.viewers.take(8).map { "&f - &7${it}" }
            // 如果超过 8 位则显示剩余数量
            if (entity.viewPlayers.viewers.size > 8) {
                list += "&f - &8... ${entity.viewPlayers.viewers.size - 8}"
            }
        }
        if (entity.viewPlayers.visible.isNotEmpty()) {
            list += player.lang("viewer-list-visible")
            // 只显示前 8 位
            list += entity.viewPlayers.visible.take(8).map { "&f - &7${it}" }
            // 如果超过 8 位则显示剩余数量
            if (entity.viewPlayers.visible.size > 8) {
                list += "&f - &8... ${entity.viewPlayers.visible.size - 8}"
            }
        }
        return list.colored().joinToString("\n")
    }

    fun appendId(): TellrawJson {
        json.append("  ")
        json.append("§8§n${entity.uniqueId}").hoverText(player.lang("copy")).suggestCommand(entity.uniqueId)
        json.append(" §7(${entity.index})")
        return json
    }
}