package com.actorsys.actors

import akka.actor.{Actor, ActorLogging}
import com.actorsys.service._

/**
  * Created by yuanye on 2017/2/14.
  */
class ClientActor extends Actor with ActorLogging {
  val path = "akka.tcp://remote-system@127.0.0.1:2552/user/remoteActor"
  val remoteActorRef = context.actorSelection(path)

  @volatile var connected = false
  @volatile var stop = false

  override def receive: Receive = {
    case Start => {
      send(Start)
      if (!connected) {
        connected = true
        log.info("actor connected:" + this)
      }
    }
    case Stop => {
      send(Stop)
      stop = true
      connected = false
    }
    case heartbeat: Heartbeat => sendWithCheck(heartbeat)
    case header: Header => send(header)
    case packet: Packet => sendWithCheck(packet)
    case shutdown: Shutdown => send(shutdown)
    case (seq, result) => log.info("RESULT: seq=" + seq + ", result=" + result)
    case _ => log.info("ERROR")
  }

  private def sendWithCheck(cmd: Serializable): Unit = {
    while (!connected) {
      Thread.sleep(100)
      log.info("Wait to be connected...")
      send(Start)
    }
    if (!stop) {
      send(cmd)
    } else {
      log.warning("Actor has stopped!")
    }
  }

  private def send(cmd: Serializable): Unit = {
    log.info("send cmd to server:" + cmd)
    try {
      remoteActorRef ! cmd
    } catch {
      case e: Exception => {}
        connected = false
        log.info("Try to connect by sending Start command...")
        send(Start)
    }
  }
}
