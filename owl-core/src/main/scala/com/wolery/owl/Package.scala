//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : Header:
//*
//*
//*  Purpose : Common definitions for use throughout the Owl project.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery

/**
 * Common definitions for use throughout the Owl project.
 *
 * @author Jonathon Bell
 */
package object owl
{
  /**
   * A non-negative integer, represented as a native 32 bit signed integer.
   */
  type ℕ = Int

  /**
   * An integer, represented as a native 32 bit signed integer.
   */
  type ℤ = Int

  /**
   * A real number, represented as a double precision floating point number.
   */
  type ℝ = Double

  /**
   * A Boolean truth value, represented as a native boolean.
   */
  type Bool = Boolean

  /**
   * A name for some entity, represented as a native string.
   */
  type Name = String

  /**
   * Extends type Bool with additional methods.
   */
  implicit final class BoolEx(val a: Bool) extends AnyVal
  {
    /**
     * TODO
     * 
     * @param  b
     * 
     * @return a == b
     */
    def iff    (b:  Bool): Bool       =  a == b
    def implies(b: ⇒Bool): Bool       = !a || b
  }

  /**
   * TO DO
   */
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
