package sporophyte

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Try

/** Special future API that could be changed to require spores for experimental purposes */
trait SFuture[+T] {
  def onComplete[U](func : scala.util.Try[T] => U)(implicit executor: ExecutionContext): Unit
  def map[U](f: T => U)(implicit executor: ExecutionContext): SFuture[U]
  def flatMap[U](f: T => SFuture[U])(implicit executor: ExecutionContext): SFuture[U]
  def foreach[U](f: T => U)(implicit executor: ExecutionContext): Unit

  def underlying: Future[T]
}
object SFuture {
  def apply[T](body: => T)(implicit executor: ExecutionContext): SFuture[T] = wrap(Future(body))

  def wrap[T](real: Future[T]): SFuture[T] =
    new SFuture[T] {
      def onComplete[U](func: (Try[T]) => U)(implicit executor: ExecutionContext): Unit =
        real.onComplete(func)

      def foreach[U](f: (T) => U)(implicit executor: ExecutionContext): Unit =
        real.foreach(f)

      def map[U](f: T => U)(implicit executor: ExecutionContext): SFuture[U] =
        wrap(real.map(f))

      def flatMap[U](f: T => SFuture[U])(implicit executor: ExecutionContext): SFuture[U] =
        wrap {
          real.flatMap { t =>
            f(t).underlying
          }
        }

      def underlying: Future[T] = real
    }

  def sequence[T](seq: Seq[SFuture[T]])(implicit executor: ExecutionContext): SFuture[Seq[T]] =
    wrap(Future.sequence(seq.map(_.underlying)))

}
