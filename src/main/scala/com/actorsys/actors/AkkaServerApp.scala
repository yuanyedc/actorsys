package com.actorsys.actors

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by yuanye on 2017/2/14.
  */
object AkkaServerApp extends App {
  val system = ActorSystem("remote-system", ConfigFactory.load().getConfig("RemoteServerSideActor"))
  val log = system.log
  log.info("Remote server actor started:" + system)
  system.actorOf(Props[RemoteActor], "remoteActor")
}
