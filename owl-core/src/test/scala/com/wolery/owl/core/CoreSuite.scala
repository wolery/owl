//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Custom test suite for testing the core package.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

//****************************************************************************

import org.scalacheck._
import org.scalacheck.Gen._

//****************************************************************************

trait CoreSuite extends OwlSuite
{
  /**
   * Checks that `(M, ⋅)` satisfies the axioms of a semigroup; that is, the
   * function `⋅ : M → M` is associative.
   *
   * @see    [[https://en.wikipedia.org/wiki/Semigroup Semigroup (Wikipedia)]]
   */
  def isSemigroup[M]()(implicit α: Monoid[M],β: Arbitrary[M]): Unit =
  {
    forAll("a","b","c") {(a: M,b: M,c: M) ⇒              // ∀ a,b,c ∈ M
    {
      assert(a ⋅ (b ⋅ c) == (a ⋅ b) ⋅ c,                 "[associativity]")
    }}
  }

  /**
   * Checks that `(M, ⋅)` is a commutative magma; that is, the function `⋅ :
   * M → M` is commutative.
   *
   * @see    [[https://en.wikipedia.org/wiki/Commutative_property
   *         Commutativity (Wikipedia)]]
   */
  def isCommutative[M]()(implicit α: Monoid[M],β: Arbitrary[M]): Unit =
  {
    forAll("a","b") {(a: M,b: M) ⇒                       // ∀ a,b ∈ M
    {
      assert(a ⋅ b == b ⋅ a,                             "[commutativity]")
    }}
  }

  /**
   * Checks that `(M, ⋅, e)` satisfies the axioms of a monoid; that it, it is
   * a semigroup with an identity element `e`.
   *
   * @see    [[https://en.wikipedia.org/wiki/Monoid Monoid (Wikipedia)]]
   */
  def isMonoid[M]()(implicit α: Monoid[M],β: Arbitrary[M]): Unit =
  {
    import α._                                           // For identity e

    isSemigroup[M]()                                     // Semigroup axioms

    forAll("a") {(m: M) ⇒                                // ∀ m ∈ M
    {
      assert(e ⋅ m == m,                                 "[left identity]")
      assert(m ⋅ e == m,                                 "[right identity]")
    }}
  }

  /**
   * Checks that `(G, ⋅, e)` satisfies the axioms of a group;  that is, it is
   * a monoid in which every element possesses an inverse.
   *
   * @see    [[https://en.wikipedia.org/wiki/Group_(mathematics) Group
   *         (Wikipedia)]]
   */
  def isGroup[G]()(implicit α: Group[G],β: Arbitrary[G]): Unit =
  {
    import α._                                           // For identity e

    isMonoid[G]()                                        // Monoid axioms

    forAll("g") {(g: G) ⇒                                // ∀ g ∈ G
    {
      assert(-g ⋅  g == e,                               "[left inverse]")
      assert( g ⋅ -g == e,                               "[right inverse]")
    }}
  }

  /**
   * Checks that `G` acts upon the carrier set `S`; that is, `apply` effects a
   * group homomorphism from `G` into `Sym(S)`.
   *
   * @see    [[https://en.wikipedia.org/wiki/Group_action Group action
   *         (Wikipedia)]]
   */
  def isAction[S,G]()(implicit α: Action[S,G],β: Arbitrary[S],γ: Arbitrary[G]): Unit =
  {
    import α._                                           // For action ops
    import α.group._                                     // For group  ops

    isGroup[G]()                                         // Group axioms

    forAll("s") {(s: S) ⇒                                // ∀ s ∈ S
    {
      assert(s + e == s,                                 "[identity]")
    }}

    forAll("s","f","g") {(s: S,f: G,g: G) ⇒              // ∀ s ∈ S, f,g ∈ G
    {
      assert(s + (f ⋅ g) == (s + f) + g,                 "[compatability +]")
      assert(s - (f ⋅ g) == (s - g) - f,                 "[compatability -]")
      assert(s + f       == s - -f,                      "[negation +]")
      assert(s - f       == s + -f,                      "[negation -]")
    }}
  }

  /**
   * Checks that `G` acts regularly upon the carrier set `S`; that is, `apply`
   * effects a group isomorphism from `G` into `Sym(S)`.
   *
   * @see    [[https://en.wikipedia.org/wiki/Principal_homogeneous_space
   *         Torsor (Wikipedia)]]
   */
  def isTorsor[S,G]()(implicit α: Torsor[S,G],β: Arbitrary[S],γ: Arbitrary[G]): Unit =
  {
    import α._                                           // For action ops

    isAction[S,G]()                                      // Action axioms

    forAll("s","t") {(s: S,t: S) ⇒                       // ∀ s,t ∈ S
    {
      assert(s + delta(s,t) == t,                        "[transitive]")
      assert(s + (t - s)    == t,                        "[transitive]")
    }}
  }

