import java.net.URL
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import kafka.producer.ProducerConfig
import java.util.Properties
import kafka.producer.Producer
import kafka.producer.KeyedMessage
import java.util.Date
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
  * Created by torbjorn on 23.04.16.
  */
object Extract extends App{


  val topic = "finnCars"
  val props = new Properties()
  props.put("metadata.broker.list", "192.168.56.56:9092")
  props.put("serializer.class", "kafka.serializer.StringEncoder")
  props.put("request.required.acks", "1")

  val config = new ProducerConfig(props)
  val producer = new Producer[String, String](config)

  val pages = List(1,200)

  pages.map(page => Future {Source.fromURL(
    new URL("https://api.import.io/store/connector/b5dbd929-298b-458d-ba50-d4e52a7876f2/_query?input=webpage/url:http%3A%2F%2Fm.finn.no%2Fcar%2Fused%2Fsearch.html%3Fbody_type%3D4%26page%3D" + page + "%26rows%3D100&&_apikey=6f684a69407b449fa8d1a8ea294f5a6bd9e7c426d14291abefb6c0dbc492734acac43b1be257f10739a580c935718ae96d03d3da27d0a4da95c2d6b411a65707583f18d38ae3ccdfd62cc74ff4791e27")
  ).mkString}.onComplete {
    case Success(value) => producer.send(new KeyedMessage[String, String](topic, value))
    case Failure(e) => e.printStackTrace()
  }
  )
}