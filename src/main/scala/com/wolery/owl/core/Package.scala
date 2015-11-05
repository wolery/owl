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
//*
//*
//****************************************************************************

package com.wolery.owl

//****************************************************************************

package object core
{
  type ℕ                              = Int
  type ℤ                              = Int
  type ℝ                              = Double
  type Bool                           = Boolean
  type Name                           = String
  type Midi                           = Int
  type Octave                         = Int
  type Maybe[α]                       = Option[α]

  def Hz (r: ℝ): Frequency            = Frequency(r)
  def kHz(r: ℝ): Frequency            = Frequency(r * 1e3)

  val C: Note                         = Note(Pitch(0))
  val D: Note                         = Note(Pitch(2))
  val E: Note                         = Note(Pitch(4))
  val F: Note                         = Note(Pitch(5))
  val G: Note                         = Note(Pitch(7))
  val A: Note                         = Note(Pitch(9))
  val B: Note                         = Note(Pitch(11))

//****************************************************************************

  implicit final class BoolSyntax(a: Bool)
  {
    def implies(b: Bool): Bool        = !a || b
    def iff    (b: Bool): Bool        =  a == b
  }

  implicit final class PartialOrderingSyntax[S](s: S)(implicit α: PartialOrdering[S])
  {
    def <    (t: S): Bool             = α.lt(s,t)
    def <=   (t: S): Bool             = α.lteq(s,t)
    def >    (t: S): Bool             = α.gt(s,t)
    def >=   (t: S): Bool             = α.gteq(s,t)
    def equiv(t: S): Bool             = α.equiv(s,t)
  }

  implicit final class OrderingSyntax[S](s: S)(implicit α: Ordering[S])
  {
    def max(t: S): S                  = α.max(s,t)
    def min(t: S): S                  = α.min(s,t)
  }

  def ∅[α]: Set[α]                    = Set[α]()

  implicit final class ElementSyntax[α](e: α)
  {
    def ∈ (s: Set[α]): Bool           =  s.contains(e)
    def ∉ (s: Set[α]): Bool           = !s.contains(e)
  }

  implicit final class SetSyntax[α](s: Set[α])
  {
    def \ (t: Set[α]): Set[α]         =  s.diff(t)
    def ∪ (t: Set[α]): Set[α]         =  s.union(t)
    def ∩ (t: Set[α]): Set[α]         =  s.intersect(t)
    def ⊖ (t: Set[α]): Set[α]         =  s.union(t) diff s.intersect(t)
    def ⊂ (t: Set[α]): Bool           =  s.subsetOf(t) && !t.subsetOf(s)
    def ⊃ (t: Set[α]): Bool           =  t.subsetOf(s) && !s.subsetOf(t)
    def ⊄ (t: Set[α]): Bool           = !s.subsetOf(t) ||  t.subsetOf(s)
    def ⊅ (t: Set[α]): Bool           = !t.subsetOf(s) ||  s.subsetOf(t)
    def ⊆ (t: Set[α]): Bool           =  s.subsetOf(t)
    def ⊇ (t: Set[α]): Bool           =  t.subsetOf(s)
    def ⊈ (t: Set[α]): Bool           = !s.subsetOf(t)
    def ⊉ (t: Set[α]): Bool           = !t.subsetOf(s)
    def ∋ (e: α)     : Bool           =  s.contains(e)
    def ∌ (e: α)     : Bool           = !s.contains(e)
  }

  implicit final class MonoidSyntax[M](m: M)(implicit α: Monoid[M])
  {
    def unary_+ : M                   = m
    def + (n: M): M                   = α.plus(m,n)
  }

  implicit final class GroupSyntax[G](f: G)(implicit α: Group[G])
  {
    def unary_- : G                   = α.negate(f)
    def - (g: G): G                   = α.plus(f,α.negate(g))
  }

  implicit final class ActionSyntax[S,G](s: S)(implicit α: Action[S,G])
  {
    def + (g: G): S                   = α.apply(s,g)
    def - (g: G): S                   = α.apply(s,α.negate(g))
  }

  implicit final class TorsorSyntax[S,G](s: S)(implicit α: Torsor[S,G])
  {
    def - (t: S): G                   = α.delta(t,s)
  }

  implicit final class TransposingSyntax[S](s: S)(implicit α: Action[S,ℤ])
  {
    def ♭                             = α.apply(s,-1)
    def ♮                             = s
    def ♯                             = α.apply(s,1)
  }

//****************************************************************************

  implicit object torsorℤℤ extends Torsor[ℤ,ℤ]
  {
    def zero             : ℤ          = 0
    def negate(i: ℤ)     : ℤ          = -i
    def plus  (i: ℤ,j: ℤ): ℤ          = i + j
    def apply (i: ℤ,j: ℤ): ℤ          = i + j
    def delta (i: ℤ,j: ℤ): ℤ          = j - i
  }

  implicit object torsorℝℝ extends Torsor[ℝ,ℝ]
  {
    def zero             : ℝ          = 0
    def negate(i: ℝ)     : ℝ          = -i
    def plus  (i: ℝ,j: ℝ): ℝ          = i + j
    def apply (r: ℝ,s: ℝ): ℝ          = r + s
    def delta (r: ℝ,s: ℝ): ℝ          = s - r
  }
}

//****************************************************************************
