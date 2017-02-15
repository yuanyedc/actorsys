package com.actorsys.actors

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

import akka.actor.{ActorSystem, Props}
import com.actorsys.service._
import com.typesafe.config.ConfigFactory

import scala.util.Random
import scala.util.parsing.json.JSONObject

/**
  * Created by yuanye on 2017/2/14.
  */
object AkkaClientApp extends App {
  val system = ActorSystem("client-system", ConfigFactory.load().getConfig("RemoteClientSideActor"))
  val log = system.log
  val clientActorRef = system.actorOf(Props[ClientActor], "clientActor")

  @volatile var running = true
  val heartbeatTime = 10000

  lazy val hbWorker = createHBWorker

  def createHBWorker: Thread = {
    new Thread("HB-Worker") {
      override def run(): Unit = {
        while (running) {
          clientActorRef ! Heartbeat("HB", 100000)
          Thread.sleep(heartbeatTime)
        }
      }
    }
  }

  clientActorRef ! Start //发送一个Start消息，第一次与远程Actor握手
  Thread.sleep(2000)
  clientActorRef ! Header("Header", 20, false)
  Thread.sleep(2000)
  hbWorker.start

  val random = Random
  val packetCount = 5
  val serviceProviders = Seq("CMCC", "AKBBC", "OLE")
  val payServiceProvicers = Seq("PayPal", "CMB", "ICBC", "ZMB", "XXB")
  val ID = new AtomicLong(10000)

  def nextID: Long = ID.incrementAndGet()

  def nextProvider(seq: Seq[String]): String = seq(random.nextInt(seq.size))

  def createPacket(packetMap: Map[String, _]): String = {
    val packet = new JSONObject(packetMap)
    packet.toString()
  }

  val startTime = System.currentTimeMillis()
  for (i <- 0 until packetCount) {
    val packet = createPacket(Map[String, Any](
      "txid" -> nextID,
      "pvid" -> nextProvider(serviceProviders),
      "txtm" -> LocalDateTime.now(),
      "payp" -> nextProvider(payServiceProvicers),
      "amt" -> random.nextFloat() * 100
    ))
    clientActorRef ! Packet("PKT", System.currentTimeMillis(), packet)
  }
  val endTime = System.currentTimeMillis()
  log.info("FINISH: timeTaken=" + (endTime - startTime) / 1000)
  Thread.sleep(2000)

  val waitSecs = heartbeatTime
  clientActorRef ! Shutdown(waitSecs)

  running = false
  while (hbWorker.isAlive) {
    log.info("Wait heartbeat worker to exit...")
    Thread.sleep(300)
  }
  system.deadLetters
}
