import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.jsoup.nodes.Document
import org.jsoup.nodes.Document.OutputSettings
import org.jsoup.nodes.{Element, Entities}
import javax.script.{Invocable, ScriptEngine, ScriptEngineManager}
import play.api.libs.json._

import scala.collection.JavaConversions._
/**
  * Created by torbjorn.torbjornsen on 05.07.2016.
  */
object ExtractDetails {



  val doc: Document = Jsoup.connect("http://m.finn.no/car/used/ad.html?finnkode=78601940").get
  val carPropElements:Element = doc.select(".mvn+ .col-count2from990").first()



  var i = 0
  for (elem:Element <- carPropElements.children()){
    if ((i % 2) == 0) {
      val key = elem.text
      val value = elem.nextElementSibling().text
      json.put(key, value)
    }
    else println("value : " + elem.text)
    i = i + 1
  }

  val carEquipElements:Element = doc.select(".col-count2upto990").first()
  for (elem:Element <- carEquipElements.children()){
    println(elem.text)
  }

  val carInfoElements:Element = doc.select(".object-description p[data-automation-id]").first()
  val info = carInfoElements.text





  //if js-engine needed to run js code : web-scraping-nashorn-scala
//  val manager: ScriptEngineManager = new ScriptEngineManager
//  val engine: ScriptEngine = manager.getEngineByName("nashorn")
//  val in: Invocable = engine.asInstanceOf[Invocable]
//
  //  engine.eval("function extractCarProperties(doc){ print(doc.select('.mvn+ .col-count2from990').first().html()); }")
//  in.invokeFunction("extractCarProperties", doc)

}
