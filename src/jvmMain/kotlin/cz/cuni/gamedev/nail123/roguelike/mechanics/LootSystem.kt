package cz.cuni.gamedev.nail123.roguelike.mechanics

import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Enemy
import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Orc
import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Rat
import cz.cuni.gamedev.nail123.roguelike.entities.items.Item
import cz.cuni.gamedev.nail123.roguelike.entities.items.Sword
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.Poison
import kotlin.random.Random

object LootSystem {
    interface  ItemDrop{
        fun getItems() : List<Item>
    }

    object NoDrop: ItemDrop {
        override fun getItems(): List<Item> = listOf<Item>()
    }

    class SingleDrop(val instanceItem : () -> Item): ItemDrop {
        override fun getItems(): List<Item> = listOf(instanceItem())
    }

    class TreasureClass(val numDrops: Int, val possibleDrops: List<Pair<Int, ItemDrop>>): ItemDrop {
        val totalProb = possibleDrops.sumOf { it.first }

        override fun getItems(): List<Item> {
            val drops = ArrayList<Item>()
            repeat(numDrops) {
                drops.addAll(pickDrop().getItems())
            }
            return drops
        }

        private fun pickDrop(): ItemDrop {
            val randNumber = Random.Default.nextInt(totalProb)
            for (drop in possibleDrops) {
                if (randNumber < drop.first) return drop.second
            }
            // Never happens, but we need to place something here anyway
            return possibleDrops.last().second
        }
    }

    val rnd = Random.Default

    val simpleSword = SingleDrop{ Sword(rnd.nextInt(3) + 2, Poison(1,1)) }

    val greatSword = SingleDrop{ Sword(rnd.nextInt(5) + 5, Poison(1,1)) }

    val enemyDrops = mapOf(
        Rat::class to TreasureClass(1, listOf(
            2 to NoDrop,
            1 to simpleSword
        )),
        Orc::class to TreasureClass(1, listOf(
            4 to NoDrop,
            2 to simpleSword,
            1 to greatSword
        ))
    )

    fun onDeath(enemy: Enemy){
        val drops = enemyDrops[enemy::class]?.getItems() ?: return
        for (item in drops) {
            enemy.area[enemy.position]?.entities?.add(item)
        }
    }

}