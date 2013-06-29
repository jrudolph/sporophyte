package sporophyte

trait Spore0[+R] extends Function0[R]
trait Spore1[-T, +R] extends Function1[T, R]

/**
 * Experimental spore implementation, which allows to experiment with the syntax
 * but doesn't do static checking.
 */
object Spore {
  def spore[R](f: () => R): Spore0[R] = new Spore0[R] {
    def apply(): R = f()
  }
  def spore[T, R](f: T => R): Spore1[T, R] = new Spore1[T, R] {
    def apply(v1: T): R = f(v1)
  }
  def capture[T](v: T): T = v

  implicit def autoSpore[R](f: () => R): Spore0[R] = spore(f)
}
