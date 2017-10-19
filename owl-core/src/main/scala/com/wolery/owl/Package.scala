//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : Header:
//*
//*
//*  Purpose : TODO
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery

/**
 * TODO
 *
 * @author Jonathon Bell
 */
package object owl
{
  /**
   * Represents a natural - that is, non negative - number as a 32 bit integer.
   */
  type ℕ = Int

  /**
   * Represents an integer as a signed 32 bit integer.
   */
  type ℤ = Int

  /**
   * Represents a real number a double precision IEEE floating point number.
   */
  type ℝ = Double

  /**
   * Represents a boolean truth value as a native Scala boolean.
   */
  type Bool = Boolean

  /**
   * Represents the name of some entity as a Scala string.
   */
  type Name = String



  implicit final class BoolSyntax(a: Bool)
  {
    def iff    (b:  Bool): Bool       =  a == b
    def implies(b: ⇒Bool): Bool       = !a || b
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
}

//****************************************************************************
