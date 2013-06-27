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
    system.actorOf(SProps {
      spore {
        val l = this.logger
        () => new LoggingActor(l)
      }
    })

    def errLogger(str: String) = Console.err.println(str)

    //system.actorOf(Props(new LoggingActor(errLogger)))
    system.actorOf(SProps {
      spore {
        val e = errLogger _
        () => new LoggingActor(e)
      }
    })

    val initialValue = 42
    //system.actorOf(Props(new OneShotActor(initialValue, Helper.nextRandom())))
    system.actorOf(SProps {
      spore {
        // I tried the code below first but imagined spores checking complained
        // that `OneShotActor` is captured from the environment.
        // val i = initialValue
        // () => new OneShotActor(i, Helper.nextRandom())
        //
        // Fortunately, I was able to fix it:

        val actorCons = new this.OneShotActor(initialValue, _: Int)
        () => actorCons(Helper.nextRandom())
      }
    })

    //system.actorOf(Props(new ElementInspactor(Text("test"))))
    system.actorOf(SProps {
      spore {
        val textElement = this.Text("test")
        () => new ElementInspactor(textElement)
      }
    })

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
