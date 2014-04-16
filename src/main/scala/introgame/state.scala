package introgame

import monocle._

case class Item(name: String) {
  override def toString = name
}

object Item {
  val name: Lens[Item, Item, String, String] = Lens(
    _get = _.name,
    _set = (s, a) => s.copy(name = a)
  )
}

case class Cell(name: String, description: String, items: List[Item])

object Cell {
  val name: Lens[Cell, Cell, String, String] = Lens(
    _get = _.name,
    _set = (s, a) => s.copy(name = a)
  )
  
  val description: Lens[Cell, Cell, String, String] = Lens(
    _get = _.description,
    _set = (s, a) => s.copy(description = a)
  )
  
  val items: Lens[Cell, Cell, List[Item], List[Item]] = Lens(
    _get = _.items,
    _set = (s, a) => s.copy(items = a)
  )
}

case class GameState(map: GameMap[Cell], player: Player)
  
object GameState {
  val map: Lens[GameState, GameState, GameMap[Cell], GameMap[Cell]] = Lens(
    _get = _.map,
    _set = (s, m) => s.copy(map = m)
  )
  
  val player: Lens[GameState, GameState, Player, Player] = Lens(
    _get = _.player,
    _set = (s, a) => s.copy(player = a)
  )
}

case class Player(x: Int, y: Int, health: Int)

object Player {
  val location: Lens[Player, Player, (Int, Int), (Int, Int)] = Lens(
    _get = s => (s.x, s.y),
    _set = (s, a) => s.copy(x = a._1, y = a._2)
  )
  
  val x: Lens[Player, Player, Int, Int] = Lens(
    _get = _.x,
    _set = (s, a) => s.copy(x = a)
  )
  
  val y: Lens[Player, Player, Int, Int] = Lens(
    _get = _.y,
    _set = (s, a) => s.copy(y = a)
  )
  
  val health: Lens[Player, Player, Int, Int] = Lens(
    _get = _.health,
    _set = (s, a) => s.copy(health = a)
  )
}

