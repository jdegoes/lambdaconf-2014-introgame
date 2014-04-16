package introgame

import scalaz._

sealed trait GameIO[+A]

object GameIO {
  case class GetLine[+A](f: String => A) extends GameIO[A]
  case class PutStrLn[+A](v: String, a: A) extends GameIO[A]
  case object Exit extends GameIO[Nothing]
  case class Pure[+A](v: A) extends GameIO[A]
  
  implicit val GameIOInstances = new Functor[GameIO] {
    def map[A, B](v: GameIO[A])(f: A => B): GameIO[B] = v match {
      case GetLine(f0) => GetLine(f compose f0)
      case PutStrLn(v, a) => PutStrLn(v, f(a))
      case Exit => Exit
      case Pure(v) => Pure(f(v))
    }
  }
  
  def getLine: Game[String] = liftIo(Free.liftF(GetLine(identity)))
  def putStrLn(str: String): Game[Unit] = liftIo(Free.liftF(PutStrLn(str, Unit)))
  def exit: Game[Unit] = liftIo(Free.liftF(Exit))
  def point[A](v: A): Game[A] = liftIo(Free.liftF(Pure(v)))
}