package sporophyte

import akka.actor.Actor

trait Element
class ElementInspactor(initialElement: Element) extends Actor {
  def receive = {
    case _ =>
  }
}
