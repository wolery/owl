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


  implicit final class BoolEx(val a: Bool) extends AnyVal
  {
    def iff    (b:  Bool): Bool       =  a == b
    def implies(b: ⇒Bool): Bool       = !a || b
  }

  implicit final class PartialOrderingEx[α](a: α)(implicit ε: PartialOrdering[α])
  {
    def <    (b: α): Bool             = ε.lt(a,b)
    def <=   (b: α): Bool             = ε.lteq(a,b)
    def >    (b: α): Bool             = ε.gt(a,b)
    def >=   (b: α): Bool             = ε.gteq(a,b)
    def equiv(b: α): Bool             = ε.equiv(a,b)
  }

  implicit final class OrderingEx[α](a: α)(implicit ε: Ordering[α])
  {
    def max(b: α): α                  = ε.max(a,b)
    def min(b: α): α                  = ε.min(a,b)
  }

  final def ∅[α]: Set[α]              = Set[α]()

  implicit final class ElementEx[α](val e: α) extends AnyVal
  {
    def ∈ (s: Set[α]): Bool           =  s.contains(e)
    def ∉ (s: Set[α]): Bool           = !s.contains(e)
  }

  implicit final class SetEx[α](val a: Set[α]) extends AnyVal
  {
    def \ (b: Set[α]): Set[α]         =  a.diff(b)
    def ∪ (b: Set[α]): Set[α]         =  a.union(b)
    def ∩ (b: Set[α]): Set[α]         =  a.intersect(b)
    def ⊖ (b: Set[α]): Set[α]         =  a.union(b) diff a.intersect(b)
    def ⊂ (b: Set[α]): Bool           =  a.subsetOf(b) && !b.subsetOf(a)
    def ⊃ (b: Set[α]): Bool           =  b.subsetOf(a) && !a.subsetOf(b)
    def ⊄ (b: Set[α]): Bool           = !a.subsetOf(b) ||  b.subsetOf(a)
    def ⊅ (b: Set[α]): Bool           = !b.subsetOf(a) ||  a.subsetOf(b)
    def ⊆ (b: Set[α]): Bool           =  a.subsetOf(b)
    def ⊇ (b: Set[α]): Bool           =  b.subsetOf(a)
    def ⊈ (b: Set[α]): Bool           = !a.subsetOf(b)
    def ⊉ (b: Set[α]): Bool           = !b.subsetOf(a)
    def ∋ (e: α)     : Bool           =  a.contains(e)
    def ∌ (e: α)     : Bool           = !a.contains(e)
  }
}

//****************************************************************************
