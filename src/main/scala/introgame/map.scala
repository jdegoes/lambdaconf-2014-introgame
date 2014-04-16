package introgame

import monocle._

case class GameMap[A](value: Vector[Vector[A]]) {
  def cols = value.length
  
  def rows(x: Int): Int = if (x < 0 || x >= cols) 0 else value(x).length
  
  def contains(x: Int, y: Int): Boolean = x >= 0 && x < cols && y >= 0 && y < rows(x)
}

object GameMap {
  def cell[A](x: Int, y: Int): Lens[GameMap[A], GameMap[A], A, A] = {
    Lens[GameMap[A], GameMap[A], A, A](
      _get = map => map.value(x)(y),
      _set = (map, v) => GameMap(map.value.updated(x, map.value(x).updated(y, v)))
    )
  }
}

