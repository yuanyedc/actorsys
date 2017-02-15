package com.actorsys.actors

import akka.actor.{ActorSystem, Props}
import com.actorsys.service.{Heartbeat, Packet, Start}

import scala.util.parsing.json.JSONObject

/**
  * Created by yuanye on 2017/2/13.
  */
object LocalClient extends App {
  val system = ActorSystem("local-system")
  val localActorRef = system.actorOf(Props[LocalActor], "local-actor")

  localActorRef ! Start
  localActorRef ! Heartbeat("000000", 0xabc)

  val contentMap = Map[String, Any]("name" -> "test", "pid" -> 888098)

  val content = new JSONObject(contentMap)
  localActorRef ! Packet("00001", System.currentTimeMillis(), content.toString())
}
