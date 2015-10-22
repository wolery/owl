//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Defines type classes for the various algebraic constructions we
//*            shall be leveraging throughout the library.
//*
//*            Our principle interest is in the action of the ring of integers
//*            upon such sets as the (even tempered) pitches and notes,  hence
//*            our choice of an additive notation for groups and the like, and
//*            our singling out of transposing and intervallic sets as special
//*            instances of these more general constructs.
//*
//*            Implicit wrappers that provide  syntactic sugar for the classes
//*            in the form of overloaded arithmetic operators are also defined
//*            within the core package object.
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
 * @tparam G the underlying set on which these group operations act
 * @see    http://en.wikipedia.org/wiki/Group_(mathematics)
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
 * @tparam G a group that acts upon the carrier set ''S'' via the mapping `apply`
 * @tparam S a non-empty set acted upon by the group ''G'' via the mapping `apply`
 * @see    http://en.wikipedia.org/wiki/Group_action
 */
trait Action[S,G] extends Group[G]
{
  /**
   * Applies an element of the group ''G'' to an element of the carrier set
   * ''S'', whatever this may mean for the actual group action in question.
   *
   * Notice that the map `(_ + g)` is necessarily a permutation of the carrier
   * set ''S'', and thus `apply` is a homomorphism from ''G'' into Aut(''S''),
   * the set of permutations of ''S'' regarded as a group under composition of
   * mappings.
   */
  def apply(s: S,g: G): S
}

/**
 * Describes a regular (right) action of the group ''G'' upon the carrier set
 * ''S''.
 *
 * Here the term ''regular'' means that the action is ''sharply transitive'':
 * that is, for every pair of elements ''s₁'' and ''s₂'' in ''S'' there exists
 * a unique element ''s₂ - s₁'' in ''G'' such that ''s₁ + (s₂ - s₁) = s₂'',
 * where `-` denotes the member `delta`.
 *
 * Thus in addition to the above axioms for a group action instances must also
 * satisfy the axiom:
 * {{{
 *    s₁ + (s₂ - s₁) = s₂                                regularity
 * }}}
 * for all ''s₁'' and ''s₂'' in ''S'', where `-` denotes the member `delta`
 *
 * We say that ''S'' is a ''torsor'' for the group ''G'', or simply that ''S''
 * is a ''G-torsor''.
 *
 * @tparam G a group that acts regularly upon the carrier set ''S'' via the mapping `apply`
 * @tparam S a non-empty set acted upon regularly by the group ''G'' via the mapping `apply`
 * @see    http://en.wikipedia.org/wiki/Principal_homogeneous_space
 * @see    http://math.ucr.edu/home/baez/torsors.html
 */
trait Torsor[S,G] extends Action[S,G]
{
  /**
   * Returns the 'delta' between a pair of elements of the carrier set ''S'';
   * that is, the unique group element in ''G'' that when applied to the first
   * element maps it into the second.
   *
   * @param  s an element of the carrier set ''S''
   * @param  t an element of the carrier set ''S''
   * @return the unique group element that when applied to ''s'' maps it to ''t''
   */
  def delta(s: S,t: S): G
}

/**
 * Describes a (right) action of the integers ℤ upon the carrier set ''S''.
 *
 * Transposing sets are of central importance within Owl because they allow us
 * to model transpositions of set elements using simple integer arithmetic.
 *
 * Notice that because ℤ is a unital ring, its action upon ''S'' is completely
 * determined by the mapping `apply(_,1)`.
 *
 * @tparam S a non-empty set acted upon by the integers via the mapping `apply`
 */
trait Transposing[S] extends Action[S,ℤ]
{
  def zero: ℤ                 = 0
  def negate(a: ℤ): ℤ         = -a
  def plus  (a: ℤ,b: ℤ): ℤ    = a + b
}

/**
 * Describes a regular (right) action of the integers ℤ upon the carrier set
 * ''S''.
 *
 * @tparam S a non-empty set acted upon regularly by the integers via the mapping `apply`
 */
trait Intervallic[S] extends Torsor[S,ℤ]
{
  def zero: ℤ                 = 0
  def negate(a: ℤ): ℤ         = -a
  def plus  (a: ℤ,b: ℤ): ℤ    = a + b
}

//****************************************************************************
