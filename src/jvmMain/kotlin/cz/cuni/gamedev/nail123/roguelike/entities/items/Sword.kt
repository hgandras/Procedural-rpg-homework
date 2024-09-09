package cz.cuni.gamedev.nail123.roguelike.entities.items

import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasInventory
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.Effect
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.NoEffect
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles

class Sword(val attackPower: Int, elementType : Effect  = NoEffect()): Weapon(GameTiles.SWORD, elementType) {
    override fun onEquip(character: HasInventory) {
        if (character is Player) {
            character.attack += attackPower
        }
    }

    override fun onUnequip(character: HasInventory) {
        if (character is Player) {
            character.attack -= attackPower
        }
    }

    override fun toString(): String {
        return "Sword($attackPower)"
    }
}