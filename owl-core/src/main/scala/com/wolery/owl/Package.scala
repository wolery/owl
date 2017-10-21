//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Common definitions used throughout Owl.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery

/**
 * Common definitions used throughout Owl.
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
   * A truth value, represented as a native boolean.
   */
  type Bool = Boolean

  /**
   * A name for some entity, represented as a native string.
   */
  type Name = String

  /**
   * Extends the boolean type with additional syntax.
   *
   * @param  a  A truth value.
   * @param  b  A truth value.
   */
  implicit final
  class BoolEx(val a: Bool) extends AnyVal
  {
    def iff    (b:  Bool): Bool =  a == b
    def implies(b: ⇒Bool): Bool = !a || b
  }

  /**
   * Extends partially ordered types with additional syntax.
   *
   * The variable α ranges over instances of the type class PartialOrdering.
   *
   * @tparam α  An instance of the type class PartialOrdering.
   * @param  a  A value of type α.
   * @param  b  A value of type α.
   */
  implicit final
  class PartialOrderingEx[α](a: α)(implicit ε: PartialOrdering[α])
  {
    def <    (b: α): Bool = ε.lt(a,b)
    def <=   (b: α): Bool = ε.lteq(a,b)
    def >    (b: α): Bool = ε.gt(a,b)
    def >=   (b: α): Bool = ε.gteq(a,b)
    def equiv(b: α): Bool = ε.equiv(a,b)
  }

  /**
   * Extends ordered types with additional syntax.
   *
   * The variable α ranges over instances of the type class Ordering.
   *
   * @tparam α  An instance of the type class Ordering.
   * @param  a  A value of type α.
   * @param  b  A value of type α.
   */
  implicit final
  class OrderingEx[α](a: α)(implicit ε: Ordering[α])
  {
    def max(b: α): α = ε.max(a,b)
    def min(b: α): α = ε.min(a,b)
  }

  /**
   * Extends the type Set[ε] with additional syntax.
   *
   * Enables us to refer to the methods of Set[ε] by their traditional symbolic
   * names:
   *
   *  - \  set difference
   *  - ∪  set union
   *  - ∩  set intersection
   *  - ⊖  symmetric difference
   *  - ⊂  set inclusion (proper)
   *  - ⊆  set inclusion
   *  - ∈  set membership
   *  - ∅  the empty set
   *
   * @tparam ε  The type of an element.
   * @param  s  A set of elements.
   * @param  t  A set of elements.
   * @param  e  A (candidate) set element.
   */
  implicit final
  class SetEx[ε](val s: Set[ε]) extends AnyVal
  {
    def \ (t: Set[ε]): Set[ε]  =  s.diff(t)
    def ∪ (t: Set[ε]): Set[ε]  =  s.union(t)
    def ∩ (t: Set[ε]): Set[ε]  =  s.intersect(t)
    def ⊖ (t: Set[ε]): Set[ε]  =  s.union(t) diff s.intersect(t)
    def ⊂ (t: Set[ε]): Bool    =  s.subsetOf(t) && !t.subsetOf(s)
    def ⊃ (t: Set[ε]): Bool    =  t.subsetOf(s) && !s.subsetOf(t)
    def ⊄ (t: Set[ε]): Bool    = !s.subsetOf(t) ||  t.subsetOf(s)
    def ⊅ (t: Set[ε]): Bool    = !t.subsetOf(s) ||  s.subsetOf(t)
    def ⊆ (t: Set[ε]): Bool    =  s.subsetOf(t)
    def ⊇ (t: Set[ε]): Bool    =  t.subsetOf(s)
    def ⊈ (t: Set[ε]): Bool    = !s.subsetOf(t)
    def ⊉ (t: Set[ε]): Bool    = !t.subsetOf(s)
    def ∋ (e: ε)     : Bool    =  s.contains(e)
    def ∌ (e: ε)     : Bool    = !s.contains(e)
  }

  /**
   * Extends the element type ε with additional syntax.
   *
   * @tparam ε  The type of an element.
   * @param  s  A set of elements.
   * @param  e  A (candidate) set element.
   *
   * @see    [[SetEx]]
   */
  implicit final
  class ElementEx[ε](val e: ε) extends AnyVal
  {
    def ∈ (s: Set[ε]): Bool =  s.contains(e)
    def ∉ (s: Set[ε]): Bool = !s.contains(e)
  }

  /**
   * The empty set.
   *
   * @tparam ε  The type of an element.
   *
   * @return An empty set of type Set[ε].
   * @see    [[SetEx]]
   */
  def ∅[ε]: Set[ε] = Set[ε]()
}

//****************************************************************************
