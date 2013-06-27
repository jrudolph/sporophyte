package sporophyte

import akka.actor.Actor

trait Util { self: Configuration =>
  class OneShotActor(init: Int, randomSeed: Int) extends Actor {
    def receive = {
      case x: Int => (init + x) * constantFactor
    }
  }
}
