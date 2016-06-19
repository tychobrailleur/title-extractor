package org.bumblecat

import akka.actor.Actor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import rx.lang.scala.Observable
import scala.collection.JavaConverters._

class TitleExtractor extends Actor {
  
  
  def receive = {
    case html:String => {
      val doc:Document = Jsoup.parse(html)
      val elements:Elements = doc.select(".storyLink")
      
      sender() ! elements.asScala.toList.map { elt:Element =>  elt.text() + " [" + elt.attr("href") + "]" }
    }
  }
}