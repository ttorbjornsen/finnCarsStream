import java.util.Properties

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}



/**
  * Created by torbjorn on 23.04.16.
  */
object KafkaProducer extends App{

  val topic = "cars_header"
  val props = new Properties()
  props.setProperty("metadata.broker.list", "192.168.56.56:9092")
  props.setProperty("serializer.class", "kafka.serializer.StringEncoder")
  props.setProperty("request.required.acks", "1")
  val producer = new Producer[String, String](new ProducerConfig(props))

  val numberOfPages = 4 //get input from parameter arguments and automatically split evenly in two ranges
  val hdrPages1 = Range(1,3,1)
  val hdrPages2 = Range(3,5,1)

  // each Future will async try to extract page results from Kafka and

  val f1:Future[Unit] = Future{
    hdrPages1.map{page =>
      val url = "https://extraction.import.io/query/extractor/048ad769-f701-42a4-9ed3-5593b3ed0cdc?_apikey=6f684a69407b449fa8d1a8ea294f5a6bd9e7c426d14291abefb6c0dbc492734acac43b1be257f10739a580c935718ae96d03d3da27d0a4da95c2d6b411a65707583f18d38ae3ccdfd62cc74ff4791e27&url=http%3A%2F%2Fm.finn.no%2Fcar%2Fused%2Fsearch.html%3Fbody_type%3D4%26rows%3D100%26page%3D" + page
      Utility.saveFinnCarsPageResults(producer, topic, url)
      println("Page " + page + " written to Kafka")
      }
    }

  val f2:Future[Unit] = Future{
    hdrPages2.map{page =>
      val url = "https://extraction.import.io/query/extractor/048ad769-f701-42a4-9ed3-5593b3ed0cdc?_apikey=6f684a69407b449fa8d1a8ea294f5a6bd9e7c426d14291abefb6c0dbc492734acac43b1be257f10739a580c935718ae96d03d3da27d0a4da95c2d6b411a65707583f18d38ae3ccdfd62cc74ff4791e27&url=http%3A%2F%2Fm.finn.no%2Fcar%2Fused%2Fsearch.html%3Fbody_type%3D4%26rows%3D100%26page%3D" + page
      Utility.saveFinnCarsPageResults(producer, topic, url)
      println("Page " + page + " written to Kafka topic " + topic)
    }
  }

  //ensure that the main thread will not end before each Future has been completed. Timeout after 10 minutes.
  val fSer2: Future[Unit] = for {
    r1 <- f1
    r2 <- f2
  } yield(r1,r2)
  Await.result(fSer2, 10 minutes) //avoid main thread stopping before futures have completed

  // TODO: print message when some pages have not been loaded successfully (due to timeout)?
  fSer2.onComplete{
    case Success(_) => println("Finished trying to write pages " + Math.min(hdrPages1.min, hdrPages2.min) + " - " + Math.max(hdrPages1.max, hdrPages2.max) + " to Kafka")
    case Failure(_) => println("Timeout - not able to complete writing to Kafka within time limit.")}
}



//  hdrPages.foreach { page =>
//    Thread.sleep(2000)
//    println("Page + " + page + " - start extracting")
//    Future {Utility.getURL("https://extraction.import.io/query/extractor/048ad769-f701-42a4-9ed3-5593b3ed0cdc?_apikey=6f684a69407b449fa8d1a8ea294f5a6bd9e7c426d14291abefb6c0dbc492734acac43b1be257f10739a580c935718ae96d03d3da27d0a4da95c2d6b411a65707583f18d38ae3ccdfd62cc74ff4791e27&url=http%3A%2F%2Fm.finn.no%2Fcar%2Fused%2Fsearch.html%3Fbody_type%3D4%26rows%3D100%26page%3D" + page)(10)
//    }.onComplete { //not very elegant solution, since this will always return a Try-object(succeed), but the Try-object may be a Failure or Success
//      case Success(Success(value)) => { //handle success
//        println("Page " + page + " - loaded successfully")
//        producer.send(new KeyedMessage[String, String](topic, value))
//      }
//      case Success(Failure(e)) => {        //handle error
//        println("Page " + page + " failed to load due to error " + println(e))
//      }
//    }
//  }



