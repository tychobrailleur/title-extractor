package org.bumblecat

import akka.pattern.pipe
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executor
import akka.actor.Actor
import akka.actor.Status
import scala.collection.JavaConverters._
import akka.event.Logging


class Getter(url: String) extends Actor {
    
  val log = Logging(context.system, this)
  
  implicit val executor = context.dispatcher.asInstanceOf[Executor with ExecutionContext]
  def client = WebClient
  
  //client get url pipeTo self
  
  def receive = {
    case url:String => {
      
      val ender = sender()

      client.get(url).onComplete { body => 
        ender ! body    
      }

    }
    
    case _: Status.Failure => context.stop(self)
  }
}