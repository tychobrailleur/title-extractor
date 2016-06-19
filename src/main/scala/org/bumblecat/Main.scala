package org.bumblecat

import akka.actor._
import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success


object GateKeeper {
  trait Operation
  case object Hello extends Operation
}

class GateKeeper extends Actor {
  def receive = {
    case msg:String => {
      val getter = context.actorOf(Props(new Getter(msg)), "getter")
      
      getter ! msg
      context.become(parse)
    }
  }
  
  def parse:Receive = {
    case Success(content) => {
      val extractor = context.actorOf(Props(new TitleExtractor()), "extractor")
         
      extractor ! content
      context.become(listTitles)
    }
  }
  
  def listTitles:Receive = {
    case titles:List[String @unchecked] => {
      println(titles.mkString("\n"))

      context.system.shutdown()
    }
  }
}
  
object Main extends App {
  val system = ActorSystem("TitleExtractorSystem")
  val gateKeeper = system.actorOf(Props[GateKeeper], "awesome")
  
  gateKeeper ! "https://news.ycombinator.com/news"
}