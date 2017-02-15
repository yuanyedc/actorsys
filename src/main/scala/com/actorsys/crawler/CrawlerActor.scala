package com.actorsys.crawler

import java.io.ByteArrayOutputStream
import java.net.{HttpURLConnection, URL}
import java.util.concurrent.{ForkJoinPool, LinkedBlockingDeque}

import akka.actor.{Actor, ActorLogging, Props}
import com.actorsys.service.{CrawledWeb, ScheduledWebUrl, Stored, WebUrl}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by yuanye on 2017/2/14.
  */
class CrawlerActor extends Actor with ActorLogging {
  private val scheduleActorRef = context.actorOf(Props[ScheduleActor], "scheduleActor")
  private val pageStoreActorRef = context.actorOf(Props[PageStoreActor], "pageStoreActor")
  private val queue = new LinkedBlockingDeque[String]()
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool())

  override def receive: Receive = {
    case link: String => {
      if (link != null && (link.startsWith("http://") || link.startsWith("https://"))) {
        log.info("Checked:" + link)
        scheduleActorRef ! WebUrl(link)
      }
    }
    case ScheduledWebUrl(link, _) => {
      var crawledWeb: CrawledWeb = null
      val crawlFuture = Future {
        try {
          var encoding = "UTF-8"
          var outlinks = Set[String]()
          val url = new URL(link)
          val domain = url.getHost
          val urlConn = url.openConnection().asInstanceOf[HttpURLConnection]
          urlConn.setConnectTimeout(5000)
          urlConn.connect()
          if (urlConn.getResponseCode == 200) {
            if (urlConn.getContentEncoding != null) {
              encoding = urlConn.getContentEncoding
            }
            if (urlConn.getContentLength > 0) {
              val ins = urlConn.getInputStream
              val buffer = Array.fill[Byte](512)(0)
              val baos = new ByteArrayOutputStream
              var bytesRead = ins.read(buffer)
              while (bytesRead > -1) {
                baos.write(buffer, 0, bytesRead)
                bytesRead = ins.read(buffer)
              }
              outlinks = extractOutlinks(link, baos.toString(encoding))
              baos.close()
            }
            log.info("Page: link=" + link + ", encoding=" + encoding + ", outlinks=" + outlinks)
            CrawledWeb(link, domain, encoding, urlConn.getContentLength, outlinks)
          }
        } catch {
          case e: Throwable => e
        }
      }
      crawlFuture onComplete {
        case Success(crawledWeb: CrawledWeb) => {
          log.info("Succeed to crawl: link=" + link + ", crawledWeb=" + crawledWeb)
          if (crawledWeb != null) {
            pageStoreActorRef ! crawledWeb
            log.info("Sent crawled data to store actor.")
            queue add link
          }
        }
        case Failure(exception: Throwable) => log.error("Fail to crawl: " + exception.toString)
      }
    }
    case Stored(link, count) => {
      queue.remove(link)
      scheduleActorRef ! (link, count)
    }
  }

  def extractOutlinks(parentUrl: String, content: String): Set[String] = {
    val outLinks = "href\\s*=\\s*\"([^\"]+)\"".r.findAllMatchIn(content).map {
      m => {
        var url = m.group(1)
        if (!url.startsWith("http") || !url.startsWith("https")) {
          url = new URL(new URL(parentUrl), url).toExternalForm
        }
        url
      }
    }.toSet
    outLinks.filter(url => !url.isEmpty && (url.endsWith("html") || url.startsWith("htm")))
  }
}
