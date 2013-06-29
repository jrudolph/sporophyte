package sporophyte

import akka.actor.{Props, ActorSystem, Actor}
import scala.concurrent.duration._
import Spore._
import java.util.concurrent.ThreadLocalRandom

object Main extends App {
  val system = ActorSystem()
  (new Boot).boot(system)
  Helper.scheduleShutdown(system)
}

/** An ephemeral class used just for booting */
class Boot extends Configuration with Encoder with Util {
  val bootOnlyBuffer = new Array[Byte](100 * 1024 * 1024)

  def boot(system: ActorSystem): Unit = {
    //system.actorOf(Props(new LoggingActor(logger)))
    system.actorOf(SProps(() => new LoggingActor(capture(logger))))

    def errLogger(str: String) = Console.err.println(str)

    //system.actorOf(Props(new LoggingActor(errLogger)))
    system.actorOf(SProps(() => new LoggingActor(capture(errLogger _))))

    val initialValue = 42
    //system.actorOf(Props(new OneShotActor(initialValue, Helper.nextRandom())))

    // I tried the code below first but imagined spores checking complained
    // that `OneShotActor` is captured from the environment.
    // system.actorOf(SProps(() => new OneShotActor(capture(initialValue), Helper.nextRandom())))
    //
    // Fortunately, I was able to fix it:
    system.actorOf(SProps(() => capture(new OneShotActor(initialValue, _: Int))(Helper.nextRandom())))

    //system.actorOf(Props(new ElementInspactor(Text("test"))))
    system.actorOf(SProps(() => new ElementInspactor(capture(this.Text("test")))))

    // Boot should go out of scope here, and be eligible for GC
  }
}
object Helper {
  def scheduleShutdown(system: ActorSystem): Unit = {
    import system.dispatcher
    println("Shutting down in 5 seconds")
    system.scheduler.scheduleOnce(5.seconds)(system.shutdown())
  }

  def nextRandom() =
    ThreadLocalRandom.current().nextInt(100000)
}

class LoggingActor(sink: String => Unit) extends Actor {
  def receive = {
    case s: String => sink(s)
  }
}
