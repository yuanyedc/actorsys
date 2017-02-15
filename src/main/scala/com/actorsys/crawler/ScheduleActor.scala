package com.actorsys.crawler


import java.util.concurrent.ConcurrentHashMap

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.actorsys.service.{ScheduledWebUrl, WebUrl}

/**
  * Created by yuanye on 2017/2/14.
  */
class ScheduleActor extends Actor with ActorLogging {
  val config = Map[String, Any](
    "domain.black.list" -> Seq("google.com", "facebook.com", "twitter.com"),
    "crawl.retry.times" -> 3,
    "filter.page.url.suffixes" -> Seq(".zip", ".avi", ".mkv", ",mp4")
  )
  val counter = new ConcurrentHashMap[String, Int]()

  override def receive: Receive = {
    case WebUrl(url) => sender ! ScheduledWebUrl(url, config)
    case (link: String, count: Int) => {
      counter.put(link, count)
      log.info("counter:" + counter.toString)
    }
  }
}

object ScheduleActor {
  def sendFeeds(crawlerActorRef: ActorRef, seeds: Seq[String]) = {
    seeds.foreach(crawlerActorRef ! _)
  }
}
