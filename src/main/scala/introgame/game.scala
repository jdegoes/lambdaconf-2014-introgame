
package introgame

import scalaz.{Lens => _, _}
import scalaz.syntax._

import monocle._
import monocle.syntax.lens._

object game {
  import GameIO._
  import Command._
  
  val execCommand: Command => Game[_] = {
    def checkedMove(fx: Int => Int, fy: Int => Int): Game[Unit] = for {
      map <- get(GameState.map)
      x   <- get(GameState.player |-> Player.x)
      y   <- get(GameState.player |-> Player.y)
      _   <- if (map.contains(fx(x), fy(y))) update(GameState.player |-> Player.location)(t => (fx(t._1), fy(t._2))) else nothing
    } yield Unit
    
    {
      case Quit => exit
    
      case Look => for {
        t      <- get(GameState.player |-> Player.location)
      
        val (x, y) = t
      
        cell   <- get(GameState.map |-> GameMap.cell(x, y))
        _      <- putStrLn("You are standing in " + cell.name)
        _      <- putStrLn(cell.description)
        _      <- if (cell.items.length > 0) putStrLn("You can see the following: " + cell.items.mkString(", ")) else nothing
      } yield Unit
    
      case North  => checkedMove(identity, _ + 1)
      case South  => checkedMove(identity, _ - 1)
      case West   => checkedMove(_ + 1, identity)
      case East   => checkedMove(_ - 1, identity)
      
      case _ => putStrLn("I'm sorry, I don't understand that right now")
    }
  }
  
  def mainLoop: Game[Unit] = for {
    _    <- putStrLn("What would you like to do now?")
    cmd  <- getLine.map(Command.parse _)    
    _    <- execCommand(cmd)
    _    <- mainLoop
  } yield Unit
}