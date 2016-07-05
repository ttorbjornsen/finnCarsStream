import java.net.URL

import scala.io.Source
import scala.util.Try

/**
  * Created by torbjorn.torbjornsen on 05.07.2016.
  */
object Utility  {

  def getURL(url: String)(retry: Int): Try[String] = {
    Try(Source.fromURL(new URL(url)).mkString)
      .recoverWith {
        case _ if(retry > 0) => {
          Thread.sleep(10000)
          println("Retry url " + url + " - " + retry + " retries left")
          getURL(url)(retry - 1)
        }
      }
  }

}
