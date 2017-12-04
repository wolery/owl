//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Describes types that are inhabited by finite sets of values.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl
package core

/**
 * Describes types that are inhabited by finite sets of values.
 *
 * Every finite type is isomorphic to some initial segment `[0, .. , n)` of ℕ,
 * the set of natural numbers, although the bijection is of course not unique,
 * there being one for each of the `n!` permutations of the `n` values.
 *
 * Any such mapping is sufficient to uniquely encode the values of the type as
 * natural numbers, however, allowing us to easily enumerate them,  store them
 * in bit sets, and so on.
 *
 * Instances satisfy the axioms:
 * {{{
 *           size   >  0                                  α is inhabited
 *    fromℕ ∘ toℕ   =  identity[α]                        toℕ   is injective
 *    toℕ ∘ toFrom  =  identity[ℕ]                        fromℕ is injective
 * }}}
 * for all `a` in `α`, where ∘ denotes function composition.
 *
 * @tparam α  A type that inhabited by a finite set of values.
 *
 * @author Jonathon Bell
 */
trait Finite[α]
{
  /**
   * The number of values inhabiting the type α.
   */
  val size: ℕ

  /**
   * TODO
   *
   * @param  a  Any value of type α.
   */
  def toℕ(a: α): ℕ

  /**
   * TODO
   *
   * @param  i  A natural number in the range `[0, size)`.
   */
  def fromℕ(i: ℕ): α
}

//****************************************************************************
