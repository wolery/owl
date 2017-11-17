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
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

/**
 * Describes the operations that endow the data type `M` with the structure of
 * a monoid.
 *
 * Instances satisfy the axioms:
 * {{{
 *            e ⋅ m = m = m ⋅ e                          identity
 *     (m₁ ⋅ m₂) ⋅ m₃ = m₁ ⋅ (m₂ ⋅ m₃)                   associativity
 * }}}
 * for all `m` in `M`, where ⋅ denotes the function `operate`. In other words,
 * `M` is a semigroup with an identity element.
 *
 * @tparam M  The underlying set on which the monoid operation acts.
 *
 * @see    [[https://en.wikipedia.org/wiki/Monoid Monoid (Wikipedia)]]
 * @author Jonathon Bell
 */
trait Monoid[M]
{
  /**
   * The identity element; that is, the unique element `e` in `M` such that
   * `e ⋅ m = m = m ⋅ e` for all `m` in `M`.
   */
  val e: M

  /**
   * Returns the product of the given elements, whatever this may mean for the
   * specific algebraic structure in question.
   *
   * The function is associative; that is, `(m₁ ⋅ m₂) ⋅ m₃ = m₁ ⋅ (m₂ ⋅ m₃)`
   * for all `m` in `M`.
   */
  def operate(m: M,n: M): M
}

/**
 * Describes the operations that endow the data type `G` with the structure of
 * a group.
 *
 * Instances satisfy the axioms:
 * {{{
 *            e ⋅ g = g = g ⋅ e                          identity
 *     (g₁ ⋅ g₂) ⋅ g₃ = g₁ ⋅ (g₂ ⋅ g₃)                   associativity
 *           g ⋅ -g = e = -g ⋅ g                         invertability
 * }}}
 * for all `g` in `G`, where ⋅ and - denote the functions `operate` and `inverse`
 * respectively. In other words, `G` is a monoid in which every element has an
 * inverse.
 *
 * @tparam G  The underlying set on which the group operation acts.
 *
 * @see    [[http://en.wikipedia.org/wiki/Group_(mathematics) Group (Wikipedia)]]
 * @author Jonathon Bell
 */
trait Group[G] extends Monoid[G]
{
  /**
   * Returns the inverse of the element `g`; that is, the unique element `-g`
   * in `G` such that `g + -g = e = -g + g`.
   */
  def inverse(g: G): G
}

/**
 * Describes a (right) action of the group `G` upon the carrier set `S`.
 *
 * Instances satisfy the axioms:
 * {{{
 *            s ⋅ e = s                                  identity
 *    s ⋅ (g₁ ⋅ g₂) = (s ⋅ g₁) + g₂                      compatability
 * }}}
 * for all `s` in `S` and `g` in `G`, where ⋅ denotes the members
 * `zero` and `plus` respectively.
 *
 * @tparam G  A group that acts upon the carrier set `S` via the mapping `apply`.
 * @tparam S  A non-empty set acted upon by the group `G` via the mapping `apply`.
 *
 * @see    [[http://en.wikipedia.org/wiki/Group_action Group action (Wikipedia)]]
 */
abstract class Action[S,G](implicit val group: Group[G])
{
  /**
   * Applies an element of the group to an element of the carrier set, whatever
   * this might mean for the actual group action in question.
   *
   * Notice that the map `(_ + g)` is necessarily a permutation of the carrier
   * set `S`, and thus `apply` is a homomorphism from `G` into Aut(`S`),
   * the set of permutations of `S` regarded as a group under composition of
   * mappings.
   */
  def apply(s: S,g: G): S
}

/**TODO
 * Describes a regular (right) action of the group `G` upon the carrier set
 * `S`.
 *
 * Here the term `regular` means that the action is `sharply transitive`;
 * that is, for every pair of elements `s₁` and `s₂` in `S` there exists
 * a unique element `s₂ - s₁` in `G` such that `s₁` + (`s₂` - `s₁`) =
 * `s₂`, where + and - denote the members `apply` and `delta` respectively.
 *
 * Thus in addition to the axioms for a group action, instances also satisfy
 * the axiom:
 * {{{
 *    s₁ + (s₂ - s₁) = s₂                                regularity
 * }}}
 * for all `s` in `S`, where + and - denote the members `apply` and `delta`
 * respectively.
 *
 * We say that `S` is a `torsor` for the group `G`, or simply that `S`
 * is a `G-torsor`.
 *
 * @tparam G  A group that acts regularly upon the carrier set `S` via the mapping `apply`.
 * @tparam S  A non-empty set acted upon regularly by the group `G` via the mapping `apply`.
 *
 * @see    [[http://en.wikipedia.org/wiki/Principal_homogeneous_space Torsor (Wikipedia)]]
 * @see    [[http://math.ucr.edu/home/baez/torsors.html Torsors Made Easy (John Baez)]]
 */
trait Torsor[S,G] extends Action[S,G]
{
  /**TODO
   * Returns the 'delta' between any pair of elements of the carrier set; that
   * is,  the unique group element that when applied to the first element maps
   * it into the second.
   *
   * @param  s An element of the carrier set `S`.
   * @param  t An element of the carrier set `S`.
   * @return The unique group element that maps `s` to `t`.
   */
  def delta(s: S,t: S): G
}

//****************************************************************************
