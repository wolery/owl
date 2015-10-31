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

  def Hz (r: ℝ): Frequency            = new Frequency(r)
  def kHz(r: ℝ): Frequency            = new Frequency(r * 1e3)

  val C: Note                         = Note.notes(0)
  val D: Note                         = Note.notes(2)
  val E: Note                         = Note.notes(4)
  val F: Note                         = Note.notes(5)
  val G: Note                         = Note.notes(7)
  val A: Note                         = Note.notes(9)
  val B: Note                         = Note.notes(11)

//****************************************************************************

  implicit final class BoolSyntax(a: Bool)
  {
    def implies(b: Bool): Bool        = !a || b
    def iff    (b: Bool): Bool        =  a == b
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

  implicit final class ElementSyntax[α](e: α)
  {
    def ∈ (s: Set[α]): Bool           =  s.contains(e)
    def ∉ (s: Set[α]): Bool           = !s.contains(e)
  }

  def ∅[α]: Set[α]                    = Set[α]()

  implicit final class GroupSyntax[G](f: G)(implicit α: Group[G])
  {
    def unary_+ : G                   = f
    def unary_- : G                   = α.negate(f)
    def + (g: G): G                   = α.plus(f, g)
    def - (g: G): G                   = α.plus(f,-g)
  }

  implicit final class ActionSyntax[S,G](s: S)(implicit α: Action[S,G])
  {
    def + (g: G): S                   = α.apply(s, g)
    def - (g: G): S                   = α.apply(s,-g)
  }

  implicit final class TorsorSyntax[S,G](s: S)(implicit α: Torsor[S,G])
  {
    def - (t: S): G                   = α.delta(t,s)
    def ⊣ (t: S): G                   = α.delta(s,t)
    def ⊢ (t: S): G                   = α.delta(t,s)
  }

  implicit final class TransposingSyntax[S](s: S)(implicit α: Action[S,ℤ])
  {
    def ♭                             = α.apply(s,-1)
    def ♮                             = s
    def ♯                             = α.apply(s,1)
  }

//****************************************************************************

  implicit object Torsorℤℤ extends Torsor[ℤ,ℤ]
  {
    def zero             : ℤ          = 0
    def negate(i: ℤ)     : ℤ          = -i
    def plus  (i: ℤ,j: ℤ): ℤ          = i + j
    def apply (i: ℤ,j: ℤ): ℤ          = i + j
    def delta (i: ℤ,j: ℤ): ℤ          = j - i
  }

  implicit object Torsorℝℝ extends Torsor[ℝ,ℝ]
  {
    def zero             : ℝ          = 0
    def negate(i: ℝ)     : ℝ          = -i
    def plus  (i: ℝ,j: ℝ): ℝ          = i + j
    def apply (r: ℝ,s: ℝ): ℝ          = r + s
    def delta (r: ℝ,s: ℝ): ℝ          = s - r
  }
}

//****************************************************************************
