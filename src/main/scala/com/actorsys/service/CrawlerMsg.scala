package com.actorsys.service

/**
  * Created by yuanye on 2017/2/14.
  */
trait CrawlerMsg {

}

case class WebUrl(link: String)

case class ScheduledWebUrl(link: String, config: Map[String, Any])

case class CrawledWeb(link: String, domain: String, encoding: String, contentLength: Int, outlinks: Set[String])

case class Stored(link: String, outlinkCount: Int)
