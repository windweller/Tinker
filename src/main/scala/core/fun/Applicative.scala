package core.fun

import simulacrum._

import scala.language.implicitConversions
import scala.language.higherKinds

/**
 * Created by Aimingnie on 9/18/15
 */
@typeclass trait Applicative[F[_]] extends Functor[F] {

  def pure[A](a: A): F[A]

  def apply[A, B](fa: F[A])(ff:F[A => B]): F[B]

  override def map[A, B](fa: F[A])(f: A => B): F[B] = apply(fa)(pure(f))

  def map2[A, B, Z](fa: F[A], fb: F[B])(f: (A, B) => Z): F[Z] =
    apply(fa)(map(fb)(b => f(_, b)))
}

object Applicative {
  implicit val optionApplicative: Applicative[Option] = new Applicative[Option] {

    override def pure[A](a: A): Option[A] = Some(a)

    override def apply[A, B](fa: Option[A])(ff: Option[(A) => B]): Option[B] = (fa, ff) match {
      case (None, _) => None
      case (Some(a), None) => None
      case (Some(a), Some(f)) => Some(f(a))
    }


  }
}
