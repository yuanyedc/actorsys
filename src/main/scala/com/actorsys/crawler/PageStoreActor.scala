package com.actorsys.crawler

import java.util.concurrent.ForkJoinPool

import akka.actor.{Actor, ActorLogging, Props}
import com.actorsys.service.{CrawledWeb, Stored}
import com.dao.OracleUtil

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by yuanye on 2017/2/14.
  */
class PageStoreActor extends Actor with ActorLogging {
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool())
  val crawlerActorRef = context.actorOf(Props[CrawlerActor], "crawlerActor")

  override def receive: Receive = {
    case CrawledWeb(link, domain, encoding, contentLength, outlinks) => {
      val future = Future {
        try {
          log.info("插入数据:link=" + link)
          var sqls = Set[String]()
          val sql = "INSERT INTO BG_SPAY_AUTH_WHITELIST(ID,AUTH_TYPE,ACCOUNT_NO,ACCOUNT_NAME,ACCOUNT_TYPE,CERT_NO) " +
            " VALUES('12333','SECCENTER','1111111','qw','01','12334')"
          sqls += sql
          //OracleUtil.doTrancation(sqls)
          (link, outlinks.size)
        } catch {
          case e: Throwable => throw e
        }
      }
      future onComplete {
        case Success((link, outlinkCount)) => {
          log.info("SUCCESS: link=" + link + ", outlinkCount=" + outlinkCount)
          crawlerActorRef ! Stored(link, outlinkCount)
        }
        case Failure(e: Throwable) => log.error("FAILURE:" + e)

      }
    }
  }
}
