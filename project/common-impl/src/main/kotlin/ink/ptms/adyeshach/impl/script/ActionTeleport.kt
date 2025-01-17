package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import org.bukkit.Location
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionTeleport(val location: ParsedAction<*>): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(location).run<Location>().thenAccept {
            val script = frame.script()
            if (script.getManager() == null || !script.isEntitySelected()) {
                errorBy("error-no-manager-or-entity-selected")
            }
            script.getEntities().forEach { e -> e.teleport(it) }
        }
    }

    companion object {

        @KetherParser(["teleport"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            ActionTeleport(it.next(ArgTypes.ACTION))
        }
    }
}