import java.net.URL
import java.util.Properties

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.io.Source
import scala.util.{Failure, Success}
import scala.concurrent.duration._
/**
  * Created by torbjorn.torbjornsen on 06.07.2016.
  */
class Tests extends FunSpec with Matchers {

  describe("Application") {
    it("can get cars header from a page of Finn") {
      val kafkaTopic = "test_cars_header" //SEPARATE TOPIC FOR TESTING
      val kafkaProps = new Properties()
      kafkaProps.setProperty("metadata.broker.list", "192.168.56.56:9092")
      kafkaProps.setProperty("serializer.class", "kafka.serializer.StringEncoder")
      kafkaProps.setProperty("request.required.acks", "1")
      val kafkaProducer = new Producer[String, String](new ProducerConfig(kafkaProps))
      println(kafka.tools.ConsumerOffsetChecker.toString)
      val pages = Seq(1)

      val f1: Future[Unit] = Future {
        pages.map { page =>
          val url = "https://extraction.import.io/query/extractor/048ad769-f701-42a4-9ed3-5593b3ed0cdc?_apikey=6f684a69407b449fa8d1a8ea294f5a6bd9e7c426d14291abefb6c0dbc492734acac43b1be257f10739a580c935718ae96d03d3da27d0a4da95c2d6b411a65707583f18d38ae3ccdfd62cc74ff4791e27&url=http%3A%2F%2Fm.finn.no%2Fcar%2Fused%2Fsearch.html%3Fbody_type%3D4%26rows%3D100%26page%3D" + page
          Utility.saveFinnCarsPageResults(kafkaProducer, kafkaTopic, url)
          println("Page " + page + " written to Kafka. Please check contents manually")
        }
      }

      Await.result(f1, 1 minutes)

    }

    ignore("can handle when the page number is outside of the range - only paid ads should be returned") {
      val page = 100000 //test this
      val retries = 10
      val url = "https://extraction.import.io/query/extractor/048ad769-f701-42a4-9ed3-5593b3ed0cdc?_apikey=6f684a69407b449fa8d1a8ea294f5a6bd9e7c426d14291abefb6c0dbc492734acac43b1be257f10739a580c935718ae96d03d3da27d0a4da95c2d6b411a65707583f18d38ae3ccdfd62cc74ff4791e27&url=http%3A%2F%2Fm.finn.no%2Fcar%2Fused%2Fsearch.html%3Fbody_type%3D4%26rows%3D100%26page%3D" + page
      val tryObject = Utility.getURL(url)(retries)
      //tryObject shouldBe an[scala.util.Success[String]]
    }

  }
}
