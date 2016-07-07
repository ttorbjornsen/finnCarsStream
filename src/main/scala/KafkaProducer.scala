import java.util.Properties
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}



/**
  * Created by torbjorn on 23.04.16.
  */
object KafkaProducer extends App{

  val topic = "cars_header"
  val props = new Properties()
  props.put("metadata.broker.list", "192.168.56.56:9092")
  props.put("serializer.class", "kafka.serializer.StringEncoder")
  props.put("request.required.acks", "1")

  val config = new ProducerConfig(props)
  val producer = new Producer[String, String](config)

  val hdrPages = Range(1,2,1) //should be about 200 when in production


  hdrPages.foreach { page =>
    Thread.sleep(2000)
    println("Page + " + page + " - start extracting")
    Future {Utility.getURL("https://extraction.import.io/query/extractor/048ad769-f701-42a4-9ed3-5593b3ed0cdc?_apikey=6f684a69407b449fa8d1a8ea294f5a6bd9e7c426d14291abefb6c0dbc492734acac43b1be257f10739a580c935718ae96d03d3da27d0a4da95c2d6b411a65707583f18d38ae3ccdfd62cc74ff4791e27&url=http%3A%2F%2Fm.finn.no%2Fcar%2Fused%2Fsearch.html%3Fbody_type%3D4%26rows%3D100%26page%3D" + page)(10)
    }.onComplete { //not very elegant solution, since this will always return a Try-object(succeed), but the Try-object may be a Failure or Success
      case Success(Success(value)) => { //handle success
        println("Page " + page + " - loaded successfully")
        producer.send(new KeyedMessage[String, String](topic, value))
      }
      case Success(Failure(e)) => {        //handle error
        println("Page " + page + " failed to load due to error " + println(e))
      }
    }
  }



}