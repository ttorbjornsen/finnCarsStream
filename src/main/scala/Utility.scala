import java.net.URL

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import scala.util.Try
import scala.util.{Failure, Success}
import scala.concurrent.duration._


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

  def saveFinnCarsPageResults(producer:Producer[String,String], topic:String, url: String):Unit = {
    val f = Future{Source.fromURL(new URL(url)).mkString}
    val action = f.map {results =>
      producer.send(new KeyedMessage[String, String](topic, results))
    } recover{
      case t: Throwable => {
        t.printStackTrace()
        saveFinnCarsPageResults(producer, topic, url)
      }
    }

    Await.result(action, 2 minutes)
  }


//  def promiseSaveFinnCarsPageResults(kafkaProducer:Producer[String,String], kafkaTopic:String, url: String):Unit = {
//    val p = Promise[String]
//    val f = p.future //get future value (when onSuccess)
//
//    val producer = Future {
//      val result = Source.fromURL(new URL(url)).mkString
//      p success result //on success of future, produce result
//    }
//
//    val consumer = Future {
//      f onSuccess {
//        case result => {
////          kafkaProducer.send(new KeyedMessage[String, String](kafkaTopic, result))
//          "String"
//        }
//      }
//    }
//
//  }

}





