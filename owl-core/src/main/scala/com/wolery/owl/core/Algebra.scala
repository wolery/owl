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
 * for all `m` in `M`, where `⋅` denotes the function `operate`.
 *
 * In other words, `M` is a semigroup with an identity element.
 *
 * @tparam M  The underlying set on which the monoid operation acts.
 *
 * @see    [[https://en.wikipedia.org/wiki/Monoid Monoid (Wikipedia)]]
 * @see    [[MonoidSyntax]]
 * @author Jonathon Bell
 */
trait Monoid[M]
{
  /**
   * The identity element of the monoid `M`;  that is,  the unique element `e`
   * of `M` such that `e ⋅ m = m = m ⋅ e` for all `m` in `M`, where ⋅  denotes
   * the function `operate`.
   */
  val e: M

  /**
   * Returns the product of the given pair of elements, whatever this may mean
   * for the specific algebraic structure in question.
   *
   * The function is ''associative''; that is, `(m₁ ⋅ m₂) ⋅ m₃ = m₁ ⋅ (m₂ ⋅ m₃)`
   * for all `m` in `M`, where `⋅` denotes the function `operate`.
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
 * for all `g` in `G`,  where `⋅` and  `-` denote the  functions `operate` and
 * `inverse` respectively.
 *
 * In other words, `G` is a monoid in which every element has an inverse.
 *
 * @tparam G  The underlying set on which the group operation acts.
 *
 * @see    [[http://en.wikipedia.org/wiki/Group_(mathematics) Group (Wikipedia)]]
 * @see    [[GroupSyntax]]
 * @author Jonathon Bell
 */
trait Group[G] extends Monoid[G]
{
  /**
   * Returns the inverse of the element `g`;  that is, the unique element `-g`
   * of `G` such  that `g ⋅ -g = e = -g ⋅ g`,  where `⋅` denotes  the function
   * `operate`.
   */
  def inverse(g: G): G
}

/**
 * Describes a (right) action of the group `G` upon the carrier set `S`.
 *
 * Instances satisfy the axioms:
 * {{{
 *            s + e = s                                  identity
 *    s + (g₁ ⋅ g₂) = (s + g₁) + g₂                      compatability
 * }}}
 * for all `s` in `S` and `g` in `G`,  where `⋅` and  `+` denote the functions
 * `operate` and `apply` respectively.
 *
 * In other words, `+` is a homomorphism from `G` into `Sym(S)`, the symmetric
 * group consisting of all permutations of `S`,  regarded as a group under the
 * operation of function composition.
 *
 * Group actions,  especially those of ℤ, the integers regarded as an additive
 * group, are of special interest to us in Owl because they enable us to model
 * permutations of pitches, notes, scales, and the like,  using simple integer
 * arithmetic.
 *
 * @tparam G  A group that acts upon the carrier set  `S` via the mapping `apply`.
 * @tparam S  A non-empty set acted upon by the group `G` via the mapping `apply`.
 *
 * @see    [[http://en.wikipedia.org/wiki/Group_action Group action (Wikipedia)]]
 * @see    [[ActionSyntax]]
 * @author Jonathon Bell
 */
abstract class Action[S,G](implicit val group: Group[G])
{
  /**
   * Applies an element of the group `G` to an element of the carrier set `S`,
   * whatever this may mean for the specific algebraic structure in question.
   *
   * Note that the mapping `_ + g` is necessarily a permutation of the carrier
   * set `S`, and thus that `apply` is a  homomorphism from `G` into `Sym(S)`,
   * the symmetric group consisting of all of permutations of `S`, regarded as
   * a group under the operation of function composition.
   */
  def apply(s: S,g: G): S
}

/**
 * Describes a regular (right) action of the group `G` upon the carrier set
 * `S`.
 *
 * By ''regular'' we mean that the action is ''sharply transitive'';  that is,
 * for every pair of elements  `s₁` and `s₂` in `S`  there exists a ``unique``
 * element `s₂ - s₁` in `G` such that `s₁ + (s₂ - s₁) = s₂`, where `+` and `-`
 * denote the functions `apply` and `delta` respectively.
 *
 * Thus,  in addition to the axioms for a group action, instances also satisfy
 * the axiom:
 * {{{
 *    s₁ + (s₂ - s₁) = s₂                                regularity
 * }}}
 * for all `s` in `S`, where `+` and `-` denote the functions `apply` and
 * `delta` respectively.
 *
 * We say that `S` is a ''torsor'' for the group `G`,  or simply that `S` is a
 * ''G-torsor''.
 *
 * Torsors, especially those of ℤ, the integers regarded as an additive group,
 * and ℤ/''n''ℤ, the ring of integers modulo ''n'', are of special interest to
 * us in Owl because they they make precise the musical notion of ''interval''
 * between notes, pitches, frequencies, and so on.
 *
 * @tparam G  A group that acts regularly upon the carrier set  `S` via the mapping `apply`.
 * @tparam S  A non-empty set acted upon regularly by the group `G` via the mapping `apply`.
 *
 * @see    [[http://en.wikipedia.org/wiki/Principal_homogeneous_space Torsor (Wikipedia)]]
 * @see    [[http://math.ucr.edu/home/baez/torsors.html Torsors Made Easy (John Baez)]]
 * @see    [[TorsorSyntax]]
 * @author Jonathon Bell
 */
trait Torsor[S,G] extends Action[S,G]
{
  /**
   * Returns the 'delta' between the given pair of elements of the carrier set
   * `S`; that is, the unique element `g` in `G` such that `s + g = t`,  where
   * `+`, denotes the function `apply`.
   *
   * @param  s An element of the carrier set `S`.
   * @param  t An element of the carrier set `S`.
   * @return The unique element of `G` that maps `s` into `t`.
   */
  def delta(s: S,t: S): G
}

//****************************************************************************
