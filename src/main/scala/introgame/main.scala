package introgame

import scalaz._
import game._
import testdata._

object Main {
  def game: Game[Unit] = mainLoop
  
  def main(args: Array[String]) {
    game.runGame(GameState(TestMap, Player(x = 0, y = 0, health = 100))).run
  }
}