package com.actorsys.crawler

import akka.actor.{ActorSystem, Props}

/**
  * Created by yuanye on 2017/2/14.
  */
object AkkaCrawlerApp extends App {
  val system = ActorSystem("crawler-system")
  val log = system.log

  val scheduleActorRef = system.actorOf(Props[ScheduleActor], "scheduleActor")
  val crawlerActorRef = system.actorOf(Props[CrawlerActor], "crawlerActor")
  val pageStoreActorRef = system.actorOf(Props[PageStoreActor], "pageStoreActor")

  val seeds: Seq[String] = Seq[String]("https://www.baidu.com/")
  ScheduleActor.sendFeeds(crawlerActorRef, seeds)
}
