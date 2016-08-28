package com.actorTest

import akka.actor.Actor
import akka.event.Logging

/**
  * Created by yuanye on 2016/8/5.
  */
class MyActor extends Actor {
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case "test" => log.info("received test")
    case _ => log.info("received unknown message")
  }
}
