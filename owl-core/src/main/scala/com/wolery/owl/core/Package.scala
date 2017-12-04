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

package com.wolery.owl

import scala.collection.generic.{CanBuildFrom => CBF}

//****************************************************************************

package object core
{
  type Midi                         = ℕ
  type Octave                       = ℤ
  type Notes                        = FiniteSet[Note]
  type Pitches                      = FiniteSet[Pitch]
  type ℤSet[S]                      = Action[S,ℤ]
  type ℤTorsor[S]                   = Torsor[S,ℤ]

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
    def operate (t: S): S           = ε.operate(s,t)
    def ⋅       (t: S): S           = ε.operate(s,t)
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

  implicit
  lazy val `Group[ℤ]` = new Group[ℤ]
  {
    val e                 : ℤ       = 0
    def inverse(i: ℤ)     : ℤ       = -i
    def operate(i: ℤ,j: ℤ): ℤ       = i + j
  }

  implicit
  lazy val `Group[ℝ]` = new Group[ℝ]
  {
    val e                 : ℝ       = 0
    def inverse(i: ℝ)     : ℝ       = -i
    def operate(i: ℝ,j: ℝ): ℝ       = i + j
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

  implicit
  lazy val `Functor[Set]` = new cats.Functor[Set]
  {
    def map[α,β](set: Set[α])(f: α ⇒ β): Set[β] = set.map(f)
  }
}

//****************************************************************************
