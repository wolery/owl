//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Describes a type that is inhabited by a finite set of values.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl
package core

/**
 * Describes a type that is inhabited by a finite set of values.
 *
 * Every finite type is isomorphic to some initial segment `[0, .. , n)` of ℕ,
 * the set of natural numbers, although the bijection is of course not unique,
 * there being one for each of the `n!` permutations of the `n` values.
 *
 * Any such mapping is sufficient to uniquely encode the values of the type as
 * natural numbers, however, allowing us to easily enumerate them,  store them
 * in bit sets, and so forth.
 *
 * Instances satisfy the axioms:
 * {{{
 *           0 ≤ toℕ(a) < size                            α is inhabited
 *    fromℕ ∘ toℕ   =  identity[α]                        toℕ   is injective
 *    toℕ ∘ toFrom  =  identity[ℕ]                        fromℕ is injective
 * }}}
 * for all `a` in `α`, where ∘ denotes function composition.
 *
 * @tparam α  A type inhabited by a finite set of values.
 *
 * @author Jonathon Bell
 */
trait Finite[α]
{
  /**
   * The number of values inhabiting the type α. The size must be greater than
   * 0; that is, the type `α` is inhabited.
   */
  val size: ℕ

  /**
   * Encodes the given value as a natural number in the range `[0, size)'.
   *
   * Implements a bijection from the values of type `α` onto the initial `size`
   * natural numbers.
   *
   * @param  a  Any value of type α.
   *
   * @return A natural number in the range `[0,n)'.
   */
  def toℕ(a: α): ℕ

  /**
   * Recovers a value of type `α` from its encoding as a natural number in the
   * range `[0, size)`.
   *
   * Undefined for values outside of this interval
   *
   * Effects a bijection from the initial `size` natural numbers onto the type
   * `α`.
   *
   * @param  n  A natural number in the range `[0, size)`.
   *
   * @return A value of type `α`. Undefined when `n ∉ [0, size)`.
   */
  def fromℕ(n: ℕ): α
}

//****************************************************************************
