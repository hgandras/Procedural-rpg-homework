package cz.cuni.gamedev.nail123.roguelike.mechanics

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.entities.enemies.*
import cz.cuni.gamedev.nail123.roguelike.entities.items.DefenseShard
import cz.cuni.gamedev.nail123.roguelike.entities.items.Sword
import cz.cuni.gamedev.nail123.roguelike.entities.objects.Chest
import cz.cuni.gamedev.nail123.roguelike.entities.items.Potion
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.FireEffect
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.NoEffect
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.Poison
import kotlin.random.Random

object LootSystem {
    interface  ItemDrop{
        fun getItems() : List<GameEntity>
    }

    object NoDrop: ItemDrop {
        override fun getItems(): List<GameEntity> = listOf<GameEntity>()
    }

    class SingleDrop(val instanceItem : () -> GameEntity): ItemDrop {
        override fun getItems(): List<GameEntity> = listOf(instanceItem())
    }

    class TreasureClass(val numDrops: Int, val possibleDrops: List<Pair<Int, ItemDrop>>): ItemDrop {
        val totalProb = possibleDrops.sumOf { it.first }

        override fun getItems(): List<GameEntity> {
            val drops = ArrayList<GameEntity>()
            repeat(numDrops) {
                drops.addAll(pickDrop().getItems())
            }
            return drops
        }

        private fun pickDrop(): ItemDrop {
            var randNumber = Random.Default.nextInt(totalProb)
            for (drop in possibleDrops) {
                randNumber -= drop.first
                if (randNumber < 0) return drop.second
            }
            // Never happens, but we need to place something here anyway
            return possibleDrops.last().second
        }
    }

    val rnd = Random.Default

    //Weapons
    val simpleSword = SingleDrop{ Sword(rnd.nextInt(2) + 3, NoEffect(), "Sword") }
    val fireSword = SingleDrop{Sword(rnd.nextInt(2) + 2,FireEffect(1), "Fire Sword")}
    val poisonSword = SingleDrop{Sword(rnd.nextInt(2) + 2, Poison(1), "Poison Sword")}

    val strongSword = SingleDrop{Sword(rnd.nextInt(3) + 4, NoEffect(), "Sword+")}
    val fireStrongSword = SingleDrop{Sword(rnd.nextInt(2) + 2, FireEffect(1), "Fire Sword+")}
    val poisonStrongSword = SingleDrop{Sword(rnd.nextInt(3) + 2,Poison(1), "Poison Sword+")}

    val potion = SingleDrop{ Potion() }
    val defenseShard = SingleDrop{ DefenseShard() }

    val enemyDrops = mapOf(
        Rat::class to TreasureClass(1, listOf(

            1 to fireSword
        )),
        Orc::class to TreasureClass(1, listOf(
            2 to NoDrop,
            5 to potion
        )),
        BossSnake::class to TreasureClass(1, listOf(
            1 to strongSword,
            1 to fireSword,
            1 to poisonSword
        )),
        BossGhost::class to TreasureClass(1, listOf(
            1 to strongSword,
            1 to fireSword,
            1 to poisonSword
        )),
        BossGolem::class to TreasureClass(1, listOf(
            1 to defenseShard,
        ))
    )

    //Chests drop
    val chestDrops = mapOf(
        Chest::class to TreasureClass(1, listOf(
            1 to simpleSword,
            2 to potion
    )))

    fun onDeath(enemy: Enemy){
        val drops = enemyDrops[enemy::class]?.getItems() ?: return
        for (item in drops) {
            enemy.area[enemy.position]?.entities?.add(item)
        }
    }

    fun onInteract(entity : GameEntity){
        val drops = chestDrops[entity::class]?.getItems()?:return
        for(item in drops){
            entity.area[entity.position]?.entities?.add(item)
        }
    }

}