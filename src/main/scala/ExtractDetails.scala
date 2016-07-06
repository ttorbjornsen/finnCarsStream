import java.util.Properties

import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.jsoup.nodes.Document
import org.jsoup.nodes.Document.OutputSettings
import org.jsoup.nodes.{Element, Entities}
import javax.script.{Invocable, ScriptEngine, ScriptEngineManager}

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import play.api.libs.json
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer



/**
  * Created by torbjorn.torbjornsen on 05.07.2016.
  */
object ExtractDetails {

  val topic = "cars_detail"
  val props = new Properties()
  props.put("metadata.broker.list", "192.168.56.56:9092")
  props.put("serializer.class", "kafka.serializer.StringEncoder")
  props.put("request.required.acks", "1")

  val config = new ProducerConfig(props)
  val producer = new Producer[String, String](config)


  val url = "http://m.finn.no/car/used/ad.html?finnkode=78601940"
  val doc: Document = Jsoup.connect(url).get
  val carPropElements:Element = doc.select(".mvn+ .col-count2from990").first()


  var i = 0
  var carPropListBuffer: ListBuffer[Map[String, String]] = ListBuffer()
  for (elem:Element <- carPropElements.children()){
    if ((i % 2) == 0) {
      val key = elem.text
      val value = elem.nextElementSibling().text
      carPropListBuffer += Map(key.asInstanceOf[String] -> value.asInstanceOf[String])
    }
    i = i + 1
  }

  val carEquipElements:Element = doc.select(".col-count2upto990").first()
  var carEquipListBuffer: ListBuffer[String] = ListBuffer()
  for (elem:Element <- carEquipElements.children()){
    carEquipListBuffer += elem.text
  }

  val carInfoElements:Element = doc.select(".object-description p[data-automation-id]").first()

  val jsObj = Json.obj("url" -> url, "properties" -> carPropListBuffer.toList, "information" -> carInfoElements.text, "equipment" -> carEquipListBuffer.toList)
  producer.send(new KeyedMessage[String, String](topic, jsObj.toString))




  //if js-engine needed to run js code : web-scraping-nashorn-scala
//  val manager: ScriptEngineManager = new ScriptEngineManager
//  val engine: ScriptEngine = manager.getEngineByName("nashorn")
//  val in: Invocable = engine.asInstanceOf[Invocable]
//
  //  engine.eval("function extractCarProperties(doc){ print(doc.select('.mvn+ .col-count2from990').first().html()); }")
//  in.invokeFunction("extractCarProperties", doc)

}
