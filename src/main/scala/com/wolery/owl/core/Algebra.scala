//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Defines type classes for the various algebraic constructions we
//*            shall be making use of.
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

/**
 * Describes the operations that endow the type ''G'' with the structure of an
 * additive group.
 *
 * Instances must satisfy the following axioms:
 * {{{
 *            0 + g = g = g + 0                        identity
 *         g + (-g) = 0 = (-g) + g                     invertability
 *     (g₁ + g₂) + g₃ = g₁ + (g₂ + g₃)                 associativity
 * }}}
 * for all elements ''g'', ''g₁'', ''g₂'', ''g₃'' of ''G'', where we write 0,
 * -, and + for the members ''zero'', ''negate'', and ''plus'' respectively.
 *
 * @see   http://en.wikipedia.org/wiki/Group_(mathematics)
 */
trait Group[G]
{
  /**
   * Returns the identity element of this additive group.
   */
  def zero: G

  /**
   * Returns the additive inverse of the given group element.
   */
  def negate(a: G): G

  /**
   * Returns the 'sum' of the two given group elements, whatever this may mean
   * for the actual group in question.
   */
  def plus(a: G,b: G): G
}

trait Action[S,G] extends Group[G]
{
  def apply(s: S,g: G) : S
}

trait Torsor[S,G] extends Action[S,G]
{
  def delta(s: S,t: S) : G
}

trait Transposing[S] extends Action[S,ℤ]
{
  def zero             : ℤ    = 0
  def negate(a: ℤ)     : ℤ    = -a
  def plus  (a: ℤ,b: ℤ): ℤ    = a + b
}

trait Intervallic[S] extends Torsor[S,ℤ]
{
  def zero             : ℤ    = 0
  def negate(a: ℤ)     : ℤ    = -a
  def plus  (a: ℤ,b: ℤ): ℤ    = a + b
}

//****************************************************************************