  /**
   * Checks that `ℤ` acts upon the carrier set `S`; that is, `apply` effects a
   * group homomorphism from `ℤ into `Sym(S)`.
   */
  def isℤSet[S]()(implicit α: Action[S,ℤ],β: Arbitrary[S],γ: Arbitrary[ℤ]): Unit =
  {
    isAction[S,ℤ]()                                      // Action axioms

    forAll("s") {(s: S) ⇒                                // ∀ s ∈ S
    {
      assert(s.♭ == s - 1,                               "[flat]")
      assert(s.♮ == s    ,                               "[natural]")
      assert(s.♯ == s + 1,                               "[sharp]")
    }}
  }

  /**
   * Checks that `ℤ` acts regularly upon the carrier set `S`; that is, `apply`
   * effects a group homomorphism from `ℤ into `Sym(S)`.
   */
  def isℤTorsor[S]()(implicit α: Torsor[S,ℤ],β: Arbitrary[S],γ: Arbitrary[ℤ]): Unit =
  {
    isTorsor[S,ℤ]()                                      // Torsor axioms

    forAll("s") {(s: S) ⇒                                // ∀ s ∈ S
    {
      assert(s.♭ == s - 1,                               "[flat]")
      assert(s.♮ == s    ,                               "[natural]")
      assert(s.♯ == s + 1,                               "[sharp]")
    }}
  }

  /**
   * Checks that `f: S ⇒ T` is equivariant with respect to the actions of `G`
   * upon `S` and `T`; that is, it commutes with the action of `G` on both `S`
   * and `T`.
   *
   * @see    [[https://en.wikipedia.org/wiki/Equivariant_map Equivarient map
   *         (Wikipedia)]]
   */
  def isEquivariant[S,T,G](f: S ⇒ T)(implicit α: Action[S,G],β: Action[T,G],γ: Arbitrary[S],δ: Arbitrary[T],ε: Arbitrary[G]): Unit =
  {
    isAction[S,G]()                                      // G acts upon S
    isAction[T,G]()                                      // G acts upon T

    forAll("s","g") {(s: S,g: G) ⇒                       // ∀ s ∈ S, g ∈ G
    {
      assert(f(s) + g == f(s + g),                       "[equivarient]")
    }}
  }

  /**
   * Checks that `S` satisfies the axioms of a partial ordering; that is, the
   * relation `<=` is ''reflexive'', ''antisymmetric', and ''transitive''.
   *
   * @see    [[https://en.wikipedia.org/wiki/Partially_ordered_set Partial
   *         order (Wikipedia)]]
   */
  def isPartiallyOrdered[S]()(implicit α: PartialOrdering[S],β: Arbitrary[S]): Unit =
  {
    forAll("s","t","u") {(s: S,t: S,u: S) ⇒              // ∀ s,t,u ∈ S
    {
      assert(s<=s,                                       "[reflexive]")
      assert(s<=t && t<=s implies s==t,                  "[antisymmetric]")
      assert(s<=t && t<=u implies s<=u,                  "[transitive]")
    }}
  }

  /**
   * Checks that `S` satisfies the axioms of a total ordering; namely that it
   * is partially ordered and, moreover, that the ordering `<=` is ''total''.
   *
   * @see [[https://en.wikipedia.org/wiki/Total_order Total order (Wikipedia)]]
   */
  def isOrdered[S]()(implicit α: PartialOrdering[S],β: Arbitrary[S]): Unit =
  {
    isPartiallyOrdered[S]()                              // Check the axioms

    forAll("s","t") {(s: S,t: S) ⇒                       // ∀ s,t ∈ S
    {
      assert(s<=t || t<=s,                               "[total]")
    }}
  }

  /**
   * Defines generators for many of the core data types.
   */
  object generate
  {
    def gen[α: Choose,β](l: α,h: α,f: α ⇒ β): Gen[β] = choose(l,h) map f

    val int   = gen(-128  ,  128  ,(x: ℤ) ⇒ x)
    val real  = gen(-128.0,  128.0,(x: ℝ) ⇒ x)
    val hertz = gen(   2.0,   10.0,(x: ℝ) ⇒ Hz(Math.exp(x)))
    val pitch = gen(   0  ,  128  ,(x: ℕ) ⇒ Pitch(x))
    val note  = gen(   0  ,  128  ,(x: ℕ) ⇒ Note(Pitch(x)))
    val notes = gen(   0  ,0xFFF  ,(x: ℕ) ⇒ Notes(x))
    val shape = gen(   0  ,0xFFF  ,(x: ℕ) ⇒ Shape(x | 1))
    val scale = for(r ←note;s ←shape) yield Scale(r,s)
  }

  /**
   * Defines implicit random generators for many of the core data types.
   */
  object arbitrary
  {
    implicit val α = Arbitrary(generate.hertz)
    implicit val β = Arbitrary(generate.pitch)
    implicit val γ = Arbitrary(generate.note)
    implicit val δ = Arbitrary(generate.notes)
    implicit val ε = Arbitrary(generate.shape)
    implicit val ζ = Arbitrary(generate.scale)
  }

  /*
   * Import on the behalf of our subclasses...
   */
  val Arbitrary = org.scalacheck.Arbitrary
}

//****************************************************************************
