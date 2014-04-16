
import scalaz.{Lens => _, _}
import scalaz.task._

import monocle._

package object introgame {
  type FreeGameIO[A] = Free[GameIO, A]
  
  type Game[A] = StateT[FreeGameIO, GameState, A]
  
  def liftIo[A](v: FreeGameIO[A]): Game[A] = StateT[FreeGameIO, GameState, A](s => v.map((s, _)))
  
  def state[A](f: GameState => (GameState, A)): Game[A] = StateT[FreeGameIO, GameState, A](s => Monad[FreeGameIO].point(f(s)))
  
  def update[A](lens: Lens[GameState, GameState, A, A])(f: A => A): Game[A] = StateT[FreeGameIO, GameState, A](s => Monad[FreeGameIO].point({ 
    val a2 = f(lens.get(s))
    val s2 = lens.set(s, a2)
    (s2, a2)
  }))
  
  def get[A](lens: Lens[GameState, GameState, A, A]): Game[A] = StateT[FreeGameIO, GameState, A](s => Monad[FreeGameIO].point((s, lens.get(s))))
  
  def nothing: Game[Unit] = state[Unit](s => (s, Unit))
  
  implicit class GameOps[A](game: Game[A]) {  
    import GameIO._
    
    def runGame(state: GameState): Task[A] = game.eval(state).runM[Task] {
      case GetLine(f) => Task { f(readLine()) }
      case PutStrLn(s, a) => Task { println(s); a; }
      case Exit => Task { System.exit(0); ??? }
      case Pure(a) => Task { a }
    }
  }
}