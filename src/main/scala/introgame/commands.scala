
package introgame

import scala.util.parsing.combinator._

sealed trait Command

object Command extends  {
  private object parser extends RegexParsers {
    lazy val goOpt: Parser[Option[String]] = "go"?
    
    lazy val quit: Parser[Command] = "quit" ^^^ Quit
  
    lazy val look: Parser[Command] = "look" ^^^ Look
    
    lazy val north: Parser[Command] = goOpt ~> "north" ^^^ North
    
    lazy val east: Parser[Command] = goOpt ~> "east" ^^^ East
    
    lazy val south: Parser[Command] = goOpt ~> "south" ^^^ South
    
    lazy val west: Parser[Command] = goOpt ~> "west" ^^^ West
    
    def grammar: Parser[Command] = quit | look | north | east | south | west
  }
  
  import parser._
  
  def parse(line: String): Command = parseAll(grammar, line) match {
    case Success(x, _) => x
    case failure : NoSuccess => Unknown
  }
  
  case object Quit      extends Command
  case object Unknown   extends Command
  case object Look      extends Command
  case object North     extends Command
  case object South     extends Command
  case object East      extends Command
  case object West      extends Command
}

