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
 * Instances must satisfy the axioms:
 * {{{
 *            0 + g = g = g + 0                          identity
 *         g + (-g) = 0 = (-g) + g                       invertability
 *     (g₁ + g₂) + g₃ = g₁ + (g₂ + g₃)                   associativity
 * }}}
 * for all ''g'', ''g₁'', ''g₂'', ''g₃'' in ''G'', where `0`, `-`, and `+`
 * denote the members `zero`, `negate`, and `plus` respectively.
 *
 * @see   http://en.wikipedia.org/wiki/Group_(mathematics)
 */
trait Group[G]
{
  /**
   * Returns the identity element of the additive group.
   */
  def zero: G

  /**
   * Returns the additive inverse of the given group element.
   */
  def negate(g: G): G

  /**
   * Returns the 'sum' of the two given group elements, whatever this may mean
   * for the actual group in question.
   */
  def plus(a: G,b: G): G
}

/**
 * Describes a (right) action of the group ''G'' upon the carrier set ''S''.
 *
 * Instances must satisfy the axioms:
 * {{{
 *            s + 0 = s                                  identity
 *    s + (g₁ + g₂) = (s + g₁) + g₂                      compatability
 * }}}
 * for all ''s'' in ''S'' and all ''g₁'', ''g₂'' in ''G'', where `0` and `+`
 * denote the members `zero` and `plus` respectively.
 *
 * @see   http://en.wikipedia.org/wiki/Group_action
 */
trait Action[S,G] extends Group[G]
{
  /**
   * Applies an element of the group ''G'' to an element of the carrier set
   * ''S'', whatever this may mean for the actual group action in question.
   *
   * Notice that the mapping `(_ + g)` is necessarily a ''permutation'' of the
   * carrier set ''S'',  and hence that the map `apply` effects a homomorphism
   * from ''G'' into ''Sym(S)'', the permutations of ''S'' regarded as a group
   * under composition of mappings.
   */
  def apply(s: S,g: G): S
}

/**
 * Describes a regular (right) action of the group ''G'' upon the carrier set
 * ''S''.
 *
 * Here the term ''regular'' means that the action is ''sharply transitive'':
 * that is, for any pair of elements ''s₂'' and ''s₂'' in ''S'', there exists
 * a unique element ''s₂ - s₁'' in ''G'' such that ''s₁ + (s₂ - s₁) = s₂'',
 * where we write `-` for the member `delta`.
 *
 * Thus in addition to usual the axioms for a group action instances must also
 * satisfy the axiom:
 * {{{
 *    s₁ + (s₂ - s₁) = s₂                                regularity
 * }}}
 *
 * We say that ''S'' is a ''torsor'' for the group ''G'', or simply that it is
 * ''G-torsor''.
 *
 * @see   http://en.wikipedia.org/wiki/Principal_homogeneous_space
 */
trait Torsor[S,G] extends Action[S,G]
{
  /**
   * Returns the delta between any pair of elements of the carrier set ''S'';
   * that is, the unique group element in ''G'' that when applied to the first
   * argument maps it into the second.
   *
   * @param  s an element of the carrier set ''S''
   * @param  t another element of the carrier set ''S''
   * @return the unique group element that maps ''s'' to ''t''
   */
  def delta(s: S,t: S): G
}

trait Transposing[S] extends Action[S,ℤ]
{
  def zero: ℤ                 = 0
  def negate(a: ℤ): ℤ         = -a
  def plus  (a: ℤ,b: ℤ): ℤ    = a + b
}

trait Intervallic[S] extends Torsor[S,ℤ]
{
  def zero: ℤ                 = 0
  def negate(a: ℤ): ℤ         = -a
  def plus  (a: ℤ,b: ℤ): ℤ    = a + b
}

//****************************************************************************
