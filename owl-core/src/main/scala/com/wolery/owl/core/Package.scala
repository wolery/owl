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

//****************************************************************************

package object core
{
  type Bits                         = Int
  type Midi                         = Int
  type Octave                       = Int

  def Hz (r: ℝ): Frequency          = Frequency(r)
  def kHz(r: ℝ): Frequency          = Frequency(r * 1e3)

  type Pitches                      = FiniteSet[Pitch]

  val C: Note                       = Note(Pitch(0))
  val D: Note                       = Note(Pitch(2))
  val E: Note                       = Note(Pitch(4))
  val F: Note                       = Note(Pitch(5))
  val G: Note                       = Note(Pitch(7))
  val A: Note                       = Note(Pitch(9))
  val B: Note                       = Note(Pitch(11))

//****************************************************************************

  implicit final class MonoidSyntax[M](m: M)(implicit α: Monoid[M])
  {
    def ⋅ (n: M): M                 = α.operate(m,n)
  }

  implicit final class GroupSyntax[G](f: G)(implicit α: Group[G])
  {
    def unary_- : G                 = α.inverse(f)
  }

  implicit final class ActionSyntax[S,G](s: S)(implicit α: Action[S,G])
  {
    def +    (g: G): S              = α(s,g)
    def -    (g: G): S              = α(s,α.group.inverse(g))
    def apply(g: G): S              = α(s,g)
  }

  implicit final class TorsorSyntax[S,G](s: S)(implicit α: Torsor[S,G])
  {
    def - (t: S): G                 = α.delta(t,s)
  }

  implicit final class ℤActionSyntax[S](s: S)(implicit α: Action[S,ℤ])
  {
    def ♭ : S                       = α(s,-1)
    def ♮ : S                       = s
    def ♯ : S                       = α(s,+1)
  }

  implicit object isℤGroup extends Group[ℤ]
  {
    val e                 : ℤ       = 0
    def inverse(i: ℤ)     : ℤ       = -i
    def operate(i: ℤ,j: ℤ): ℤ       = i + j
  }

  implicit object isℝGroup extends Group[ℝ]
  {
    val e                 : ℝ       = 0
    def inverse(i: ℝ)     : ℝ       = -i
    def operate(i: ℝ,j: ℝ): ℝ       = i + j
  }
}

//****************************************************************************
