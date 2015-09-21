package core.fun

import simulacrum._

import scala.language.higherKinds
import scala.language.implicitConversions

/**
 * Created by Aimingnie on 9/18/15.
 */
@typeclass trait Functor[F[_]] {

  def map[A, B](fa: F[A])(f: A => B): F[B]

  def lift[A, B](f: A => B): F[A] => F[B] = fa => map(fa)(f)

  def as[A, B](fa: F[A], b: => B): F[B] = map(fa)(_ => b)

  def void[A](fa: F[A]): F[Unit] = as(fa, ())

}

object Functor {
  implicit val optionFunctor: Functor[Option] = new Functor[Option] {
    override def map[A, B](fa: Option[A])(f: (A) => B): Option[B] = fa.map(f)
  }
}