package sporophyte

import akka.actor.{Props, Actor}

/** Special Props that require a spore instead of a function0 */
object SProps {
  def apply(cons: Spore0[Actor]): Props =
    Props(creator = cons)
}

