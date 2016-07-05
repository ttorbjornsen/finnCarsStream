import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.script.{Invocable, ScriptEngine, ScriptEngineManager}
/**
  * Created by torbjorn.torbjornsen on 05.07.2016.
  */
object ExtractDetails {
  //http://henning.kropponline.de/2014/05/25/web-scraping-nashorn-scala/

  val manager: ScriptEngineManager = new ScriptEngineManager
  val engine: ScriptEngine = manager.getEngineByName("nashorn")
  engine.eval("function hello(){print('Hello');} hello();")
  val doc: Document = Jsoup.connect("http://m.finn.no/car/used/ad.html?finnkode=78601940").get
  engine.eval("function extractTitle(doc){ print(doc.select('head title').first().html()); }")
  val in: Invocable = engine.asInstanceOf[Invocable]
  in.invokeFunction("extractTitle", doc);

  engine.eval("function extractDetails(doc){ print(doc.select('body div.container.bg-blue-greyish.pbs div div.line div.unit.r-size2of3 div:nth-child(3) div div dl:nth-child(12)').first().html()); }")
  engine.eval("function extractDetails(doc){ print(doc.select('div:nth-child(3)').first().html()); }")
  in.invokeFunction("extractDetails", doc);

  head > title
  body > div.container.bg-blue-greyish.pbs > div > div.line > div.unit.r-size2of3 > div:nth-child(3) > div > div > dl:nth-child(12)
  /html/body/div[3]/div/div[2]/div[1]/div[3]/div/div/dl[2]
}
