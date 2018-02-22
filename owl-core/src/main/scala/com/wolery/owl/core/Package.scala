//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl

//****************************************************************************

package object core
{
  type Logging                      = com.wolery.util.Logging

  type Functor[F[_]]                = com.wolery.math.Functor[F]
  type Finite[α]                    = com.wolery.math.Finite[α]
  type FiniteSet[α]                 = com.wolery.math.FiniteSet[α]
  type Semigroup[S]                 = com.wolery.math.Semigroup[S]
  type Monoid[S]                    = com.wolery.math.Monoid[S]
  type Group[S]                     = com.wolery.math.Group[S]
  type Action[S,G]                  = com.wolery.math.Action[S,G]
  type Torsor[S,G]                  = com.wolery.math.Torsor[S,G]
  val  FiniteSet                    = com.wolery.math.FiniteSet

  type ℤSet[S]                      = Action[S,ℤ]
  type ℤTorsor[S]                   = Torsor[S,ℤ]

  type Midi                         = ℕ
  type Octave                       = ℤ
  type Notes                        = FiniteSet[Note]
  type Pitches                      = FiniteSet[Pitch]

  def Hz (r: ℝ): Frequency          = Frequency(r)
  def kHz(r: ℝ): Frequency          = Frequency(r * 1e3)

  val C: Note                       = Note(Pitch(0))
  val D: Note                       = Note(Pitch(2))
  val E: Note                       = Note(Pitch(4))
  val F: Note                       = Note(Pitch(5))
  val G: Note                       = Note(Pitch(7))
  val A: Note                       = Note(Pitch(9))
  val B: Note                       = Note(Pitch(11))

//****************************************************************************

  implicit final
  class SemigroupSyntax[S](s: S)(implicit ε: Semigroup[S])
  {
    def combine (t: S): S           = ε.combine(s,t)
    def ⋅       (t: S): S           = ε.combine(s,t)
  }

  implicit final
  class GroupSyntax[G](f: G)(implicit ε: Group[G])
  {
    def inverse : G                 = ε.inverse(f)
    def unary_- : G                 = ε.inverse(f)
  }

  implicit final
  class ActionSyntax[S,G](s: S)(implicit ε: Action[S,G])
  {
    def apply(g: G): S              = ε(s,g)
    def +    (g: G): S              = ε(s,g)
    def -    (g: G): S              = ε(s,ε.group.inverse(g))
  }

  implicit final
  class TorsorSyntax[S,G](s: S)(implicit ε: Torsor[S,G])
  {
    def delta (t: S): G             = ε.delta(t,s)
    def -     (t: S): G             = ε.delta(t,s)
  }

  implicit final
  class ℤSetSyntax[S](s: S)(implicit ε: ℤSet[S])
  {
    def ♭ : S                       = ε(s,-1)
    def ♮ : S                       = s
    def ♯ : S                       = ε(s,+1)
  }

  implicit final
  class FiniteSyntax[α](a: α)(implicit ε: Finite[α])
  {
    def toℕ: ℕ                      = ε.toℕ(a)
  }

  implicit final
  class FiniteSyntax2[α](i: ℕ)(implicit ε: Finite[α])
  {
    def fromℕ: α                    = ε.fromℕ(i)
  }

//instances:

  implicit
  lazy val `Group[ℤ]` = new Group[ℤ]
  {
    val e                 : ℤ       = 0
    def inverse(i: ℤ)     : ℤ       = -i
    def combine(i: ℤ,j: ℤ): ℤ       = i + j
  }

  implicit
  lazy val `Group[ℝ]` = new Group[ℝ]
  {
    val e                 : ℝ       = 0
    def inverse(i: ℝ)     : ℝ       = -i
    def combine(i: ℝ,j: ℝ): ℝ       = i + j
  }

  implicit
  lazy val `Functor[Set]` = new Functor[Set]
  {
    override
    def map[α,β](set: Set[α])(f: α ⇒ β): Set[β] = set.map(f)
  }

  implicit
  def `PartialOrdering[Set[α]]`[α,S[α] <: Set[α]]: PartialOrdering[S[α]] =
  {
    object instance extends PartialOrdering[Set[Any]]
    {
      def lteq(s: Set[Any],t: Set[Any]): Bool = s ⊆ t

      def tryCompare(s: Set[Any],t: Set[Any]) = (s ⊆ t,s ⊇ t) match
      {
        case (true, true)  ⇒ Some( 0)
        case (true, false) ⇒ Some(-1)
        case (false,true)  ⇒ Some(+1)
        case (false,false) ⇒ None
      }
    }

    instance.asInstanceOf[PartialOrdering[S[α]]]
  }
}

//****************************************************************************
