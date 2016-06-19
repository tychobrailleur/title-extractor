package org.bumblecat

import akka.actor.Actor
import com.ning.http.client.AsyncHttpClient
import scala.concurrent.Promise
import java.util.concurrent.Executor
import scala.concurrent.Future
import akka.event.Logging


case class BadStatus(status: Int) extends RuntimeException

object WebClient {
  
  def client = new AsyncHttpClient
  
  def get(url: String)(implicit exec: Executor):Future[String] = {
    val f = client.prepareGet(url).execute()
    val p = Promise[String]()
    
    f.addListener(new Runnable {
      def run = {
        val response = f.get
        if (response.getStatusCode < 400) {
          p.success(response.getResponseBody)
        } else {
          p.failure(BadStatus(response.getStatusCode))
        }
      }
    }, exec)
    
    p.future
  }
  
  def shutdown = client.close()
}